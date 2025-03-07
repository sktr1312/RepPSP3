package clienteHttp.models;

import com.google.gson.annotations.SerializedName;

public class Telefono {

    @SerializedName("numTelefono")
    private int numTelefono;
    @SerializedName("codOperador")
    private Operador operador;
    @SerializedName("Titular")
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
                '}';
    }

}
