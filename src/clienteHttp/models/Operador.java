package clienteHttp.models;

import com.google.gson.annotations.SerializedName;

public class Operador {

    @SerializedName("codOperador")
    private int codOperador;
    @SerializedName("nombre")
    private String nombre;

    public Operador() {
    }

    public Operador(int codOperador, String nombre) {
        this.codOperador = codOperador;
        this.nombre = nombre;
    }

    public int getCodOperador() {
        return codOperador;
    }

    public void setCodOperador(int codOperador) {
        this.codOperador = codOperador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Operador{" +
                "codOperador=" + codOperador +
                ", nombre='" + nombre + '\'' +
                '}';
    }

}
