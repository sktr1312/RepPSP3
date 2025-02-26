package aparcamiento;

import java.util.Random;

public class Conductor extends Thread {
    String nombre;
    String matricula;
    int numCoche;
    Parking parking;
    static final int TIEMPO_REFLEXION = 5;
    static Random random = new Random();

    public Conductor(String nombre, String matricula, int numCoche) {
        this.nombre = nombre;
        this.matricula = matricula;
        this.numCoche = numCoche;
    }

    public int getNumCoche() {
        return numCoche;
    }

    public void setNumCoche(int numCoche) {
        this.numCoche = numCoche;
    }

    public void setParking(Parking parking) {
        this.parking = parking;
    }

    public Parking getParking() {
        return parking;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public void run() {
        int numPlaza = -1;
        try {
            numPlaza = parking.aparcar(this);
        } catch (InterruptedException e) {
            System.out.println("Intentando aparcar");
        }

        try {
            Thread.sleep(random.nextInt(TIEMPO_REFLEXION));
        } catch (InterruptedException ex) {
            System.out.println("Dejando plaza el coche " + String.valueOf(this.getNumCoche()));
        }
        parking.dejarPlaza(numPlaza);
    }

}
