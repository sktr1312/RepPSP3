package savetheearth.app;

import java.util.ArrayList;
import java.util.List;

import savetheearth.clases.HWWC;
import savetheearth.clases.Nave;
import savetheearth.clases.NaveA;
import savetheearth.clases.NaveBS;

public class App {
    final static int NUM_METEORITOS = 20;
    final static int NUM_NAVES_A = 5;
    final static int NUM_NAVES_BS = 3;

    public static void main(String[] args) {

        System.out.println("=== INICIO DE LA MISIÓN DE SALVAMENTO ===");
        System.out.printf("Meteoritos detectados: %d%n", NUM_METEORITOS);
        System.out.printf("Naves tipo Perforadora disponibles: %d%n", NUM_NAVES_A);
        System.out.printf("Naves tipo Bombardero disponibles: %d%n", NUM_NAVES_BS);
        System.out.println("=======================================");
        HWWC hwwc = new HWWC(NUM_METEORITOS);
        List<Nave> naves = new ArrayList<>();
        int id = 0;
        // Crear e iniciar naves tipo A
        for (int i = 0; i < NUM_NAVES_A; i++) {
            id++;
            Nave naveA = new NaveA(hwwc, id);
            naves.add(naveA);
            naveA.start();
        }

        // Crear e iniciar naves tipo BS
        for (int i = 0; i < NUM_NAVES_BS; i++) {
            id++;
            Nave naveBS = new NaveBS(hwwc, id);
            naves.add(naveBS);
            naveBS.start();
        }

        // Esperar a que todas las naves terminen
        for (Nave nave : naves) {
            try {
                nave.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\n=======================================");
        System.out.println("🌍 ¡Misión completada! La Tierra está a salvo.");
        System.out.println("=======================================");
    }
}
