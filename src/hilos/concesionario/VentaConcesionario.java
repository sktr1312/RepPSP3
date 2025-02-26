package concesionario;

public class VentaConcesionario {
    public static void main(String[] args) {
        final int NUM_CLIENTES = 50;

        Concesionario concesionario = new Concesionario();
        Cliente[] clientes = new Cliente[NUM_CLIENTES];
        for (int i = 0; i < clientes.length; i++) {
            clientes[i] = new Cliente(concesionario);
            clientes[i].start();
        }
        for (Cliente cliente : clientes) {
            try {
                cliente.join();
            } catch (InterruptedException e) {
            }
        }
        concesionario.mostrarVentas();
    }
}