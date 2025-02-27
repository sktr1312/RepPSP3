package Servidor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
    private boolean activo;
    private int puerto = 7;
    private ServerSocket serverSocket;
    private Map<String, Socket> clientes = new HashMap<>();

    public Servidor(int puerto) {
        this.puerto = puerto;
        this.activo = true;
    }


    /**PERMITIR CONEXIONES TRAS TIRAR SERVIDOR Y DESCONECTAR CLIENTES ACTUALES 
    public void levantar(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor arriba");
        try {
            while (activo) {  
                Socket socket = serverSocket.accept();
                String clienteId = socket.getRemoteSocketAddress().toString();
                System.out.println("Cliente conectado: " + clienteId);
                clientes.put(clienteId, socket);
                new Thread(new ClientHandler(socket, this)).start();
            }
        } catch (IOException e) {
            if (activo) {
                throw e; // Solo relanzamos si no fue un cierre voluntario
            }
        }
    } */

    /**NO PERMITIR MAS CONEXIONES DE NINGUN CLIENTE TRAS DESCONECTAR LOS ACTUALES  */
    public void levantar() throws IOException {
        serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor arriba");
        try {
            while (activo) {  
                try {
                    Socket socket = serverSocket.accept();
                    if (!activo) {
                        socket.close();
                        break;
                    }
                    String clienteId = socket.getRemoteSocketAddress().toString();
                    System.out.println("Cliente conectado: " + clienteId);
                    clientes.put(clienteId, socket);
                    new Thread(new ClientHandler(socket, this)).start();
                } catch (IOException e) {
                    if (activo) {
                        throw e;
                    }
                    break;
                }
            }
            close();
        } catch (IOException e) {
            if (activo) {
                throw e;
            }
        }
    }
    
    public synchronized void close() {
        // Evitar cierre múltiple
        setActivo(false);
        try {
            // Primero cerramos el serverSocket para evitar nuevas conexiones
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            // Luego cerramos las conexiones de los clientes
            for (Socket socket : clientes.values()) {
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.out.println("Error cerrando socket cliente: " + e.getMessage());
                }
            }
            clientes.clear();
        } catch (IOException e) {
            System.out.println("Error cerrando servidor: " + e.getMessage());
        }
    } 


    public synchronized void setActivo(boolean activo) {
        this.activo = activo;
    }
    public  Map<String, Socket> getClientes() {
        return clientes;
    }
}