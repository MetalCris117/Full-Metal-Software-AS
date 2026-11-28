package mx.edu.uacm.is.slt.as.axolotPolicy.modelo;

import java.util.Objects;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity 
public class BeneficiarioPoliza {
    
    @EmbeddedId 
    private IdBeneficiarioPoliza id;
    private byte porcentaje;

    public BeneficiarioPoliza() {}

    public BeneficiarioPoliza(IdBeneficiarioPoliza id, byte porcentaje) {
        this.id = id;
        this.porcentaje = porcentaje;
    }

    public IdBeneficiarioPoliza getId() {
        return id;
    }

    public void setId(IdBeneficiarioPoliza id) {
        this.id = id;
    }

    public byte getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(byte porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BeneficiarioPoliza that)) return false;
        return Objects.equals(id, that.id);
    }

}
