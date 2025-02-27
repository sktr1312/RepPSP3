package sockets.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClienteConection extends Thread {
    private Socket cliente;
    ServidorChatSocket servidorChatSocket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private Cliente clienteModelo;

    public ClienteConection(ServidorChatSocket servidorChatSocket, Socket cliente) {
        this.cliente = cliente;
        this.servidorChatSocket = servidorChatSocket;

    }

    @Override
    public void run() {
        try {
            entrada = new DataInputStream(cliente.getInputStream());
            salida = new DataOutputStream(cliente.getOutputStream());
            salida.writeUTF("Bienvenido al chat\nIngrese su nombre de usuario: ");
            String nombreUsuario = entrada.readUTF();
            if (servidorChatSocket.getClientes().containsKey(nombreUsuario)) {
                salida.writeUTF("El nombre de usuario ya existe");
                cliente.close();
                return;

            } else {
                clienteModelo = new Cliente(nombreUsuario, cliente);
                servidorChatSocket.getClientes().put(nombreUsuario, this);
                salida.writeUTF("Bienvenido " + nombreUsuario);
                while (true) {
                    String nombreUsuarioDestino = entrada.readUTF();
                    if (nombreUsuarioDestino.equals("salir")) {
                        servidorChatSocket.getClientes().remove(nombreUsuarioDestino);
                        cliente.close();
                        break;
                    } else if (servidorChatSocket.getClientes().containsKey(nombreUsuarioDestino)) {
                        ClienteConection clienteDestino = servidorChatSocket.getClientes().get(nombreUsuarioDestino);
                        clienteDestino.getSalida().writeUTF(nombreUsuarioDestino + " te ha enviado un mensaje: " + entrada.readUTF());
                        
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataOutputStream getSalida() {
        return salida;
    }

}
