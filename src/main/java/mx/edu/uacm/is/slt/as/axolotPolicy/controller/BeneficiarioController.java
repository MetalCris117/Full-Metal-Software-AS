package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.BeneficiarioPoliza;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

@RestController
public class BeneficiarioController {

    private static final String BASE_URL = "http://nachintoch.mx:8080";

    @GetMapping({
        "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}/{segundo_apellido}",
        "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}"
    })
    public BeneficiarioPoliza getOne(
            @PathVariable("nombres") String nombres,
            @PathVariable("primer_apellido") String primerApellido,
            @PathVariable(value = "segundo_apellido", required = false) String segundoApellido,
            @PathVariable("fecha_nacimiento") Date fechaNacimiento,
            @PathVariable("clave_poliza") UUID clavePoliza) {

        RestClient clienteRest = RestClient.create();

        try {
            if (segundoApellido != null && !segundoApellido.isBlank()) {
                String url = BASE_URL + "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}/{segundo_apellido}";
                return clienteRest.get()
                        .uri(url, fechaNacimiento, clavePoliza, nombres, primerApellido, segundoApellido)
                        .retrieve()
                        .body(BeneficiarioPoliza.class);
            } else {
                String url = BASE_URL + "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}";
                return clienteRest.get()
                        .uri(url, fechaNacimiento, clavePoliza, nombres, primerApellido)
                        .retrieve()
                        .body(BeneficiarioPoliza.class);
            }
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    @GetMapping("/beneficiarios")
    public List<BeneficiarioPoliza> getAll() {
        RestClient clienteRest = RestClient.create();

        return clienteRest.get()
                .uri(BASE_URL + "/beneficiarios")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @PostMapping({
        "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{porcentaje}/{nombres}/{primer_apellido}/{segundo_apellido}",
        "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{porcentaje}/{nombres}/{primer_apellido}"
    })
    public ResponseEntity<?> registrar(
            @PathVariable("fecha_nacimiento") Date fechaNacimiento,
            @PathVariable("clave_poliza") String clavePolizaStr,
            @PathVariable("porcentaje") byte porcentaje,
            @PathVariable("nombres") String nombres,
            @PathVariable("primer_apellido") String primerApellido,
            @PathVariable(value = "segundo_apellido", required = false) String segundoApellido) {

        UUID clavePoliza;
        try {
            clavePoliza = UUID.fromString(clavePolizaStr.trim());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Póliza inválida: el formato de la clave debe ser un UUID."));
        }

        if (porcentaje < 1 || porcentaje > 100) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El porcentaje debe ser un número entero entre 1% y 100%."));
        }

        try {
            // 1. Verificar duplicado en la misma póliza (EX-BYR)
            BeneficiarioPoliza existente = getOne(nombres, primerApellido, segundoApellido, fechaNacimiento, clavePoliza);
            if (existente != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("mensaje", "El beneficiario ya está registrado en esta póliza."));
            }

            // 2. Verificar que la suma de porcentajes no exceda 100%
            List<BeneficiarioPoliza> todos = getAll();
            int porcentajeActual = todos.stream()
                    .filter(b -> b.getId() != null && clavePoliza.equals(b.getId().getClavePoliza()))
                    .mapToInt(BeneficiarioPoliza::getPorcentaje)
                    .sum();

            if (porcentajeActual + porcentaje > 100) {
                int disponible = Math.max(0, 100 - porcentajeActual);
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "La suma excede el 100%. Ya hay " + porcentajeActual + "% asignado (disponible: " + disponible + "%)."));
            }

            // 3. Enviar alta al Sistema Dueño
            RestClient clienteRest = RestClient.create();
            BeneficiarioPoliza creado;

            if (segundoApellido != null && !segundoApellido.isBlank()) {
                String url = BASE_URL + "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{porcentaje}/{nombres}/{primer_apellido}/{segundo_apellido}";
                creado = clienteRest.post()
                        .uri(url, fechaNacimiento, clavePoliza, porcentaje, nombres, primerApellido, segundoApellido)
                        .retrieve()
                        .body(BeneficiarioPoliza.class);
            } else {
                String url = BASE_URL + "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{porcentaje}/{nombres}/{primer_apellido}";
                creado = clienteRest.post()
                        .uri(url, fechaNacimiento, clavePoliza, porcentaje, nombres, primerApellido)
                        .retrieve()
                        .body(BeneficiarioPoliza.class);
            }

            return ResponseEntity.ok(creado);

        } catch (RestClientResponseException e) {
            // El Sistema Dueño respondió rechazando el registro (póliza inexistente / inválida)
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Póliza inválida: la clave de póliza ingresada no existe en el sistema."));
        } catch (ResourceAccessException e) {
            // Solo cuando no hay conexión física con el Sistema Dueño (EX-DND)
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("mensaje", "El Sistema Dueño no está disponible en este momento."));
        }
    }
}