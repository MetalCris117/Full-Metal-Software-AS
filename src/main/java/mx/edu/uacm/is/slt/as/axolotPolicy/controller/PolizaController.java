package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

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
                .body(new ParameterizedTypeReference<>(){
                });
    
    }

    /**
     * ERIK R4, R8 y R15 - PRM-GP / PRN-GP
     * Realiza una peticion GET al sistema dueño
     * para devolver todas las polizas del cliente con el CURP dado.
     * Ruta: http://localhost:8081/polizas/cliente/{curp}
     * @param curp CURP del cliente asegurado
     * @return lista de polizas asociadas al cliente con esa CURP
     */
    @GetMapping("/polizas/cliente/{curp}")
    public List<Poliza> getPolizasPorCurp(@PathVariable String curp) {

        RestClient clienteRestPoliza = RestClient.create();

        return clienteRestPoliza.get()
                .uri("http://nachintoch.mx:8080/polizas/cliente/{curp}", curp)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
