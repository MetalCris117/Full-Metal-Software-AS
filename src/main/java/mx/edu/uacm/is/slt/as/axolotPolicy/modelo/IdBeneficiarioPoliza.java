package mx.edu.uacm.is.slt.as.axolotPolicy.modelo;

import java.sql.Date;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;

@Embeddable 
public class IdBeneficiarioPoliza {

    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private Date fechaNacimiento;
    private UUID clavePoliza;

    public IdBeneficiarioPoliza() {}

    public IdBeneficiarioPoliza(String nombres, String primerApellido,
                                String segundoApellido, Date fechaNacimiento,
                                UUID clavePoliza) {
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
        this.clavePoliza = clavePoliza;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public UUID getClavePoliza() {
        return clavePoliza;
    }

    public void setClavePoliza(UUID clavePoliza) {
        this.clavePoliza = clavePoliza;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof IdBeneficiarioPoliza that)) return false;
        return nombres.equalsIgnoreCase(that.nombres) &&
        primerApellido.equalsIgnoreCase(that.primerApellido) &&
        ((segundoApellido == null && that.segundoApellido == null) ||
            (segundoApellido != null &&
                segundoApellido.equalsIgnoreCase(
                    that.segundoApellido))) &&
        Objects.equals(fechaNacimiento, that.fechaNacimiento) &&
        Objects.equals(clavePoliza, that.clavePoliza);
    }

    @Override 
    public int hashCode(){
        return Objects.hash(nombres, primerApellido, segundoApellido,
                fechaNacimiento, clavePoliza);
    }
    
}
