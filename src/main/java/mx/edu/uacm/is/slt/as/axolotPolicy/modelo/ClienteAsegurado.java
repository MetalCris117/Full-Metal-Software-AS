package mx.edu.uacm.is.slt.as.axolotPolicy.modelo;

import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ClienteAsegurado {

    @Id
    private String curp;
    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private String direccion;
    private Date fechaNacimiento;

    public ClienteAsegurado() {
    }

    public ClienteAsegurado(String curp, String nombres, String primerApellido, String segundoApellido, String direccion, Date fechaNacimiento) {
        this.curp = curp;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
    }

    public ClienteAsegurado(String curp, String nombres, String primerApellido, String direccion, Date fechaNacimiento) {
        this.curp = curp;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = null;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    @Override
    public boolean equals(Object obj) {

        if (super.equals(obj)) {
            return true;
        }

        if (obj instanceof ClienteAsegurado) {
            ClienteAsegurado otroCliente = (ClienteAsegurado) obj;
            if (curp == null) {
                return otroCliente.curp == null;
            }
            return curp.equalsIgnoreCase(otroCliente.curp);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(curp, nombres, primerApellido, segundoApellido, direccion, fechaNacimiento);
    }

}