package sockets.pruebas.ejercicio1;

public class Usuario {
    private String nombreUsuario;
    private int numConexiones;
    private boolean conectado;

    public Usuario() {
    }

    public Usuario(String nombre, int numConexiones, boolean conectado) {
        this.nombreUsuario = nombre;
        this.numConexiones = numConexiones;
        this.conectado = conectado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getNumConexiones() {
        return numConexiones;
    }

    public void setNumConexiones(int numConexiones) {
        this.numConexiones = numConexiones;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Usuario other = (Usuario) obj;
        if (nombreUsuario == null) {
            if (other.nombreUsuario != null)
                return false;
        } else if (!nombreUsuario.equals(other.nombreUsuario))
            return false;
        return true;
    }

    public void addConexion() {
        this.numConexiones++;
        this.conectado = true;
    }

}
