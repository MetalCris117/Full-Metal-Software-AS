package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IntefazPrincipalController {

    @GetMapping("/")
    public String mostrarPrincipal() {
        return "index"; //le dice a sprint boot que busque el archivo index.html en la carpeta templates
    }
}
