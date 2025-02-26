package pruebashilo;

public class Hilo extends Thread {
    static int numHilo = 0;

    public Hilo(String nombre) {
        super(nombre);
        Hilo.numHilo += 1;
    }

    public void run() {
        System.out.println("Soy el hilo " + Hilo.numHilo);
    }

    public static void main(String[] args) {
        int numHilos = 100;
        Hilo hilo = null;
        for (int i = 0; i < numHilos; i++) {
            hilo = new Hilo(String.valueOf(i));
            hilo.start();
        }
        System.out.println(hilo.getState());
    }
}
