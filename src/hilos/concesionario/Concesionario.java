package concesionario;

import java.util.ArrayList;

class Concesionario {
    ArrayList<Coche> coches = new ArrayList<>();

    public Concesionario() {
        insertarCoches("León", 10);
        insertarCoches("Ibiza", 7);
        insertarCoches("Ateca", 3);
    }

    private void insertarCoches(String modelo, int cantidad) {
        for (int i = 1; i <= cantidad; i++)
            coches.add(new Coche(modelo, i));
    }

    public synchronized Coche getCocheLibre() {
        synchronized (coches) {
            for (Coche coche : coches)
                if (!coche.estaOcupado())
                    return coche;
            return null;
        }
    }

    public void mostrarVentas() {
        for (Coche coche : coches)
            System.out.println(coche);
    }
}