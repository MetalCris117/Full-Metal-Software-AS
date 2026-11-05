package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import java.util.UUID;
import java.util.List;
import java.sql.Date;
import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.BeneficiarioPoliza;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class BeneficiarioController {

    @GetMapping({"/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}/{segundo_apellido}",
                "/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}"})
    public BeneficiarioPoliza getOne(@PathVariable ("nombres") String nombres,
                                @PathVariable ("primer_apellido") String primerApellido,
                                @PathVariable (value = "segundo_apellido", required = false) String segundoApellido,
                                @PathVariable ("fecha_nacimiento") Date fechaNacimiento,
                                @PathVariable ("clave_poliza") UUID clavePoliza) {
        
        RestClient clienteRest = RestClient.create();
        
        String url;
        if(segundoApellido != null){
            url = "http://nachintoch.mx:8080/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}/{segundo_apellido}";
            return clienteRest.get()
                .uri(url, fechaNacimiento, clavePoliza, nombres, primerApellido, segundoApellido)
                .retrieve()
                .body(BeneficiarioPoliza.class);
        }else{
            url = "http://nachintoch.mx:8080/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}";
            return clienteRest.get()
                .uri(url, fechaNacimiento, clavePoliza, nombres, primerApellido)
                .retrieve()
                .body(BeneficiarioPoliza.class);            
        }
    }

    @GetMapping ("/beneficiarios")
    public List<BeneficiarioPoliza> getAll(){

        RestClient clienteRest = RestClient.create();

        List<BeneficiarioPoliza> beneficiarios = clienteRest.get()
            .uri("http://nachintoch.mx:8080/beneficiarios")
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
        
        return beneficiarios;
    }
}
