package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.ClienteAsegurado;

/**
 * CRUZ ROLDAN ERIK
 * Controlador Web MVC para las vistas de gestion de cliente y notificaciones.
 * Ligado a:
 * - Vistas: usuarios/registro.html y componentes/notificacion.html
 * - ERS: Req. 1, Req. 5, Req. 10, Req. 11, Req. 17 y Req. 19
 * - Casos de Uso: NRM-AC, NRM-MC, EX-DND, EX-CNR y EX-CYR
 */
@Controller
public class ClienteVistaController {

    private static final String BASE_URL = "http://localhost:8081";

    // Abre la vista de Alta de Cliente desde el boton de inicio.html (NRM-AC)
    @GetMapping("/cliente/nuevo")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("modoEdicion", false);
        model.addAttribute("cliente", new ClienteAsegurado());
        return "usuarios/registro";
    }

    // Procesa el formulario de Alta de Cliente (NRM-AC / EX-CYR / EX-DND)
    @PostMapping("/cliente/nuevo")
    public String procesarAltaCliente(
            @RequestParam String curp,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNacimiento,
            @RequestParam String nombres,
            @RequestParam String primerApellido,
            @RequestParam(required = false) String segundoApellido,
            @RequestParam String direccion,
            Model model) {

        ClienteAsegurado nuevo = new ClienteAsegurado(
                curp.trim().toUpperCase(),
                nombres.trim(),
                primerApellido.trim(),
                (segundoApellido != null && !segundoApellido.isBlank()) ? segundoApellido.trim() : null,
                direccion.trim(),
                fechaNacimiento);

        try {
            RestClient.create().post()
                    .uri(BASE_URL + "/cliente")
                    .body(nuevo)
                    .retrieve()
                    .body(ClienteAsegurado.class);

            cargarOperacionExitosa(model);
            return "componentes/notificacion";

        } catch (HttpClientErrorException.Conflict | HttpClientErrorException.BadRequest e) {
            // Caso excepcional EX-CYR: El cliente ya esta registrado
            model.addAttribute("esExito", false);
            model.addAttribute("tituloPestana", "Cliente ya registrado");
            model.addAttribute("codigo", "409");
            model.addAttribute("etiqueta", "CLIENTE YA REGISTRADO");
            model.addAttribute("titulo", "El cliente ya cuenta con un registro");
            model.addAttribute("mensaje", "Ya existe un cliente registrado con la CURP proporcionada en el Sistema Dueño.");
            model.addAttribute("urlSecundaria", "/cliente/nuevo");
            model.addAttribute("textoSecundaria", "Intentar de nuevo");
            model.addAttribute("pieNotificacion", "Axolotl Policy · Gestión de seguros");
            return "componentes/notificacion";

        } catch (Exception e) {
            // Caso excepcional EX-DND: Sistema Dueño no disponible (Req. 10)
            cargarErrorSistemaNoDisponible(model, "/cliente/nuevo");
            return "componentes/notificacion";
        }
    }

    // Abre la vista de Modificar Cliente desde el boton de inicio.html (NRM-MC)
    @GetMapping("/cliente/modificar")
    public String mostrarFormularioModificar(Model model) {
        model.addAttribute("modoEdicion", true);
        model.addAttribute("cliente", new ClienteAsegurado());
        return "usuarios/registro";
    }

    // Busca el cliente por CURP antes de editar (NRM-MC / EX-CNR / EX-DND)
    @GetMapping("/cliente/modificar/buscar")
    public String buscarClienteParaModificar(@RequestParam String curp, Model model) {
        String curpLimpia = curp.trim().toUpperCase();
        try {
            ClienteAsegurado cliente = RestClient.create().get()
                    .uri(BASE_URL + "/cliente/{curp}", curpLimpia)
                    .retrieve()
                    .body(ClienteAsegurado.class);

            if (cliente == null || cliente.getCurp() == null) {
                cargarErrorClienteNoRegistrado(model);
                return "componentes/notificacion";
            }

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            formato.setTimeZone(TimeZone.getTimeZone("UTC"));

            model.addAttribute("modoEdicion", true);
            model.addAttribute("curpBuscada", curpLimpia);
            model.addAttribute("cliente", cliente);
            model.addAttribute("fechaFormateada",
                    cliente.getFechaNacimiento() != null ? formato.format(cliente.getFechaNacimiento()) : "");
            return "usuarios/registro";

        } catch (HttpClientErrorException.NotFound e) {
            // Caso excepcional EX-CNR: Cliente no registrado (Req. 11)
            cargarErrorClienteNoRegistrado(model);
            return "componentes/notificacion";

        } catch (Exception e) {
            // Caso excepcional EX-DND: Sistema Dueño no disponible (Req. 10)
            cargarErrorSistemaNoDisponible(model, "/cliente/modificar");
            return "componentes/notificacion";
        }
    }

    // Envia la actualizacion del cliente al PUT de ClienteController (NRM-MC)
    @PostMapping("/cliente/modificar")
    public String procesarModificacionCliente(
            @RequestParam String curp,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNacimiento,
            @RequestParam String nombres,
            @RequestParam String primerApellido,
            @RequestParam(required = false) String segundoApellido,
            @RequestParam String direccion,
            Model model) {

        String curpLimpia = curp.trim().toUpperCase();
        ClienteAsegurado actualizado = new ClienteAsegurado(
                curpLimpia,
                nombres.trim(),
                primerApellido.trim(),
                (segundoApellido != null && !segundoApellido.isBlank()) ? segundoApellido.trim() : null,
                direccion.trim(),
                fechaNacimiento);

        try {
            RestClient.create().put()
                    .uri(BASE_URL + "/cliente/{curp}", curpLimpia)
                    .body(actualizado)
                    .retrieve()
                    .body(ClienteAsegurado.class);

            cargarOperacionExitosa(model);
            return "componentes/notificacion";

        } catch (HttpClientErrorException.NotFound e) {
            cargarErrorClienteNoRegistrado(model);
            return "componentes/notificacion";

        } catch (Exception e) {
            cargarErrorSistemaNoDisponible(model, "/cliente/modificar");
            return "componentes/notificacion";
        }
    }

    private void cargarOperacionExitosa(Model model) {
        model.addAttribute("esExito", true);
        model.addAttribute("tituloPestana", "Operación exitosa");
        model.addAttribute("etiqueta", "OPERACIÓN EXITOSA");
        model.addAttribute("titulo", "Operación realizada correctamente");
        model.addAttribute("mensaje", "Los cambios se han guardado y sincronizado con el Sistema Dueño.");
    }

    private void cargarErrorClienteNoRegistrado(Model model) {
        model.addAttribute("esExito", false);
        model.addAttribute("tituloPestana", "Cliente no encontrado");
        model.addAttribute("codigo", "404");
        model.addAttribute("etiqueta", "REGISTRO NO ENCONTRADO");
        model.addAttribute("titulo", "Cliente no registrado en el sistema");
        model.addAttribute("mensaje", "No se encontró ningún cliente asegurado con la CURP ingresada. Verifica tus datos o realiza tu registro.");
        model.addAttribute("urlSecundaria", "/cliente/nuevo");
        model.addAttribute("textoSecundaria", "Registrar Cliente");
        model.addAttribute("pieNotificacion", "Axolotl Policy · Gestión de seguros");
    }

    private void cargarErrorSistemaNoDisponible(Model model, String urlReintento) {
        model.addAttribute("esExito", false);
        model.addAttribute("tituloPestana", "Servidor no disponible");
        model.addAttribute("codigo", "503");
        model.addAttribute("etiqueta", "INTERRUPCIÓN TEMPORAL");
        model.addAttribute("titulo", "El servidor de pólizas no está disponible en este momento");
        model.addAttribute("mensaje", "No podemos conectar con el servidor en este momento. Por favor, inténtalo de nuevo más tarde.");
        model.addAttribute("urlSecundaria", urlReintento);
        model.addAttribute("textoSecundaria", "Recargar página");
        model.addAttribute("pieNotificacion", "Gracias por tu paciencia.");
    }
}