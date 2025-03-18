package restclient.menu;

// Clase que envuelve un menu hijo
public class SubMenu implements MenuComponent {
    private Menu menu;

    public SubMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void display() {
        System.out.println(menu.getTitle());
    }

    @Override
    public void execute() {
        menu.execute();
    }

    public Menu getMenu() {
        return menu;
    }
}