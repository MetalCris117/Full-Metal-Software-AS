package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BeneficiarioVistaController {

    @GetMapping("/registrar-beneficiario")
    public String mostrarFormularioBeneficiario() {
        return "beneficiario/registrar-beneficiario";
    }

    // Paso 4: Confirmación de registro exitoso
    @GetMapping("/beneficiario-registrado")
    public String mostrarRegistroExitoso(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "") String clavePoliza,
            Model model) {

        model.addAttribute("esExito", true);
        model.addAttribute("tituloPestana", "Beneficiario Registrado");
        model.addAttribute("etiqueta", "ALTA DE BENEFICIARIO EXITOSA");
        model.addAttribute("titulo", "Beneficiario registrado correctamente");
        model.addAttribute("mensaje", "El beneficiario " + nombre + " ha sido guardado y sincronizado con la póliza en el Sistema Dueño.");

        return "componentes/notificacion"; // Cambia si el HTML está dentro de una subcarpeta en templates/
    }

    // Flujo Excepcional EX-DND: Sistema Dueño no disponible (503)
    @GetMapping("/error-sistema-dueno")
    public String mostrarErrorSistemaDueno(
            @RequestParam(required = false) String clavePoliza,
            Model model) {

        String urlReintento = (clavePoliza != null && !clavePoliza.isBlank())
                ? "/registrar-beneficiario?clave_poliza=" + clavePoliza
                : "/registrar-beneficiario";

        model.addAttribute("esExito", false);
        model.addAttribute("codigo", "503");
        model.addAttribute("tituloPestana", "Sistema No Disponible");
        model.addAttribute("etiqueta", "INTERRUPCIÓN TEMPORAL");
        model.addAttribute("titulo", "El Sistema Dueño de pólizas no está disponible en este momento");
        model.addAttribute("mensaje", "No fue posible procesar el alta del beneficiario porque el servidor de pólizas no responde. Inténtalo de nuevo más tarde.");
        model.addAttribute("urlSecundaria", urlReintento);
        model.addAttribute("textoSecundaria", "Reintentar registro");
        model.addAttribute("pieNotificacion", "Axolotl Policy · Gestión de seguros");

        return "componentes/notificacion"; // Cambia si el HTML está dentro de una subcarpeta en templates/
    }
}