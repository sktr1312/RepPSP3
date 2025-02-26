package empresa;

public class Jefe extends Personal {
    public Jefe(String nombre, Oficina oficina) {
        super(nombre, oficina);
    }

    @Override
    public void aTrabajar() {
        oficina.saludoJefe(nombre);
    }
}
