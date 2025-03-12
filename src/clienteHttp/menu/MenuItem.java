package clienteHttp.menu;

import clienteHttp.commands.Command;

// Clase para un elemento de menú (hoja del composite)
public class MenuItem implements MenuComponent {
    private String name;
    private Command command;

    public MenuItem(String name, Command command) {
        this.name = name;
        this.command = command;
    }

    @Override
    public void display() {
        System.out.println("- " + name);
    }

    @Override
    public void execute() {
        command.execute();
    }
}
