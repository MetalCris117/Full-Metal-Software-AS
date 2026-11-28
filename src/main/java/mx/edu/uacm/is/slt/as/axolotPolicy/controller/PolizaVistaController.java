package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.Poliza;

@Controller
public class PolizaVistaController {

    private final PolizaController polizaController;

    public PolizaVistaController(PolizaController polizaController) {
        this.polizaController = polizaController;
    }

    @GetMapping("/listaPolizas")
    public String getMethodName(@RequestParam(value = "query", required = false) String query, Model model) {

        List<Poliza> polizas = new ArrayList<>();
        boolean conectado = true;

        try {
            List<Poliza> resultado = polizaController.getAll();
            if (resultado != null) {
                polizas = resultado;
            }
        } catch (Exception e) {
            conectado = false;
        }

        if (query != null && !query.isBlank()) {
            String filtro = normalizar(query);

            //buscamos por tipo de poliza primero
            if (filtro.equals("auto") || filtro.equals("vida") || filtro.equals("medico")) {
                polizas = polizas.stream()
                        .filter(p -> convertirTipo(p.getTipo()).equals(filtro))
                        .toList();
            } else {
                // busca por clave o descripcion o currp
                polizas = polizas.stream()
                        .filter(p -> (p.getClave() != null && normalizar(p.getClave().toString()).contains(filtro))
                                || normalizar(p.getDescripcion()).contains(filtro)
                                || normalizar(p.getCurp()).contains(filtro))
                        .toList();
            }
        }

        model.addAttribute("polizas", polizas);
        model.addAttribute("query", query);
        model.addAttribute("conectado", conectado);

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
}