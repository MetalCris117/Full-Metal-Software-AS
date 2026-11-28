package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.ClienteAsegurado;

/**
 * PEREZ NAVA BRAYAN
 * Controlador Web MVC para la vista Formulario poliza..
 * Ligado a:
 * - Vistas: polizas/registro.html y componentes/notificacion.html
 * - Casos de Uso: NRM-AP, NRM-MP, EX-CNR y EX-DND
 */
@Controller
public class PolizaVistaController {

    private static final String BASE_URL = "http://localhost:8081";

    // Abre el formulario vacio desde el boton "Registrar Nueva Poliza" de inicio.html
    @GetMapping("/poliza/nueva")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("modoEdicion", false);
        return "polizas/registro";
    }

    // Procesa el formulario de Alta de Poliza
    @PostMapping("/poliza/nueva")
    public String procesarAltaPoliza(
            @RequestParam int tipo,
            @RequestParam double monto,
            @RequestParam String descripcion,
            @RequestParam String curp,
            Model model) {

        String curpLimpia = curp.trim().toUpperCase();
        // generamos un UUID para la nueva poliza
        UUID clave = UUID.randomUUID();

        try {
            // Caso excepcional: la CURP no es de ningun cliente
            if (!clienteExiste(curpLimpia)) {
                cargarErrorClienteNoRegistrado(model);
                return "componentes/notificacion";
            }

            // Uso de POST de PolizaController 
            RestClient.create().post()
                    .uri(BASE_URL + "/poliza/{clave}/{tipo}/{monto}/{descripcion}/{curp}",
                            clave, tipo, monto, limpiarDescripcion(descripcion), curpLimpia)
                    .retrieve()
                    .toBodilessEntity();

            model.addAttribute("esExito", true);
            model.addAttribute("tituloPestana", "Póliza registrada");
            model.addAttribute("etiqueta", "PÓLIZA REGISTRADA");
            model.addAttribute("titulo", "La póliza se registró correctamente");
            model.addAttribute("mensaje", "La clave de la póliza es " + clave
                    + ". Guárdala para consultarla o modificarla después.");
            return "componentes/notificacion";

        } catch (Exception e) {
            // Caso excepcional: Sistema Dueño no disponible
            cargarErrorSistemaNoDisponible(model, "/poliza/nueva");
            return "componentes/notificacion";
        }
    }

    // Pregunta al GET /cliente/{curp} de ClienteController si la CURP esta registrada
    private boolean clienteExiste(String curp) {
        try {
            ClienteAsegurado cliente = RestClient.create().get()
                    .uri(BASE_URL + "/cliente/{curp}", curp)
                    .retrieve()
                    .body(ClienteAsegurado.class);
            return cliente != null && cliente.getCurp() != null;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    // ajustamos la descripcion para que no tenga caracteres que rompan la URL por ejemplos "/" o "\"
    private String limpiarDescripcion(String descripcion) {
        return descripcion.trim().replace("/", "-").replace("\\", "-");
    }

    private void cargarErrorClienteNoRegistrado(Model model) {
        model.addAttribute("esExito", false);
        model.addAttribute("tituloPestana", "Cliente no encontrado");
        model.addAttribute("codigo", "404");
        model.addAttribute("etiqueta", "CLIENTE NO REGISTRADO");
        model.addAttribute("titulo", "La CURP no pertenece a ningún cliente");
        model.addAttribute("mensaje", "Para darle una póliza a alguien primero tiene que estar registrado como cliente.");
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
        model.addAttribute("textoSecundaria", "Intentar de nuevo");
        model.addAttribute("pieNotificacion", "Gracias por tu paciencia.");
    }
}