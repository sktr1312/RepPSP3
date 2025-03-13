package examenrest.menu;

// Clase para representar un submenú en el menú principal
public class SubMenu implements MenuComponent {
    private Menu menu;

    public SubMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void display() {
        // Solo muestra el título del menú, no su contenido
        System.out.println(menu.getTitle());
    }

    @Override
    public void execute() {
        // Al ejecutar, delega al menú real
        menu.execute();
    }

    public Menu getMenu() {
        return menu;
    }
}