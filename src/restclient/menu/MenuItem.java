package restclient.menu;

import restclient.commands.PrintCommand;

// Clase para un elemento de menú (hoja del composite)
public class MenuItem implements MenuComponent {
    private String name;
    private Runnable command;
    private PrintCommand printCommand;

    public MenuItem(String name, Runnable command) {
        this.name = name;
        this.command = command;
        printCommand = new PrintCommand("Presione Enter para continuar...");
    }

    @Override
    public void display() {
        System.out.println("- " + name);
    }

    @Override
    public void execute() {
        command.run();
        printCommand.run();
    }
}
