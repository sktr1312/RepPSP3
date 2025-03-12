package clienteHttp;

import java.util.Scanner;

import clienteHttp.utiles.TerminalUtils;

public class Menu {
    public static void main(String[] args) {
        int option;
        try (Scanner sc = new Scanner(System.in)) {
            showMenu();
            try {
                option = sc.nextInt();
            } catch (Exception e) {
                option = 0;
            }
            sc.nextLine();

            while (option != 5) {
                switch (option) {
                    case 1 ->
                        visualizarRegistros(sc);
                    case 2 ->
                        insertarRegistro(sc);
                    case 3 ->
                        actualizarRegistro(sc);
                    case 4 ->
                        borrarRegistro(sc);
                    case 5 ->
                        System.out.println("Saliendo...");
                    default -> System.out.println("Opción no válida");
                }
                showMenu();
                try {
                    option = sc.nextInt();
                } catch (Exception e) {
                    option = 0;
                }
                sc.nextLine();
            }
        }
    }

    private static void visualizarRegistros(Scanner sc) {
        TerminalUtils.clearScreen();
        boolean salir = false;
        int option;
        do {
            System.out.println("Visualizacion de registros");
            System.out.println("==========================");
            System.out.println("Escocoja un recurso;");
            System.out.println("1. telefonos");
            System.out.println("2. operadores");
            System.out.println("3. historial");
            System.out.println("4. Salir");
            try {
                option = sc.nextInt();
            } catch (Exception e) {
                option = 0;
            }
            sc.nextLine();
            switch (option) {
                case 1 -> {
                    TerminalUtils.clearScreen();
                    System.out.println("Visualizando telefonos");
                    System.out.println("==========================");
                    System.out.println("1. Visualizar todos los telefonos");
                    System.out.println("2. Visualizar un telefono");
                    System.out.println("3. Salir");
                    try {
                        option = sc.nextInt();
                    } catch (Exception e) {
                        option = 0;
                    }
                    sc.nextLine();
                    switch (option) {
                        case 1 ->
                            System.out.println(new SendRequest().sendGetTelefonosRequest());
                        case 2 -> {
                            System.out.println("Introduce el número de telefono");
                            try {
                                int numTelefono = sc.nextInt();
                                System.out.println(new SendRequest().sendGetTelefonoRequest(numTelefono));
                            } catch (Exception e) {
                                System.out.println("Número de telefono no válido");
                            }
                        }
                        case 3 ->
                            salir = true;
                        default ->
                            System.out.println("Opción no válida");
                    }
                }

            }
        } while (!salir);

    }

    private static void insertarRegistro(Scanner sc) {
        System.out.println("Escoja un recurso a insertar");
        System.out.println("==========================");
        System.out.println("Escocoja un recurso;");
        System.out.println("1. telefonos");
        System.out.println("2. operadores");
        System.out.println("3. historial");
        System.out.println("4. Salir");

    }

    private static void actualizarRegistro(Scanner sc) {
        System.out.println("Que recurso quiere actualizar");
        System.out.println("==========================");
        System.out.println("Escocoja un recurso;");
        System.out.println("1. telefonos");
        System.out.println("2. operadores");
        System.out.println("3. historial");
        System.out.println("4. Salir");

    }

    private static void borrarRegistro(Scanner sc) {
        System.out.println("Que recurso quiere borrar");
        System.out.println("==========================");
        System.out.println("Escocoja un recurso;");
        System.out.println("1. telefonos");
        System.out.println("2. operadores");
        System.out.println("3. historial");
        System.out.println("4. Salir");
    }

    private static void showMenu() {
        System.out.println("Menú");
        System.out.println("==========================");
        System.out.println("1. Visualizar registros");
        System.out.println("2. Insertar nuevo registro");
        System.out.println("3. Actualizar registro");
        System.out.println("4. Eliminar registro");
        System.out.println("5. Salir");
    }
}
