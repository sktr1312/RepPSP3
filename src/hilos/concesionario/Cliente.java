package concesionario;

class Cliente extends Thread {
    Concesionario concesionario;
    static int numCliente = 0;

    public Cliente(Concesionario concesionario) {
        super("" + ++numCliente);
        this.concesionario = concesionario;
    }

    @Override
    public void run() {
        Coche posibleCoche;
        while ((posibleCoche = concesionario.getCocheLibre()) != null)
            if (posibleCoche.intentarComprar(this))
                break;
    }

    @Override
    public String toString() {
        return "C" + getName();
    }
}
