package restclient.menu;

// Clase que envuelve un menu hijo
public class SubMenu implements MenuComponent {
    private Menu menu;

    public SubMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void display() {
        // Solo muestra el título del menu
        System.out.println(menu.getTitle());
    }

    @Override
    public void execute() {
        // delega el execute al menu
        menu.execute();
    }

    public Menu getMenu() {
        return menu;
    }
}