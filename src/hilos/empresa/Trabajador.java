package empresa;

public class Trabajador extends Personal {

    public Trabajador(String nombre, Oficina oficina) {
        super(nombre, oficina);
    }

    @Override
    public void aTrabajar() {
        oficina.saludoEmpleado(nombre);
    }

}
