package clienteHttp.commands;

public class ExitCommand implements Command {
    @Override
    public void execute() {
        System.out.println("Saliendo...");
        System.exit(0);
    }

}
