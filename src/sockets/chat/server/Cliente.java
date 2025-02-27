package sockets.chat.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private String nombreUsuario;
    private List<String> mensajesEnviados;
    private List<String> mensajesRecibidos;
    Socket socket;

    public Cliente(String nombreUsuario, Socket socket) {
        this.nombreUsuario = nombreUsuario;
        this.mensajesEnviados = new ArrayList<>();
        this.mensajesRecibidos = new ArrayList<>();
        this.socket = socket;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public List<String> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(List<String> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

    public List<String> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(List<String> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
