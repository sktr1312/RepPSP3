package examenrestcopy.models;

import com.google.gson.annotations.SerializedName;

public class Telefono {

    @SerializedName("numTelefono")
    private int numTelefono;
    @SerializedName("codOperador")
    private Operador operador;
    @SerializedName("titular")
    private String titular;

    public Telefono() {
    }

    public Telefono(int telefono, Operador operador, String titular) {
        this.numTelefono = telefono;
        this.operador = operador;
        this.titular = titular;
    }

    public int getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(int numTelefono) {
        this.numTelefono = numTelefono;
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    @Override
    public String toString() {
        return "Telefono{" +
                "numTelefono=" + numTelefono +
                ", operador=" + operador +
                ", titular='" + titular + '\'' +
                "}\n";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + numTelefono;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Telefono other = (Telefono) obj;
        if (numTelefono != other.numTelefono)
            return false;
        return true;
    }

}
