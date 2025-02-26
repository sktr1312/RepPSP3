package apuestas;

import java.util.ArrayList;

public class App {

    public static void main(String[] args) {
        final int NUM_EMPLEADOS = 10;
        Partido partidoCDM = new Partido();
        Porra porra = new Porra(partidoCDM, NUM_EMPLEADOS);
        ArrayList<Trabajador> trabajadores = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String nombre = "Trabajador " + i;
            String equipo = (i % 2 == 0) ? "PRF" : "DM2";
            Trabajador trabajador = new Trabajador(nombre, porra, equipo);
            trabajadores.add(trabajador);
            trabajador.start();
        }

        for (Trabajador trabajador : trabajadores) {
            try {
                trabajador.join();
            } catch (InterruptedException e) {
                System.out.println("Error en el hilo");
            }

            System.out.println("Cantidad total apostada: " + porra.getCantidadTotalApostada());
            System.out.println("Resultado del partido: DM2-" + partidoCDM.getGolesEquipo1() + "  PFR-"
                    + partidoCDM.getGolesEquipo2());
            ArrayList<Trabajador> ganadores = porra.getGanadores();
            for (Trabajador trabajador1 : ganadores) {
                System.out.println(
                        "El trabajador " + trabajador1.getNombre() + " ha ganado " + trabajador1.getCantidadAGanar());
            }
        }
    }
}