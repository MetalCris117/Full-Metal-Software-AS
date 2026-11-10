package mx.edu.uacm.is.slt.as.axolotPolicy.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import mx.edu.uacm.is.slt.as.axolotPolicy.modelo.Poliza;

@RestController 
public class PolizaController {


    /**
     *Realiza una peticion GET al sistema dueño
     *para recuperar la lista de polizas
     *la ruta http://localhost:8081/polizas muestra 
     *todas las polizas.
     * @return lista con todas las polizas registradas
     */
    @GetMapping("/polizas")      
    public List<Poliza> getAll(){

        RestClient clienteRestPoliza = RestClient.create();

        return clienteRestPoliza.get()
                .uri("http://nachintoch.mx:8080/polizas")
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});
    
    }

   /**Realiza una peticion GET al sistema dueño 
    * para obtener las polizas de un beneficiario 
    * @return lista de polizas de un beneficiario
    */
    @GetMapping({"/polizas/b/{nombres}/{primer_apellido}"
    ,"/polizas/b/{nombres}/{primer_apellido}/{segundo_apellido}"}) 
    public List<Poliza> getPolizasBeneficiario(
        @PathVariable(value = "nombres") String nombre,
        @PathVariable(value = "primer_apellido") String primerApellido,
        @PathVariable(value = "segundo_apellido",
        required = false) String segundoApellido){

            RestClient clienteRestPBeneficiario = RestClient.create();

            //checamos si segundoApellido no es null
            String uriDestino = (segundoApellido != null)
            ? "http://nachintoch.mx:8080/polizas/b/{nombres}/{primer_apellido}/{segundo_apellido}"
            : "http://nachintoch.mx:8080/polizas/b/{nombres}/{primer_apellido}";
            //reviza primero si segundoApellido es distinto de null
            //para realizar peticion get
            List<Poliza> listaPolizasBeneficiario = (segundoApellido != null)
                    ? (clienteRestPBeneficiario.get()
                    .uri(uriDestino,nombre, primerApellido, segundoApellido)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>(){}))
                    // en el caso de que segundo apellido sea null
                    // se hace la peticion get si el parametro segundoApeliido
                    : (clienteRestPBeneficiario.get()
                    .uri(uriDestino , nombre , primerApellido)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>(){}));

            if(listaPolizasBeneficiario == null
                 || listaPolizasBeneficiario.isEmpty()){
                return new ArrayList<>();
            }
            return  listaPolizasBeneficiario;
    }
}
