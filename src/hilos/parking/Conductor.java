package parking;

public class Conductor extends Thread {

    private static final int MIN_TIEMPO_PARKING = 1000; // 1 segundo
    private static final int MAX_TIEMPO_PARKING = 5000; // 5 segundos
    private final Parking parking;
    private final int id;

    public Conductor(int id, Parking parking) {
        this.id = id;
        this.parking = parking;
    }

    @Override
    public void run() {
        try {
            int plazaAsignada = parking.aparcar(id);
            Thread.sleep(MIN_TIEMPO_PARKING +
                    (long) (Math.random() * (MAX_TIEMPO_PARKING - MIN_TIEMPO_PARKING)));
            parking.salir(plazaAsignada, id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
