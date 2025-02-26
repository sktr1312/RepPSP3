package empresa;

public abstract class Personal extends Thread {
    String nombre;
    Oficina oficina;

    public Personal(String nombre, Oficina oficina) {
        this.nombre = nombre;
        this.oficina = oficina;

    }

    public abstract void aTrabajar();

    @Override
    public void run() {
        aTrabajar();
    }

}