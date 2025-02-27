package Servidor;

import java.io.IOException;

//debe permitir varias conexiones simultaneas con distintos clientes
public class Main {

    public static void main(String[] args) throws IOException {
        int puerto = 7; // puerto ECHO
        Servidor servidor = new Servidor(puerto);
        servidor.levantar();
    }

}