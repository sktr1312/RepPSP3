package api.api.Cliente.src.modelo;

public class Cliente {
    private int codCliente; 
    private String nombre;
    private String apellidos;
    private int codProvincia;
    private int vip;

    public Cliente(int codCliente, String nombre, String apellidos, int codProvincia, int vip) {
        this.codCliente = codCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.codProvincia = codProvincia;
        this.vip = vip;
    }

    public int getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(int codCliente) {
        this.codCliente = codCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getCodProvincia() {
        return codProvincia;
    }

    public void setCodProvincia(int codProvincia) {
        this.codProvincia = codProvincia;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    @Override
    public String toString() {
        return "===".repeat(20) + "\n\nID: " + codCliente + "\nNombre: " + nombre + "\nApellidos: " + apellidos + "\ncodProvincia: " + codProvincia
                + "\nVip: " + (vip == 1? "Si" : "No") + "\n" ;
    }

}
