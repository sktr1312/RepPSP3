package sockets.pruebas.servidorCliente1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

public class EchoServidor extends Thread {
    Socket socket;
    String fin;

    public EchoServidor(Socket socket, String fin) {
        this.socket = socket;
        this.fin = fin;
    }

    @Override
    public void run() {
        SocketAddress clientAddress = socket.getRemoteSocketAddress();
        System.out.println("Ha conectado " + clientAddress);
        try (
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            boolean salir = false;
            while (!salir) {
                String str = in.readUTF();
                out.writeUTF(str);
                if (str.equalsIgnoreCase(fin))
                    salir = true;
                else {
                    System.out.println("Servidor retransmite: " + str);
                    System.out.println("****************************");
                }
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("Error" + e.getMessage());
        }

    }

}
