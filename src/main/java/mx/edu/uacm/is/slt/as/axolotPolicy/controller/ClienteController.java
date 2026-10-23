package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.ClienteAsegurado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController // sirve para indicar que es un controlador de tipo REST
public class ClienteController {

    @GetMapping("/cliente/{curp}") // sirve para indicar que es un metodo que responde a una peticion GET
    // y que recibe un parametro en la URL. por ejemplo, si la URL es
    // http://localhost:8080/cliente/ABC123,
    // el parámetro curp tendra el valor ABC123
    public ClienteAsegurado getOne(@PathVariable String curp) {// sirve para indicar que el parámetro curp
        // se obtiene de la URL
        RestClient clienteRest = RestClient.create();// sirve para crear un cliente REST que se usara para hacer
        // la peticion al servicio externo
        ClienteAsegurado cliente = clienteRest.get() // sirve para hacer una peticion GET al servicio externo,
                // en este caso a la URL http://nachintoch.mx:8080/cliente/{curp}
                .uri("http://nachintoch.mx:8080/cliente/{curp}", curp) // sirve para indicar la URL del servicio
                // externo y el parametro curp que se obtiene de la URL
                .retrieve() // sirve para indicar que se quiere obtener la respuesta del servicio externo
                .body(ClienteAsegurado.class);// sirve para indicar que se quiere obtener el cuerpo de la respuesta
        // y que se quiere mapear a un objeto
        return cliente; // sirve para devolver el objeto cliente al cliente que hizo la peticion GET
    }
}