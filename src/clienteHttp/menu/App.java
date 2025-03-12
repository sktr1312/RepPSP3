package clienteHttp.menu;

import clienteHttp.commands.ExitCommand;
import clienteHttp.commands.PrintCommand;

public class App {
    public static void main(String[] args) {
        // Crear menú principal
        Menu mainMenu = new Menu("Menú Principal");
        // Crear submenús
        Menu fileMenu = new Menu("Gestión de Archivos");
        Menu userMenu = new Menu("Gestión de Usuarios");
        Menu reportMenu = new Menu("Reportes");
        // Añadir opciones al submenú de archivos
        fileMenu.addComponent(new MenuItem("Crear archivo", new PrintCommand("Creando archivo...")));
        fileMenu.addComponent(new MenuItem("Abrir archivo", new PrintCommand("Abriendo archivo...")));
        fileMenu.addComponent(new MenuItem("Guardar archivo", new PrintCommand("Guardando archivo...")));
        // Añadir opciones al submenú de usuarios
        userMenu.addComponent(new MenuItem("Crear usuario", new PrintCommand("Creando usuario...")));
        userMenu.addComponent(new MenuItem("Editar usuario", new PrintCommand("Editando usuario...")));
        userMenu.addComponent(new MenuItem("Eliminar usuario", new PrintCommand("Eliminando usuario...")));
        // Añadir opciones al submenú de reportes
        reportMenu.addComponent(new MenuItem("Reporte diario", new PrintCommand("Generando reporte diario...")));
        reportMenu.addComponent(new MenuItem("Reporte mensual", new PrintCommand("Generando reporte mensual...")));

        // Añadir submenús al menú principal
        mainMenu.addComponent(new SubMenu(fileMenu));
        mainMenu.addComponent(new SubMenu(userMenu));
        mainMenu.addComponent(new SubMenu(reportMenu));
        mainMenu.addComponent(new MenuItem("Salir", new ExitCommand()));

        // Iniciar la aplicación
        mainMenu.execute();
    }
}
