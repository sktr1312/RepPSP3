package parking;

public class Parking {

    private static final int TOTAL_PLAZAS = 10;
    private final int[] plazas; // 0 = libre, otro número = ID del conductor

    public Parking() {
        plazas = new int[TOTAL_PLAZAS];
    }

    public synchronized int aparcar(int conductorId) throws InterruptedException {
        while (true) {
            for (int i = 0; i < TOTAL_PLAZAS; i++) {
                if (plazas[i] == 0) {
                    plazas[i] = conductorId;
                    mostrarEstado("ENTRADA - Conductor " + conductorId + " aparca en plaza " + i);
                    return i;
                }
            }
            wait();
        }
    }

    public synchronized void salir(int plazaId, int conductorId) {
        plazas[plazaId] = 0;
        mostrarEstado("SALIDA  - Conductor " + conductorId + " libera plaza " + plazaId);
        notify();
    }

    private void mostrarEstado(String evento) {
        System.out.println("\n" + evento);
        System.out.println("Estado del aparcamiento:");
        for (int i = 0; i < TOTAL_PLAZAS; i++) {
            if (plazas[i] == 0) {
                System.out.print("[L] ");
            } else {
                System.out.print("[C" + plazas[i] + "] ");
            }
        }
        System.out.println("\n");
    }
}