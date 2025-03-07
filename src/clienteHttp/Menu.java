package clienteHttp;

import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        int option;
        try (Scanner sc = new Scanner(System.in)) {
            showMenu();
            option = sc.nextInt();
            if (sc.hasNextLine()) {
                sc.nextLine();

            }

            while (option != 5) {
                switch (option) {
                    case 1 ->
                        visualizarRegistros();

                    case 2 ->
                        insertarRegistro();

                    case 3 ->
                        actualizarRegistro();

                    case 4 ->
                        borrarRegistro();

                    case 5 ->
                        System.out.println("Saliendo...");

                    default -> System.out.println("Opción no válida");
                }

            }
            showMenu();
        }
    }

    private static void visualizarRegistros() {
        System.out.println("Visualizacion de registros");
        System.out.println("==========================");
        System.out.println("Escocoja un recurso;");
        System.out.println("1. telefonos");
        System.out.println("2. operadores");
        System.out.println("3. Ventas");

    }

    private static void insertarRegistro() {
        System.out.println("Escoja un recurso a insertar");
        System.out.println("==========================");
        System.out.println("Escocoja un recurso;");
        System.out.println("1. telefonos");
        System.out.println("2. operadores");
        System.out.println("3. Ventas");
    }

    private static void actualizarRegistro() {
        System.out.println("Que Recurso quier");
        System.out.println("==========================");
        System.out.println("Escocoja un recurso;");
        System.out.println("1. telefonos");
        System.out.println("2. operadores");
        System.out.println("3. Ventas");
    }

    private static void borrarRegistro() {
        System.out.println("Borrando registro...");
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
