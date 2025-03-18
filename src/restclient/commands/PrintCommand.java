package restclient.commands;

// Comando para imprimir un mensaje en pantalla
public class PrintCommand implements Command {
    private String message;

    public PrintCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute() {
        System.out.println(message);
        System.console().readLine();
    }
}
