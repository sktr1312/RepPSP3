package aparcamiento;

import java.util.ArrayList;
import java.util.Random;

public class Parking {

    ArrayList<Plaza> plazas;
    private int plazasDisponibles;

    public Parking(int numPlazas) {
        plazas = new ArrayList<>();
        for (int i = 0; i < numPlazas; i++) {
            plazas.add(new Plaza(i + 1, this));
        }
        plazasDisponibles = numPlazas;
    }

    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public void setPlazasDisponibles(int plazasLibres) {
        this.plazasDisponibles = plazasLibres;
    }

    public boolean hayPlazasLibres() {
        return plazasDisponibles > 0;
    }

    public synchronized int aparcar(Conductor conductor) throws InterruptedException {
        int numPlaza = -1;
        while (!hayPlazasLibres()) {
            wait();
        }
        Plaza plaza = plazas.get(new Random().nextInt(plazas.size()));
        if (!plaza.isOcupado()) {
            plaza.ocuparPlaza(conductor);
            numPlaza = plaza.getNumPlaza();
            plazasDisponibles--;
        }

        return numPlaza;
    }

    public void actualizarParking(Conductor conductor, boolean salida) {
        StringBuilder dettalePlaza = new StringBuilder();
        for (Plaza plaza : plazas) {
            String libre = plaza.isOcupado() ? plaza.getConductor().getNumCoche() + "" : "x";
            dettalePlaza.append(libre);
            dettalePlaza.append("-");
        }
        dettalePlaza.append("El coche ").append(conductor.getNumCoche())
                .append(salida ? " Ha salido\n" : " ha aparcado\n");
        System.out.print(dettalePlaza.toString());

    }

    public synchronized void dejarPlaza(int numPlaza) {

        for (Plaza plaza : plazas) {
            if (plaza.getNumPlaza() == numPlaza) {
                plaza.liberarPlaza();
                plazasDisponibles++;
                notify();
                break;
            }
        }
    }
}
