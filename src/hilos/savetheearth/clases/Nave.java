package savetheearth.clases;

public abstract class Nave extends Thread {
    protected final HWWC hwwc;
    protected final int id;

    public Nave(HWWC hwwc, int id) {
        this.hwwc = hwwc;
        this.id = id;
    }

    @Override
    public abstract void run();

     protected void informar(String mensaje) {
        System.out.println(this.getClass().getSimpleName() + " " + id + ": " + mensaje);
    }

    public int getIdNave() {
        return id;
    }
}
