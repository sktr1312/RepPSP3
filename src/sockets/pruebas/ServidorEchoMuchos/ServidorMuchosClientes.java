package sockets.pruebas.ServidorEchoMuchos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class ServidorMuchosClientes extends Thread {
    int puerto = 7; // puerto ECHO
    ServerSocket serverSocket;
    boolean salir = false;

    public ServidorMuchosClientes() throws IOException {
        serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor arriba");
    }

    @Override
    public void run() {
        do {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                System.out.println("Servidor abajo");
                return;
            }
            if (!salir)
                new HiloEcho(this, socket).start();
        } while (!salir);
    }

    public void shutdown() {
        salir = true;
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("Problemas duante el apagado del servidor");
        }
    }

    public static void main(String[] args) {
        try {
            new ServidorMuchosClientes().start();
        } catch (IOException ex) {
            System.out.println("Error iniciando el servidor");
        }
    }
}

class HiloEcho extends Thread {
    String finHilo = "fin", finServidor = "shutdown";
    ServidorMuchosClientes servidor;
    Socket socket;

    public HiloEcho(ServidorMuchosClientes servidor, Socket socket) {
        this.servidor = servidor;
        this.socket = socket;
    }

    @Override
    public void run() {
        SocketAddress clientAddress = socket.getRemoteSocketAddress();
        System.out.println("Ha conectado " + clientAddress);
        DataInputStream in;
        DataOutputStream out;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("Problemas creando la conexión");
            return;
        }
        String str;
        boolean salir = false;
        while (!salir) {
            try {
                str = in.readUTF();
                if (str.equalsIgnoreCase(finServidor))
                    servidor.shutdown();
                out.writeUTF(str);
            } catch (IOException ex) {
                System.out.println("Error en la transmisión");
                break;
            }
            if (str.equalsIgnoreCase(finHilo))
                salir = true;
            else {
                System.out.println("Servidor retransmite: " + str);
                System.out.println("***************************");
            }
        }
        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error cerrando conexión");
        }
    }

}
