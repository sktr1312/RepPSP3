package restclient.commands;

public class ExitCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Saliendo...");
        System.exit(0);
    }

}
