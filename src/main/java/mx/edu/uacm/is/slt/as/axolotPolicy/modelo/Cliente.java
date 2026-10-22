package mx.edu.uacm.is.slt.as.axolotPolicy.modelo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public class Cliente {
    @Id
    private String curp;
    private Date fecha_nacimiento;
    private String nombres;
    private String primer_apellido;
    private String segundo_apellido;
    private String direccion;

    public Cliente() {
    }

    public Cliente(String curp, Date fecha_nacimiento, String nombres, String primer_apellido, String segundo_apellido, String direccion) {
        this.curp = curp;
        this.fecha_nacimiento = fecha_nacimiento;
        this.nombres = nombres;
        this.primer_apellido = primer_apellido;
        this.segundo_apellido = segundo_apellido;
        this.direccion = direccion;
    }
    public String getCurp() {
        return curp;
    }
    public void setCurp(String curp) {
        this.curp = curp;
    }
    public Date getFecha_nacimiento() {
        return fecha_nacimiento;    
    }
    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
    public String getNombres() {
        return nombres;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    public String getPrimer_apellido() {
        return primer_apellido;
    }
    public void setPrimer_apellido(String primer_apellido) {
        this.primer_apellido = primer_apellido;
    }
    public String getSegundo_apellido() {
        return segundo_apellido;
    }
    public void setSegundo_apellido(String segundo_apellido) {
        this.segundo_apellido = segundo_apellido;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof Cliente) {
            Cliente otroCliente = (Cliente) obj;
            return curp.equalsIgnoreCase(otroCliente.curp);
        }
        return false;
    }
    @Override 
    public int hashCode() {
        return Objects.hash(curp, fecha_nacimiento, nombres, primer_apellido, segundo_apellido, direccion);
    }

}
