package Servidor;

import java.io.DataOutputStream;
import java.io.IOException;

public  class Comando {
    public static void fin( DataOutputStream out) throws IOException{
        out.writeUTF("Cerrando servidor");
    }

    public static void help( DataOutputStream out) throws IOException{
        out.writeUTF("Comandos disponibles: \n" +
        "/FIN - Desconectar cliente\n" +
        "/SD - Apagar servidor\n" +
        "/HELP - Mostrar ayuda\n");
    }


}
