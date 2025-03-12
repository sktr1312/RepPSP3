package clienteHttp.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Modificar la clase Menu para limpiar la pantalla entre navegaciones
// Clase para un menú que puede contener otros menús o items
public class Menu implements MenuComponent {
    private String title;
    private List<MenuComponent> components = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public Menu(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addComponent(MenuComponent component) {
        components.add(component);
    }

    private void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
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
        int choice;
        do {
            clearScreen();
            display();
            System.out.print("Seleccione una opción: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
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