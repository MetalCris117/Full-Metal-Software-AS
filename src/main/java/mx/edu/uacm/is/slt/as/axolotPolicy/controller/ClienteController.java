package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.util.List;
import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.ClienteAsegurado;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

// Indica que es un controlador REST
@RestController
public class ClienteController {

    // Responde a una petición GET que trae la CURP en la URL.
    // Por ejemplo, en http://localhost:8081/cliente/ABC123
    // el parametro curp vale ABC123
    @GetMapping("/cliente/{curp}")
    public ClienteAsegurado getOne(@PathVariable String curp) {
        // Crea el cliente REST que hace la peticion al sistema dueño
        RestClient clienteRest = RestClient.create();

        // Pide el cliente al sistema dueño
        // get() elige el tipo de peticion,
        // uri() arma la direccion poniendo la CURP en {curp},
        // retrieve() manda la peticion y espera la respuesta,
        // body() convierte el JSON recibido en un ClienteAsegurado
        ClienteAsegurado cliente = clienteRest.get()
                .uri("http://nachintoch.mx:8080/cliente/{curp}", curp)
                .retrieve()
                .body(ClienteAsegurado.class);

        // Devuelve el cliente a quien hizo la peticion
        return cliente;
    }

    // entrega lista de clientes http://localhost:8081/clientes
    // de todos los clientes del sistema dueño
    @GetMapping("/clientes")
    public List<ClienteAsegurado> getAll() {
        RestClient clienteRest = RestClient.create();

        // ParameterizedTypeReference le dice a Spring que es
        // una lista de ClienteAsegurado
        List<ClienteAsegurado> clientes = clienteRest.get()
                .uri("http://nachintoch.mx:8080/clientes")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return clientes;
    }
}