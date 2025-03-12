package clienteHttp;

import java.util.List;

import clienteHttp.commands.Command;
import clienteHttp.commands.PrintCommand;
import clienteHttp.menu.Menu;
import clienteHttp.menu.MenuItem;
import clienteHttp.menu.SubMenu;
import clienteHttp.models.Telefono;

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
        // submenus actualizar registros
        SubMenu actualizarRegistros = new SubMenu(new Menu("Actualizar Registros"));
        // submenus eliminar registros
        SubMenu eliminarRegistros = new SubMenu(new Menu("Eliminar Registros"));
        // submenu insertar registros
        SubMenu insertarRegistros = new SubMenu(new Menu("Insertar Registros"));
        //submenu otro
        SubMenu otro = new SubMenu(new Menu("Otro"));
        // añadir submenus al menu principal
        menu.addComponent(verRegistros);
        menu.addComponent(actualizarRegistros);
        menu.addComponent(eliminarRegistros);
        menu.addComponent(insertarRegistros);
        menu.addComponent(otro);
        // añadir opciones a visualizar registros
        verRegistros.getMenu().addComponent(new MenuItem("Ver todos los registros", App::verTodosLosRegistros));

    }

    public static void verTodosLosRegistros() {
        SendRequest sendRequest = new SendRequest();
        List<Telefono> telefonos = sendRequest.sendGetTelefonosRequest();
        telefonos.forEach(System.out::println);
        System.out.println("Presione Enter para continuar...");
        System.console().readLine();
    }
}
