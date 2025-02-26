package aparcamiento;

import java.util.ArrayList;

public class App {

        public static void main(String[] args) {
                final int NUM_PLAZAS = 10;
                final int NUM_CONDUCTORES = 50;
                ArrayList<Conductor> conductores = new ArrayList<>();

                Parking parking = new Parking(NUM_PLAZAS);
                for (int i = 1; i < NUM_CONDUCTORES; i++) {
                        conductores.add(new Conductor("Conductor " + i, "Matricula " + i, i));
                        conductores.get(i - 1).setParking(parking);
                        conductores.get(i - 1).start();

                }

                for (Conductor conductor : conductores) {
                        try {
                                conductor.join();
                        } catch (InterruptedException e) {
                                System.out.println(e.getMessage());
                        }

                }

        }

}
