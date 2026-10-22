package mx.edu.uacm.is.slt.as.axolotPolicy.modelo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public class Cliente {
    @Id
    private String curp;
    private Date fechaNacimiento;
    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private String direccion;

    public Cliente() {
    }

    public Cliente(String curp, Date fechaNacimiento, String nombres, String primerApellido, String segundoApellido, String direccion) {
        this.curp = curp;
        this.fechaNacimiento = fechaNacimiento;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.direccion = direccion;
    }
    public String getCurp() {
        return curp;
    }
    public void setCurp(String curp) {
        this.curp = curp;
    }
    public Date getfechaNacimiento() {
        return fechaNacimiento;    
    }
    public void setfechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getNombres() {
        return nombres;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    public String getprimerApellido() {
        return primerApellido;
    }
    public void setprimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }
    public String getsegundoApellido() {
        return segundoApellido;
    }
    public void setsegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
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
        return Objects.hash(curp, fechaNacimiento, nombres, primerApellido, segundoApellido, direccion);
    }

}
