package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.ClienteAsegurado;

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

    // POST - Registro de clientes
    @PostMapping("/cliente")
    public ClienteAsegurado registrarCliente(@RequestBody ClienteAsegurado nuevoCliente) {

        RestClient clienteRest = RestClient.create();

        // Valida la nulidad y fijar zona horaria UTC para evitar el desfase de 1 día
        String fecha = "";
        if (nuevoCliente.getFechaNacimiento() != null) {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            formatoFecha.setTimeZone(TimeZone.getTimeZone("UTC"));
            fecha = formatoFecha.format(nuevoCliente.getFechaNacimiento());
        }

        // Construye la URL
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString("http://nachintoch.mx:8080")
                .pathSegment(
                        "cliente",
                        nuevoCliente.getCurp(),
                        nuevoCliente.getDireccion(),
                        fecha,
                        nuevoCliente.getNombres(),
                        nuevoCliente.getPrimerApellido()
                );

        // Agregar el segundo apellido solo si existe y no esta en blanco
        if (nuevoCliente.getSegundoApellido() != null && !nuevoCliente.getSegundoApellido().isBlank()) {
            builder.pathSegment(nuevoCliente.getSegundoApellido());
        }

        URI direccion = builder.build().encode().toUri();

        // Enviar petición POST al sistema dueño
        return clienteRest.post()
                .uri(direccion)
                .retrieve()
                .body(ClienteAsegurado.class);
    }

    //ERIK R5 y R8 - NRM-MC
    // Responde a una petición PUT para actualizar los datos de un cliente por su CURP
    // Por ejemplo, en http://localhost:8081/cliente/ABC123 con los datos nuevos en JSON
    @PutMapping("/cliente/{curp}")
    public ClienteAsegurado updateCliente(@PathVariable String curp,
            @RequestBody ClienteAsegurado clienteActualizado) {
        RestClient clienteRest = RestClient.create();

        // La fecha va como texto aaaa-MM-dd, en UTC para que no se recorra un dia
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        formatoFecha.setTimeZone(TimeZone.getTimeZone("UTC"));
        String fecha = formatoFecha.format(clienteActualizado.getFechaNacimiento());

        // El sistema dueño recibe los datos dentro de la direccion
        UriComponentsBuilder ruta = UriComponentsBuilder
                .fromUriString("http://nachintoch.mx:8080")
                .pathSegment("cliente", curp, clienteActualizado.getDireccion(), fecha,
                        clienteActualizado.getNombres(), clienteActualizado.getPrimerApellido());

        // El segundo apellido solo se agrega si existe
        String segundo = clienteActualizado.getSegundoApellido();
        if (segundo != null && !segundo.isBlank()) {
            ruta.pathSegment(segundo);
        }

        return clienteRest.put()
                .uri(ruta.build().encode().toUri())
                .retrieve()
                .body(ClienteAsegurado.class);
    }
}