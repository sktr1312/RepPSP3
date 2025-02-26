package sockets.pruebas.servidorCliente1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) throws IOException {
        int puerto = 7; // puerto ECHO
        String FIN = "fin";
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor arriba");
            while (true) {
                Socket socket = serverSocket.accept(); // Esperamos por un cliente
                EchoServidor echoServer = new EchoServidor(socket, FIN);
                echoServer.start();

            }
        }
    }

}
