package apuestas;

import java.util.Random;

public class Partido {

    String equipo1 = "DM2";
    String equipo2 = "PRF";
    final int NUM_GOLES_MAX = 4;
    int golesEquipo1 = 0;
    int golesEquipo2 = 0;
    Random random = new Random();

    public String jugarPartido() {
        while (golesEquipo1 < NUM_GOLES_MAX && golesEquipo2 < NUM_GOLES_MAX) {
            int equipo = random.nextInt(2);
            if (equipo == 0) {
                golesEquipo1++;
            } else {
                golesEquipo2++;
            }

        }
        return golesEquipo1 > golesEquipo2 ? equipo1 : equipo2;
    }

    public int getGolesEquipo1() {
        return golesEquipo1;

    }

    public int getGolesEquipo2() {
        return golesEquipo2;
    }

}
