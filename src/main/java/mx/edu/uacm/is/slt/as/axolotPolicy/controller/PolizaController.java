package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import java.util.List;
import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.Poliza;

@RestController 
public class PolizaController {


    /**
     *Realiza una peticion GET al sistema dueño
     *para recuperar la lista de polizas
     *la ruta http://localhost:8081/Polizas muestra 
     *todas las polizas.
     * @return lista con todas las polizas registradas
     */
    @GetMapping("/Polizas")      
    public List<Poliza> getAll(){

        RestClient clienteRestPoliza = RestClient.create();

        return clienteRestPoliza.get()
                .uri("http://nachintoch.mx:8080/polizas")
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});
    
    }
}
