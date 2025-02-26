package apuestas;

import java.util.ArrayList;

public class Porra {
    int numTrabajadores;
    String equipoGanador;
    Partido partido;
    int cantidadTotalApostada = 0;
    ArrayList<Trabajador> ganadores = new ArrayList<>();
    ArrayList<Trabajador> trabajadores = new ArrayList<>();
    boolean hayApuestas = true;
    int cantidadPorGanador;

    public Porra(Partido partido, int numTrabajadores) {
        this.partido = partido;
        this.numTrabajadores = numTrabajadores;
    }

    public synchronized void apostar(Trabajador trabajador, int cantidad, int equipo) {

        if (trabajadores.size() < (numTrabajadores * trabajador.getNumApuestas())) {
            trabajadores.add(trabajador);
            cantidadTotalApostada += cantidad;
            System.out.println("Cantidad apostada: " + cantidadTotalApostada);
        } else {
            trabajadores.add(trabajador);
            cantidadTotalApostada += cantidad;
            hayApuestas = false;
            notifyAll();
        }
    }

    public synchronized int cobrar() {
        cantidadTotalApostada -= cantidadPorGanador;
        return cantidadPorGanador;

    }

    public ArrayList<Trabajador> getGanadores() {
        return ganadores;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public synchronized void esperarPartido() {
        if (hayApuestas) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error en el hilo");
            }
        }
        setEquipoGanador(partido.jugarPartido());
        setGanadores();
        setCantidadPorGanador();
    }

    public void setGanadores() {
        for (Trabajador trabajador : trabajadores) {
            if (trabajador.getEquipo().equals(equipoGanador)) {
                ganadores.add(trabajador);
            }
        }
    }

    public String getEquipoGanador() {
        return equipoGanador;
    }

    public void setEquipoGanador(String equipoGanador) {
        hayApuestas = true;
        this.equipoGanador = equipoGanador;
    }

    public int getCantidadTotalApostada() {
        return cantidadTotalApostada;
    }

    private void setCantidadPorGanador() {
        this.cantidadPorGanador = getCantidadTotalApostada() / ganadores.size();
    }

}
