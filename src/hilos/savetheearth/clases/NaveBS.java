package savetheearth.clases;

public class NaveBS extends Nave {

    public NaveBS(HWWC hwwc, int id) {
        super(hwwc, id);
    }

    @Override
    public void run() {
        while (!hwwc.todosDestruidos()) {
            Meteorito meteorito = hwwc.obtenerMeteoritoDisponible();
            if (meteorito == null)
                break;

            // Verificar si el meteorito está taladrado
            if (meteorito.estaTaladrado() && !meteorito.estaDestruido()) {

                // Surtir combustible
                meteorito.surtirCombustible();
                // synchronized (meteorito) {
                //     meteorito.notify();
                // }

                // Colocar y detonar bomba
                meteorito.destruir();
            }
        }
    }
}