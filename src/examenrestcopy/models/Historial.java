package examenrestcopy.models;

import com.google.gson.annotations.SerializedName;

public class Historial {

    @SerializedName("id")
    private int id;
    @SerializedName("telefono")
    private Telefono telefono;
    @SerializedName("codOperadorAntiguo")
    private Operador operadorAntiguo;
    @SerializedName("codOperadorNuevo")
    private Operador operadorNuevo;
    @SerializedName("motivos")
    private String motivos;

    public Historial() {
    }

    public Historial(int id, Telefono telefono, Operador operadorAntiguo, Operador operadorNuevo, String motivos) {
        this.id = id;
        this.telefono = telefono;
        this.operadorAntiguo = operadorAntiguo;
        this.operadorNuevo = operadorNuevo;
        this.motivos = motivos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Telefono getTelefono() {
        return telefono;
    }

    public void setTelefono(Telefono telefono) {
        this.telefono = telefono;
    }

    public Operador getOperadorAntiguo() {
        return operadorAntiguo;
    }

    public void setOperadorAntiguo(Operador operadorAntiguo) {
        this.operadorAntiguo = operadorAntiguo;
    }

    public Operador getOperadorNuevo() {
        return operadorNuevo;
    }

    public void setOperadorNuevo(Operador operadorNuevo) {
        this.operadorNuevo = operadorNuevo;
    }

    public String getMotivos() {
        return motivos;
    }

    public void setMotivos(String motivos) {
        this.motivos = motivos;
    }

    
    @Override
    public String toString() {
        return "Historial{" +
                "id=" + id +
                ", telefono=" + telefono +
                ", operadorAntiguo=" + operadorAntiguo +
                ", operadorNuevo=" + operadorNuevo +
                ", motivos='" + motivos + '\'' +
                '}';
    }
}
