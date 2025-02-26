package sockets.pruebas.ejercicio2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ServidorConexiones extends Thread {
    int puerto = 50000; // puerto ECHO
    ServerSocket serverSocket;
    boolean salir = false;
    List<Usuario> usuarios;

    public ServidorConexiones() throws IOException {
        serverSocket = new ServerSocket(puerto);
        usuarios = new ArrayList<>();
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
                new HiloConexiones(this, socket).start();
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

    public boolean comprobarUsuario(String nombreCliente) {
        boolean existe = false;
        List<Usuario> usuariosConectados = usuarios.stream().filter((usuario) -> {
            return usuario.isConectado() == true;
        }).toList();
        if (usuariosConectados.contains(new Usuario(nombreCliente, 0, false))) {
            existe = true;
        }
        return existe;
    }

    public static void main(String[] args) {
        try {
            new ServidorConexiones().start();
        } catch (IOException ex) {
            System.out.println("Error iniciando el servidor");
        }
    }

    public void addConexion(String mensajeCliente) {
        int index = usuarios.indexOf(new Usuario(mensajeCliente, 1, true));
        if (index >= 0) {
            Usuario user = usuarios.get(index);
            user.addConexion();
        } else {
            usuarios.add(new Usuario(mensajeCliente, 1, true));
        }
    }

    public String extraerUsuarios() {
        List<Usuario> usuariosConectados = usuarios.stream().filter((o1) -> {
            return o1.isConectado() == true;
        }).toList();

        StringBuilder strBuilder = new StringBuilder();
        usuariosConectados.forEach(o1 -> {
            strBuilder.append(o1.getNombreUsuario()).append(",").append(o1.getNumConexiones()).append(",")
                    .append(o1.isConectado()).append(";");
        });

        return strBuilder.toString();
    }

    public String extraerUsuario(String mensaje) {
        Usuario userTemporal = new Usuario();
        userTemporal.setNombreUsuario(mensaje);
        userTemporal = usuarios.get(usuarios.indexOf(userTemporal));
        return userTemporal.getNombreUsuario() + "," + userTemporal.getNumConexiones() + ","
                + userTemporal.isConectado();
    }
}

/* HILO INTERNO */
class HiloConexiones extends Thread {
    String finHilo = "fin", finServidor = "shutdown";
    ServidorConexiones servidor;
    Socket socket;
    String mensajeCliente;
    String nombreUsuario;
    StringBuilder respuestaServidor;
    final int LOG_OUT = 200;
    final int CONEXION_ESTABLECIDA = 201;
    final int SERVIDOR_APAGADO = 202;
    final int USUARIO_ENCONTRADO = 203;
    final int ERROR_EXISTE_USUARIO = 204;
    final int ERROR_NO_EXISTE_USUARIO = 205;
    final int ERROR_PALABRA_CLAVE = 206;

    public HiloConexiones(ServidorConexiones servidor, Socket socket) {
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
        if (connect(in, out)) {
            recibirPeticiones(in, out);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error cerrando conexión");
        }
    }

    private boolean connect(DataInputStream in, DataOutputStream out) {
        boolean conectado = false;
        while (!conectado) {
            try {
                mensajeCliente = in.readUTF();
                nombreUsuario = mensajeCliente;
                boolean existeCliente = servidor.comprobarUsuario(mensajeCliente);
                if ((nombreUsuario != finHilo && nombreUsuario != finServidor) && !existeCliente) {
                    out.writeInt(CONEXION_ESTABLECIDA);
                    servidor.addConexion(nombreUsuario);
                    conectado = true;
                    System.out.println("Se ha conectado el usuario: " + mensajeCliente);
                    System.out.println("***************************");
                } else if (existeCliente) {
                    out.writeBoolean(false);
                    out.writeInt(ERROR_EXISTE_USUARIO);
                } else if (mensajeCliente == finServidor) {
                    out.writeInt(ERROR_PALABRA_CLAVE);
                } else {
                    out.writeInt(LOG_OUT);
                    break;
                }
            } catch (IOException ex) {
                System.out.println("Error en la transmisión");
                break;
            }
        }
        return conectado;
    }

    private void recibirPeticiones(DataInputStream in, DataOutputStream out) {
        boolean sw = false;
        while (!sw) {
            try {
                String mensaje = in.readUTF();
                if (!mensaje.equalsIgnoreCase(finHilo) || !mensaje.equalsIgnoreCase(finServidor)) {
                    boolean existeUsuario = servidor.comprobarUsuario(mensaje);
                    if (existeUsuario) {
                        out.writeInt(USUARIO_ENCONTRADO);
                        out.writeUTF(servidor.extraerUsuario(mensaje));
                    } else {
                        out.writeInt(ERROR_NO_EXISTE_USUARIO);
                    }
                } else if (mensaje.equalsIgnoreCase(finHilo)) {
                    out.writeInt(LOG_OUT);
                    sw = true;
                } else if (mensaje.equalsIgnoreCase(finServidor)) {
                    out.writeInt(SERVIDOR_APAGADO);
                    servidor.shutdown();
                    sw = true;
                }
            } catch (IOException e) {
                System.out.println("Error en la transmisión");
                break;
            }

        }
    }

}
