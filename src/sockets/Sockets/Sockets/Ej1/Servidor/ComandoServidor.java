package Servidor;

public enum ComandoServidor {
    FIN("/FIN"),
    SD("/SD"),
    ECHO("/ECHO"),
    HELP("/HELP");

    private final String comando;

    ComandoServidor(String comando) {
        this.comando = comando;
    }

    public static ComandoServidor fromString(String texto) {
        for (ComandoServidor cmd : ComandoServidor.values()) {
            if (cmd.comando.equalsIgnoreCase(texto)) {
                return cmd;
            }
        }
        return null;
    }
}