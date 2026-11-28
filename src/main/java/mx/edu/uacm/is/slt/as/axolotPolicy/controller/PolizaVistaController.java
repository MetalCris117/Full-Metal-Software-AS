package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.BeneficiarioPoliza;
import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.ClienteAsegurado;
import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.Poliza;

@Controller
public class PolizaVistaController {

    private final PolizaController polizaController;
    private static final String BASE_URL = "http://localhost:8081";

    public PolizaVistaController(PolizaController polizaController) {
        this.polizaController = polizaController;
    }

    //Abre la interfaz que muestra las polizas disponibles del sitema dueño
    @GetMapping("/listaPolizas")
    public String mostrarListadoPolizas(@RequestParam(value = "query", required = false) String query, Model model) {

        List<Poliza> polizas = new ArrayList<>();

        try {
            List<Poliza> resultado = polizaController.getAll();
            if (resultado != null) {
                polizas = resultado;
            }
        } catch (Exception e) {
            // si el sitema dueño no esta disponible se muestra una notificacion
            cargarErrorSistemaNoDisponible(model, "/listaPolizas");
            return "componentes/notificacion";
        }

        if (query != null && !query.isBlank()) {
            String filtro = normalizar(query);

            //buscamos por tipo de poliza primero
            if (filtro.equals("auto") || filtro.equals("vida") || filtro.equals("medico")) {
                polizas = polizas.stream()
                        .filter(p -> convertirTipo(p.getTipo()).equals(filtro))
                        .toList();
            } else {
                // busca por clave o descripcion o curp
                polizas = polizas.stream()
                        .filter(p -> (p.getClave() != null && normalizar(p.getClave().toString()).contains(filtro))
                                || normalizar(p.getDescripcion()).contains(filtro)
                                || normalizar(p.getCurp()).contains(filtro))
                        .toList();
            }
        }

        model.addAttribute("polizas", polizas);
        model.addAttribute("query", query);
        model.addAttribute("conectado", true);

        return "polizas/listadoPolizas";
    }

    private String convertirTipo(int tipo) {
        return switch (tipo) {
            case 0 -> "auto";
            case 1 -> "vida";
            case 2 -> "medico";
            default -> String.valueOf(tipo);
        };
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }


/**
 * PEREZ NAVA BRAYAN
 * Controlador Web MVC para la vista Formulario poliza..
 * Ligado a:
 * - Vistas: polizas/formulario.html y componentes/notificacion.html
 * - Casos de Uso: NRM-AP, NRM-MP, EX-CNR y EX-DND
 */

    // Abre el formulario vacio desde el boton "Registrar Nueva Poliza" de inicio.html
    @GetMapping("/poliza/nueva")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("modoEdicion", false);
        return "polizas/formulario";
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

    // Muestra la vista de Gestión de Póliza al hacer clic en "Gestionar" desde listadoPolizas.html
    @GetMapping("/polizas/gestionar/{clave}")
    public String mostrarGestionPoliza(@PathVariable UUID clave, Model model) {
        try {
            RestClient restClient = RestClient.create();

            // Obtiene la póliza por su clave
            Poliza poliza = restClient.get()
                    .uri(BASE_URL + "/poliza/{clave}", clave)
                    .retrieve()
                    .body(Poliza.class);

            if (poliza == null) {
                model.addAttribute("esExito", false);
                model.addAttribute("tituloPestana", "Póliza no encontrada");
                model.addAttribute("codigo", "404");
                model.addAttribute("etiqueta", "PÓLIZA NO ENCONTRADA");
                model.addAttribute("titulo", "No se encontró la póliza solicitada");
                model.addAttribute("mensaje", "Verifica que la clave de la póliza sea correcta o que no haya sido eliminada.");
                model.addAttribute("urlSecundaria", "/listaPolizas");
                model.addAttribute("textoSecundaria", "Volver a Mis Pólizas");
                model.addAttribute("pieNotificacion", "Axolotl Policy · Gestión de seguros");
                return "componentes/notificacion";
            }

            // Obtiene los beneficiarios asociados a la póliza
            List<BeneficiarioPoliza> beneficiarios = new ArrayList<>();
            try {
                List<BeneficiarioPoliza> todos = restClient.get()
                        .uri(BASE_URL + "/beneficiarios")
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});
                if (todos != null) {
                    beneficiarios = todos.stream()
                            .filter(b -> b.getId() != null && clave.equals(b.getId().getClavePoliza()))
                            .toList();
                }
            } catch (Exception ignored) {
            }

            // Sumar el porcentaje de participación asignado
            int porcentajeTotal = 0;
            for (BeneficiarioPoliza b : beneficiarios) {
                porcentajeTotal += b.getPorcentaje();
            }

            model.addAttribute("poliza", poliza);
            model.addAttribute("beneficiarios", beneficiarios);
            model.addAttribute("porcentajeTotal", porcentajeTotal);
            model.addAttribute("conectado", true);

            return "polizas/gestionPoliza";

        } catch (Exception e) {
            cargarErrorSistemaNoDisponible(model, "/polizas/gestionar/" + clave);
            return "componentes/notificacion";
        }
    }

    // Eliminar Póliza
    @PostMapping("/polizas/eliminar/{clave}")
    public String eliminarPoliza(@PathVariable UUID clave, Model model) {
        try {
            RestClient.create().delete()
                    .uri(BASE_URL + "/poliza/{clave}", clave)
                    .retrieve()
                    .toBodilessEntity();

            model.addAttribute("esExito", true);
            model.addAttribute("tituloPestana", "Póliza eliminada");
            model.addAttribute("etiqueta", "PÓLIZA ELIMINADA");
            model.addAttribute("titulo", "La póliza se eliminó correctamente");
            model.addAttribute("mensaje", "La póliza con clave " + clave + " fue eliminada del sistema.");
            return "componentes/notificacion";

        } catch (Exception e) {
            cargarErrorSistemaNoDisponible(model, "/polizas/gestionar/" + clave);
            return "componentes/notificacion";
        }
    }

    // Abre formulario.html en modo edición (desde Gestionar Póliza o desde Inicio)
    @GetMapping("/poliza/editar")
    public String mostrarFormularioEdicion(
            @RequestParam(value = "clave", required = false) String clave,
            Model model) {

        model.addAttribute("modoEdicion", true);
        model.addAttribute("claveBuscada", clave);

        if (clave == null || clave.isBlank()) {
            return "polizas/formulario";
        }

        try {
            UUID uuidClave = UUID.fromString(clave.trim());
            Poliza poliza = RestClient.create().get()
                    .uri(BASE_URL + "/poliza/{clave}", uuidClave)
                    .retrieve()
                    .body(Poliza.class);

            if (poliza == null) {
                model.addAttribute("esExito", false);
                model.addAttribute("tituloPestana", "Póliza no encontrada");
                model.addAttribute("codigo", "404");
                model.addAttribute("etiqueta", "PÓLIZA NO ENCONTRADA");
                model.addAttribute("titulo", "No se encontró ninguna póliza con esa clave");
                model.addAttribute("mensaje", "Verifica que la clave de 36 caracteres sea correcta.");
                model.addAttribute("urlSecundaria", "/poliza/editar");
                model.addAttribute("textoSecundaria", "Buscar otra clave");
                model.addAttribute("pieNotificacion", "Axolotl Policy · Gestión de seguros");
                return "componentes/notificacion";
            }

            model.addAttribute("poliza", poliza);
            model.addAttribute("montoFormateado", String.format(Locale.US, "%.2f", poliza.getMonto()));
            return "polizas/formulario";

        } catch (IllegalArgumentException e) {
            model.addAttribute("esExito", false);
            model.addAttribute("tituloPestana", "Clave inválida");
            model.addAttribute("codigo", "400");
            model.addAttribute("etiqueta", "FORMATO INVÁLIDO");
            model.addAttribute("titulo", "La clave ingresada no tiene formato válido");
            model.addAttribute("mensaje", "La clave de la póliza debe ser un UUID válido de 36 caracteres.");
            model.addAttribute("urlSecundaria", "/poliza/editar");
            model.addAttribute("textoSecundaria", "Intentar de nuevo");
            model.addAttribute("pieNotificacion", "Axolotl Policy · Gestión de seguros");
            return "componentes/notificacion";
        } catch (Exception e) {
            cargarErrorSistemaNoDisponible(model, "/poliza/editar?clave=" + clave);
            return "componentes/notificacion";
        }
    }

    // Procesa el envío del formulario de edición (PUT)
    @PostMapping("/poliza/editar")
    public String procesarEdicionPoliza(
            @RequestParam UUID clave,
            @RequestParam String curp,
            @RequestParam int tipo,
            @RequestParam double monto,
            @RequestParam String descripcion,
            Model model) {

        String curpLimpia = curp.trim().toUpperCase();

        try {
            if (!clienteExiste(curpLimpia)) {
                cargarErrorClienteNoRegistrado(model);
                return "componentes/notificacion";
            }

            RestClient.create().put()
                    .uri(BASE_URL + "/poliza/{clave}/{tipo}/{monto}/{descripcion}/{curp}",
                            clave, tipo, monto, limpiarDescripcion(descripcion), curpLimpia)
                    .retrieve()
                    .toBodilessEntity();

            model.addAttribute("esExito", true);
            model.addAttribute("tituloPestana", "Póliza actualizada");
            model.addAttribute("etiqueta", "PÓLIZA ACTUALIZADA");
            model.addAttribute("titulo", "Los datos de la póliza se actualizaron correctamente");
            model.addAttribute("mensaje", "La póliza con clave " + clave + " fue modificada en el sistema.");
            return "componentes/notificacion";

        } catch (Exception e) {
            cargarErrorSistemaNoDisponible(model, "/poliza/editar?clave=" + clave);
            return "componentes/notificacion";
        }
    }
}