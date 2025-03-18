package restclient.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import restclient.utiles.TerminalUtils;


// Clase para un menú que puede contener otros menús o items
public class Menu implements MenuComponent {
    private String title;
    private List<MenuComponent> components = new ArrayList<>();

    public Menu(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addComponent(MenuComponent component) {
        components.add(component);
    }

    @Override
    public void display() {
        System.out.println("\n=== " + title + " ===");
        for (int i = 0; i < components.size(); i++) {
            System.out.print((i + 1) + ". ");
            components.get(i).display();
        }
        System.out.println("0. Volver/Salir");
    }

    @Override
    public void execute() {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            TerminalUtils.clearScreen();
            display();
            System.out.print("Seleccione una opción: ");

            try {
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    choice = -1;
                    continue;
                }

                choice = Integer.parseInt(input);

                if (choice > 0 && choice <= components.size()) {
                    components.get(choice - 1).execute();
                } else if (choice != 0) {
                    System.out.println("Opción no válida");
                    System.out.println("Presione Enter para continuar...");
                    scanner.nextLine();
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número");
                System.out.println("Presione Enter para continuar...");
                scanner.nextLine();
                choice = -1;
            }
        } while (choice != 0);

    }
}