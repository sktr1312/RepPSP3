package apuestas;

import java.util.Random;

public class Trabajador extends Thread {

    final int NUM_APUESTAS = 5;
    String nombre;
    Porra porra;
    String equipo;
    int cantidadAGanar = 0;

    public Trabajador(String nombre, Porra porra, String equipo) {
        this.nombre = nombre;
        this.porra = porra;
        this.equipo = equipo;
    }

    public void apostar(int cantidadApostar) {
        porra.apostar(this, cantidadApostar, equipo.equals("DM2") ? 0 : 1);
    }

    @Override
    public void run() {
        for (int i = 0; i < NUM_APUESTAS; i++) {
            apostar(i + 1);
        }
        try {
            Thread.sleep(new Random().nextInt(100, 300));
        } catch (InterruptedException e) {
            System.out.println("Error en el hilo");
        }

        esperarPartido();
        comprobarResultado();
    }

    public void esperarPartido() {
        porra.esperarPartido();
    }

    public void comprobarResultado() {
        if (porra.getEquipoGanador().equals(equipo)) {
            cantidadAGanar = cobrar();
        }

    }

    public int cobrar() {
        return porra.cobrar();
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public int getCantidadAGanar() {
        return cantidadAGanar;
    }

    public void setCantidadAGanar(int cantidadAGanar) {
        this.cantidadAGanar = cantidadAGanar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    int getNumApuestas() {
        return NUM_APUESTAS;
    }

    

}
