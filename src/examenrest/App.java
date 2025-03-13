package examenrest;

import java.util.ArrayList;
import java.util.List;

import examenrest.models.Historial;
import examenrest.menu.Menu;
import examenrest.menu.MenuItem;
import examenrest.menu.SubMenu;
import examenrest.models.Operador;
import examenrest.models.Telefono;
import examenrest.restclient.SendRequest;
import examenrest.utiles.TerminalUtils;

public class App {
    public static void main(String[] args) {
        // menu principal
        Menu menu = new Menu("Menú principal");
        // añadirSubmenus
        addSubmenus(menu);
        menu.execute();

    }

    private static void addSubmenus(Menu menu) {
        // submenus visualizar registros
        SubMenu verRegistros = new SubMenu(new Menu("Visualizar Registros"));
        // añadir opciones a visualizar registros
        verRegistros.getMenu()
                .addComponent(new MenuItem("Ver telefonos de un operador", App::visualizarTelefonosPorOperador));
        verRegistros.getMenu()
                .addComponent(new MenuItem("Ver telefonos de un titular", App::visualizarTelefonosPorTitular));
        verRegistros.getMenu()
                .addComponent(new MenuItem("Ver historial de un número", App::visualizarHistorialNumero));
        // submenus actualizar registros
        SubMenu actualizarRegistros = new SubMenu(new Menu("Actualizar Registros"));
        // submenus eliminar registros
        SubMenu eliminarRegistros = new SubMenu(new Menu("Eliminar Registros"));
        // submenu insertar registros
        SubMenu insertarRegistros = new SubMenu(new Menu("Insertar Registros"));
        // añadir submenus al menu principal
        menu.addComponent(verRegistros);
        menu.addComponent(actualizarRegistros);
        menu.addComponent(eliminarRegistros);
        menu.addComponent(insertarRegistros);

    }

    public static void visualizarTelefonosPorOperador() {
        TerminalUtils.clearScreen();
        int codOperador = 0;
        SendRequest sendRequest = new SendRequest();
        List<Operador> operadores = new ArrayList<>();
        sendRequest.getOperadores().thenAccept(operador -> {
            operadores.addAll(operador);
        }).join();
        System.out.printf("%-15s %s\n", "Nombre", "Código");
        System.out.println("--------------------------------------------");
        operadores.forEach(operador -> {
            System.out.printf("%-18s %s\n", operador.getNombre(), operador.getCodOperador());
        });
        System.out.println("--------------------------------------------");
        boolean valido = false;
        do {
            System.out.println("Seleccione un operador:(Introduzca codigo) ");
            try {
                codOperador = Integer.parseInt(System.console().readLine());
                final int codOperadorFinal = codOperador;
                if (operadores.stream().anyMatch(operador -> operador.getCodOperador() == codOperadorFinal)) {
                    valido = true;
                } else {
                    System.err.println("Error: Código de operador no válido");

                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número");
            }
        } while (!valido);
        TerminalUtils.clearScreen();

        List<Telefono> telefonos = new ArrayList<>();

        sendRequest.getTelefonosPorOperador(codOperador).thenAccept(telefono -> {
            if (telefono == null || telefono.isEmpty()) {
                System.out.println("No hay registros para mostrar");

            } else
                telefonos.addAll(telefono);
        }).join();
        if (!telefonos.isEmpty()) {
            System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
            System.out.println("--------------------------------------------");
            telefonos.forEach(telefono -> {
                System.out.printf("%-15s %-15s %-15s\n", telefono.getNumTelefono(), telefono.getOperador().getNombre(),
                        telefono.getTitular());
            });
        }
        // telefonos.forEach(System.out::println);

    }

    public static void visualizarTelefonosPorTitular() {
        TerminalUtils.clearScreen();
        String titular = "";
        SendRequest sendRequest = new SendRequest();
        List<Telefono> telefonos = new ArrayList<>();
        System.out.println("Introduzca el nombre del titular: ");
        titular = System.console().readLine();
        sendRequest.getTelefonosTitular(titular).thenAccept(telefono -> {
            if (telefono == null || telefono.isEmpty()) {
                System.out.println("No hay registros para mostrar");

            } else
                telefonos.addAll(telefono);
        }).join();
        if (!telefonos.isEmpty()) {
            System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
            System.out.println("--------------------------------------------");
            telefonos.forEach(telefono -> {
                System.out.printf("%-15s %-15s %-15s\n", telefono.getNumTelefono(), telefono.getOperador().getNombre(),
                        telefono.getTitular());
            });
        }

    }

    public static void visualizarHistorialNumero() {
        TerminalUtils.clearScreen();
        int numTelefono = 0;
        SendRequest sendRequest = new SendRequest();
        List<Telefono> telefonos = new ArrayList<>();
        sendRequest.getTelefonos().thenAccept(telefono -> {
            if (telefono == null || telefono.isEmpty()) {
                System.out.println("No hay registros para mostrar");

            } else
                telefonos.addAll(telefono);
        }).join();
        if (!telefonos.isEmpty()) {
            System.out.printf("%-15s\n", "Números");
            System.out.println("----------------------");
            telefonos.forEach(telefono -> {
                System.out.printf("%-15s\n", telefono.getNumTelefono());
            });
        }
        boolean valido = false;
        do {
            System.out.println("Seleccione un número de teléfono:(Introduzca número) ");
            try {
                numTelefono = Integer.parseInt(System.console().readLine());
                final int numTelefonoFinal = numTelefono;
                if (telefonos.stream().anyMatch(telefono -> telefono.getNumTelefono() == numTelefonoFinal)) {
                    valido = true;
                } else {
                    System.err.println("Error: Número de teléfono no válido");

                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número");
            }
        } while (!valido);
        TerminalUtils.clearScreen();
        List<Historial> historial = new ArrayList<>();
        sendRequest.getHistorialTelefono(numTelefono).thenAccept(hist -> {
            if (hist == null || hist.isEmpty()) {
                System.out.println("No hay registros para mostrar");

            } else
                historial.addAll(hist);
        }).join();
        if (!historial.isEmpty()) {
            System.out.printf("%-15s %-15s %-15s %-15s %-15s\n", "Id", "Número Telefono", "Operador Antiguo",
                    "Operador Nuevo", "Motivos");
            System.out.println("--------------------------------------------");
            historial.forEach(historial1 -> {
                System.out.printf("%-15s %-15s %-15s %-15s %-15s\n",
                        historial1.getId(), historial1.getTelefono().getNumTelefono(), historial1.getOperadorAntiguo().getNombre(),
                        historial1.getOperadorNuevo().getNombre());
            });

        }
    }
}
