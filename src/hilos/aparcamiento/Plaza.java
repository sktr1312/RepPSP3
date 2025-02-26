package aparcamiento;

public class Plaza {
    int numPlaza;
    private boolean ocupado;
    private Conductor conductor;
    Parking parking;

    public Plaza(int numPlaza, Parking parking) {
        this.parking = parking;
        ocupado = false;
        conductor = null;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public int getNumPlaza() {
        return numPlaza;
    }

    public void ocuparPlaza(Conductor conductor) {
            ocupado = true;
            this.conductor = conductor;
            parking.actualizarParking(conductor, !ocupado);
    }

    public void liberarPlaza() {
        ocupado = false;
        parking.actualizarParking(conductor, !ocupado);
    }

    @Override
    public String toString() {
        return "Plaza [ocupado=" + ocupado + ", matricula=" + conductor + "]";
    }
}
