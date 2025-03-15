package examenrest.menu;

import examenrest.commands.Command;
import examenrest.commands.PrintCommand;

// Clase para un elemento de menú (hoja del composite)
public class MenuItem implements MenuComponent {
    private String name;
    private Command command;
    private PrintCommand printCommand;

    public MenuItem(String name, Command command) {
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
        command.execute();
        printCommand.execute();
    }
}
