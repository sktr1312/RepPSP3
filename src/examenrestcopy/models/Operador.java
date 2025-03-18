package examenrestcopy.models;

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

    public Operador(String string) {
        this.nombre = string;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + codOperador;
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
        Operador other = (Operador) obj;
        if (codOperador != other.codOperador)
            return false;
        return true;
    }

}
