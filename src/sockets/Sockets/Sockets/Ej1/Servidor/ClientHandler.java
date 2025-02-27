package Servidor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

class ClientHandler implements Runnable {
    private Socket socket;
    private Servidor sv;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean primero = true;

    public ClientHandler(Socket socket, Servidor sv) {
        this.socket = socket;
        this.sv = sv;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private String procesarComando(String entrada) throws IOException {
        ComandoServidor comando = ComandoServidor.fromString(entrada);
        if (comando == null) {
            comando = ComandoServidor.HELP;
        }
        switch (comando) {
            case HELP -> {
                Comando.help(out);
                return "Mostrando ayuda...";
            }
            case SD -> {
                // Informar al cliente actual
                out.writeUTF("El servidor se apagará en 2 segundos...");
                // Crear un nuevo hilo para el apagado diferido
                new Thread(() -> {
                    try {
                        //Informar a todos los demás clientes
                        for (Socket clientSocket : sv.getClientes().values()) {
                            if (!clientSocket.isClosed())  { //!clientSocket.equals(socket) && 
                                clientSocket.close();
                                //DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
                               // clientOut.writeUTF("El servidor se apagará en 2 segundos...");
                            }
                        }
                        // Thread.sleep(2000);
                        // Apagar el servidor
                        sv.setActivo(false);
                        sv.close();
                    } catch (Exception e) {
                        System.out.println("Error durante el apagado: " + e.getMessage());
                    }
                }).start();
                return "Iniciando secuencia de apagado...";
            }
          
            case FIN -> {
                Comando.fin(out);
                return "Desconectando...";
            }
            case ECHO -> {
                return comando.toString();
            }
        }
        return "";
    }

    @Override
    public void run() {
        try {
            SocketAddress clientAddress = socket.getRemoteSocketAddress();
            System.out.println("Ha conectado " + clientAddress);
            if (primero) {
                out.writeUTF("Bienvenido! Escribe HELP para ver los comandos disponibles.");
                primero = false;
            }
            
            while (!socket.isClosed()) {
                try {
                    String entrada = in.readUTF();
                    if (entrada == null) break;
                    
                    String respuesta = procesarComando(entrada);
                    if (!socket.isClosed()) {
                        //out.writeUTF(respuesta);
                    }
                    if (ComandoServidor.FIN.toString().equalsIgnoreCase(entrada)) {
                        break;
                    }
                    System.out.println("Servidor procesa: " + entrada);
                    System.out.println("Respuesta: " + respuesta);
                    System.out.println("****************************");
                } catch (IOException e) {
                    if (!socket.isClosed()) {
                        throw e;
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Cliente desconectado: " + socket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.out.println("Error cerrando el socket del cliente: " + e.getMessage());
            }
        }
    }
}