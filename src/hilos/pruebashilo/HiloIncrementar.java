package pruebashilo;
// Realice una aplicación que cree NUM_HILOS hilos que incrementen NUM_INCREMENTOS un dato común.

import java.util.ArrayList;

// Al final se visualizará el valor del dato verificando si su valor es NUM_HILOS*NUM_INCREMENTOS.

// Problemas que se pueden dar:

// se crea un dato para cada hilo, no uno común
// se visualiza el valor en el hilo principal antes de que acaben todos los incrementos
// sin código sincronizado no se puede asegurar que se incremente de manera correcta.
public class HiloIncrementar extends Thread {
    static final int NUM_HILOS = 10;
    static final int NUM_INCREMENTOS = 10;
    static Integer datoComun = 0;

    public HiloIncrementar(String nombre) {
        super(nombre);
    }

    @Override
    public void run() {
        synchronized (datoComun) {

            Integer suma = datoComun;
            Integer suma2 = NUM_INCREMENTOS;
            Integer sumaTotal = suma + suma2;
            datoComun = sumaTotal;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ArrayList<HiloIncrementar> hilos = new ArrayList<>();
        for (int i = 0; i < NUM_HILOS; i++) {
            HiloIncrementar hilo = new HiloIncrementar("hilo" + i);
            hilo.start();
            hilos.add(hilo);

        }
        for (HiloIncrementar hilo : hilos) {
            hilo.join();
        }

        System.out.println(datoComun);
    }
}
