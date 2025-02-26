package sockets.pruebas.ejercicio2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Cliente {
    static final int LOG_OUT = 200;
    static final int CONEXION_ESTABLECIDA = 201;
    static final int SERVIDOR_APAGADO = 202;
    static final int USUARIO_ENCONTRADO = 203;
    static final int ERROR_EXISTE_USUARIO = 204;
    static final int ERROR_NO_EXISTE_USUARIO = 205;
    static final int ERROR_PALABRA_CLAVE = 206;
    static String servidor = "localhost", FIN = "fin", mensaje = "";
    static int puerto = 50000; // puerto ECHO

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {
            Socket socket;
            DataInputStream in;
            DataOutputStream out;
            try {
                socket = new Socket(servidor, puerto);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("Problemas conectando con servidor " + servidor + ":" + puerto);
                return;
            }
            System.out.println("Conectado con el servidor");
            while (!mensaje.equalsIgnoreCase(FIN)) {
                mensaje = sc.nextLine();
                int codRecibido;
                try {
                    // Enviamos
                    out.writeUTF(mensaje);
                    System.out.println("Cliente envía: " + mensaje);
                    // Recibimos
                    codRecibido = in.readInt();
                    System.out.println("Cliente recibe: " + codRecibido);
                } catch (IOException ex) {
                    System.out.println("Problemas en la transmisión");
                    break;
                }
                switch (codRecibido) {
                    case LOG_OUT -> {
                        System.out.println("Cerrando aplicacion");
                        mensaje = FIN;
                        break;
                    }
                    case CONEXION_ESTABLECIDA -> {
                        System.out.println("Conexion establecida");
                    }
                    case ERROR_EXISTE_USUARIO -> {
                        System.out.println("Ya esta conectado un usuario con ese nombre");
                    }
                    case ERROR_NO_EXISTE_USUARIO -> {
                        System.out.println("No existe el usuario");
                    }

                    case ERROR_PALABRA_CLAVE -> {
                        System.out.println("Error palabra clave no disponible");
                    }

                    case SERVIDOR_APAGADO -> {
                        System.out.println("El servidor se apagara");
                        mensaje = FIN;
                        break;
                    }
                    case USUARIO_ENCONTRADO -> {
                        String usuario = null;
                        try {
                            usuario = in.readUTF();
                        } catch (IOException e) {
                            System.out.println("Error al recuperar el usuario");
                            e.printStackTrace();
                        }
                        if (usuario != null) {
                            Usuario user = new Usuario(usuario.split(",")[0], Integer.valueOf(usuario.split(",")[1]),
                                    Boolean.parseBoolean(usuario.split(",")[2]));
                            System.out.println("El usuario " + user.getNombreUsuario() + " se ha conectado "
                                    + user.getNumConexiones() + (user.getNumConexiones() > 1 ? " veces" : " vez")
                                    + (user.isConectado() == true ? "\tONLINE" : "\tOFFLINE"));
                        }
                    }
                }
            }
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Error cerrando conexión");
            }
        }
    }
}
