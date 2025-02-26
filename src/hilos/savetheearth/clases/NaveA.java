package savetheearth.clases;

public class NaveA extends Nave {

    public NaveA(HWWC hwwc, int id) {
        super(hwwc, id);
    }

    @Override
    public void run() {
        while (!hwwc.todosDestruidos()) {
            try {
                Meteorito meteorito = hwwc.obtenerMeteoritoDisponible();
                if (meteorito != null && !meteorito.estaDestruido() && !meteorito.estaTaladrado()) {
                    // Aterrizar en el meteorito
                    meteorito.aterrizar(this);
                    // Taladrar el meteorito
                    meteorito.taladrar();
                    // Esperar a que llegue una nave BS
                    // synchronized (meteorito) {
                    // meteorito.wait();
                    // }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
