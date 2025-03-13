package clienteHttp;

import java.util.ArrayList;
import java.util.List;

import clienteHttp.commands.Command;
import clienteHttp.commands.PrintCommand;
import clienteHttp.menu.Menu;
import clienteHttp.menu.MenuItem;
import clienteHttp.menu.SubMenu;
import clienteHttp.models.Operador;
import clienteHttp.models.Telefono;
import clienteHttp.utiles.TerminalUtils;
import clienteHttp.commands.SendRequest;

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
                .addComponent(new MenuItem("Ver todos los registros", App::visualizarTelefonosPorOperador));

        // submenus actualizar registros
        SubMenu actualizarRegistros = new SubMenu(new Menu("Actualizar Registros"));
        // submenus eliminar registros
        SubMenu eliminarRegistros = new SubMenu(new Menu("Eliminar Registros"));
        // submenu insertar registros
        SubMenu insertarRegistros = new SubMenu(new Menu("Insertar Registros"));
        // submenu otro
        SubMenu otro = new SubMenu(new Menu("Otro"));
        // añadir submenus al menu principal
        menu.addComponent(verRegistros);
        menu.addComponent(actualizarRegistros);
        menu.addComponent(eliminarRegistros);
        menu.addComponent(insertarRegistros);
        menu.addComponent(otro);

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
        System.out.println("Presione Enter para continuar...");
        System.console().readLine();
    }
}
