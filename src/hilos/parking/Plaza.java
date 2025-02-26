package parking;

public class Plaza {
    private final int numeroPlaza;
    private Integer conductorId;

    public Plaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
        this.conductorId = null;
    }

    public boolean intentarAparcar(int conductorId) {
        if (this.conductorId == null) {
            this.conductorId = conductorId;
            System.out.println("Conductor " + conductorId + " aparca en la plaza " + numeroPlaza);
            return true;
        }
        return false;

    }

    public void dejarPlaza(int conductorId) {
        if (this.conductorId != null && this.conductorId == conductorId) {
            System.out.println("Conductor " + conductorId + " deja la plaza " + numeroPlaza);
            this.conductorId = null;
        }

    }

    public boolean estaOcupadaPor(int conductorId) {
        return this.conductorId != null && this.conductorId == conductorId;

    }

    public boolean estaOcupada() {
        return conductorId != null;
    }

    public int getNumeroPlaza() {
        return numeroPlaza;
    }

    public Integer getConductorId() {
        return conductorId;
    }
}
