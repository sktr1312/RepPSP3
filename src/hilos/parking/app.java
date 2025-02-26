package parking;

public class app {
    private static final int TOTAL_CONDUCTORES = 50;
    
    public static void main(String[] args) {
        Parking parking = new Parking();
        Thread[] conductores = new Thread[TOTAL_CONDUCTORES];
        
        // Crear y iniciar los hilos de los conductores
        for (int i = 0; i < TOTAL_CONDUCTORES; i++) {
            conductores[i] = new Conductor(i + 1, parking);
            conductores[i].start();
        }
        
        // Esperar a que todos los conductores terminen
        for (Thread conductor : conductores) {
            try {
                conductor.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Simulación finalizada");
    }
}
