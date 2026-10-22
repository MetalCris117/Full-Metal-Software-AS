package mx.edu.uacm.is.slt.as.axolotPolicy.modelo;

import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity 
public class Poliza {

    @Id
    private UUID clave;
    private int tipo;
    private double monto;
    private String descripcion;
    private String curp;

    public Poliza(){}

    public Poliza(UUID clave,int tipo,double monto, String descripcion, String curp){
        this.clave = clave;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.curp = curp;
    }

    public UUID getclave() {
        return clave;
    }

    public void setclave(UUID clave) {
        this.clave = clave;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }


    @Override
    public boolean equals(Object obj) {

        if(super.equals(obj)){
            return true;
        }
        
        if(obj instanceof Poliza){
            Poliza otraPoliza = (Poliza)obj;
            return clave.equals(otraPoliza.clave);
        }
       return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clave,tipo,monto,descripcion,curp);
    }

    
}
