package empresa;

import java.util.Random;

public class app {
    public static void main(String[] args) {
        int NUM_EMPLEADOS = 10;
        int cuandoLlegaElJefe = new Random().nextInt(NUM_EMPLEADOS + 1);
        Oficina oficina = new Oficina();
        for (int i = 0; i <= NUM_EMPLEADOS; i++) {
            if (cuandoLlegaElJefe == i)
                new Jefe("JEFE", oficina).start();
            else
                new Trabajador("Trabajador " + ((i < cuandoLlegaElJefe) ? i : i - 1), oficina).start();
        }
    }
}
