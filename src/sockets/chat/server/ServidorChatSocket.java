package sockets.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorChatSocket extends Thread {
    private static final long serialVersionUID = 1L;
    private static final int PORT = 1234;
    private ServerSocket servidor;
    private Map<String, ClienteConection> clientes;

    public ServidorChatSocket() {
        clientes = new HashMap<>();
    }

    @Override
    public void run() {
        super.run();
        try {
            servidor = new ServerSocket(PORT);
            System.out.println("Servidor iniciado...");
            while (true) {
                Socket socket = servidor.accept();
                System.out.println("Cliente conectado...");
                ClienteConection hilo = new ClienteConection(this, socket);
                hilo.start();

            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorChatSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, ClienteConection> getClientes() {
        return clientes;
    }
}
