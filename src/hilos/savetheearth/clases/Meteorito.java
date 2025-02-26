package savetheearth.clases;

import java.util.Date;

public class Meteorito {
    private int id;
    private boolean taladrado = false;
    private boolean destruido = false;
    private Nave naveActual = null;

    public Meteorito(int id) {
        this.id = id;
    }

    public synchronized void aterrizar(Nave nave) throws InterruptedException {
        while (naveActual == null) {
            naveActual = nave;
            System.out.printf("[%tT] 🛸 Perforadora-%d ha aterrizado en meteorito %d%n",
                    new Date(), nave.getIdNave(), id);
        }

    }

    public synchronized void taladrar() {
        taladrado = true;
        System.out.printf("[%tT] 🔨 Perforadora-%d ha completado el taladrado del meteorito %d%n",
                new Date(), naveActual.getIdNave(), id);
        try {
            wait(); // Esperar a que llegue una nave BS
        } catch (InterruptedException e) {
        }
    }

    public synchronized void surtirCombustible() {
        System.out.printf("[%tT] ⛽ Bombardero-%d está surtiendo combustible en meteorito %d%n",
                new Date(), naveActual.getIdNave(), id);
        notify();

    }

    public synchronized void destruir() {
        System.out.printf("Meteorito %S ha sido destruido%n", id);
        destruido = true;
        naveActual = null;
        // notifyAll();
    }

    public synchronized boolean estaDestruido() {
        return destruido;
    }

    public boolean estaTaladrado() {
        return taladrado;

    }
}