package restclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import examenrest.models.Historial;
import restclient.commands.Command;
import restclient.menu.Menu;
import restclient.menu.MenuItem;
import restclient.menu.SubMenu;
import examenrest.models.Operador;
import examenrest.models.Telefono;
import restclient.utiles.TerminalUtils;

public class App2 {
    static RestClientFacade restClient = new RestClientFacade();

    public static void main(String[] args) {
        // menu principal
        Menu menu = new Menu("Menú principal");
        // añadirSubmenus
        addSubmenus(menu);
        menu.execute();
    }

    private static void addSubmenus(Menu menu) {
        // submenus visualizar registros
        SubMenu verRegistros = createSubmenu("Visualizar Registros");
        // añadir opciones a visualizar registros

        addMenuItem(verRegistros, new MenuItem("Ver todos los operadores", App2::visualizarTelefonosPorOperador),
                new MenuItem("Ver todos los titulares", App2::visualizarTelefonosPorTitular),
                new MenuItem("Ver todos los historiales", App2::visualizarHistorialNumero),
                new MenuItem("Ver todos los telefonos", App2::visualizarTelefonos));

        // submenus actualizar registros
        SubMenu actualizarRegistros = createSubmenu("Actualizar Registros");
        // añadir opciones a actualizar registros
        addMenuItem(actualizarRegistros, new MenuItem("Actualizar operador", App2::actualizarOperador));

        // submenus eliminar registros
        SubMenu eliminarRegistros = createSubmenu("Eliminar Registros");

        // submenu insertar registros
        SubMenu insertarRegistros = createSubmenu("Insertar Registros");
        // añadir opciones a insertar registros
        addMenuItem(insertarRegistros, new MenuItem("Añadir teléfono", App2::anhadirTelefono));

        // añadir submenus al menu principal
        addSubmenuToMenu(menu, verRegistros, actualizarRegistros, eliminarRegistros, insertarRegistros);

    }

    // Métodos generalizados para crear y manipular menús
    private static SubMenu createSubmenu(String title) {
        return new SubMenu(new Menu(title));
    }

    private static void addMenuItem(SubMenu submenu, MenuItem... items) {
        for (MenuItem item : items) {
            submenu.getMenu().addComponent(item);
        }

    }

    private static void addSubmenuToMenu(Menu menu, SubMenu... submenu) {
        for (SubMenu sub : submenu)
            menu.addComponent(sub);
    }

    public static void actualizarOperador() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);
        Telefono telefono = selectItemFromList(telefonos, App2::displayTelefonosList, App2::getTelefonoInput);

        TerminalUtils.clearScreen();
        Operador operador = getOperador();
        Operador operadorAntiguo = telefono.getOperador();
        telefono.setOperador(operador);

        if (processUpdate(() -> restClient.patchTelefonoTitular(telefono), "Teléfono actualizado correctamente",
                "Error al actualizar el teléfono")) {
            TerminalUtils.clearScreen();
            String motivo = getInputWithValidation(
                    "Introduzca el motivo del cambio de operador: ",
                    input -> !input.trim().isEmpty(),
                    "El motivo no puede estar vacío");

            Historial historial = new Historial();
            historial.setTelefono(telefono);
            historial.setOperadorAntiguo(operadorAntiguo);
            historial.setOperadorNuevo(operador);
            historial.setMotivos(motivo);

            TerminalUtils.clearScreen();
            processUpdate(() -> restClient.postHistorial(historial), "Historial añadido correctamente",
                    "Error al añadir el historial");
        }
    }

    private static <T> T fetchData(Supplier<CompletableFuture<T>> dataProvider) {
        return dataProvider.get().join();
    }

    private static <T> boolean processUpdate(Supplier<CompletableFuture<Boolean>> updateOperation,
            String successMessage, String errorMessage) {
        boolean result = updateOperation.get().join();
        if (result) {
            System.out.println(successMessage);
            waitForEnter();
            return true;
        } else {
            System.err.println(errorMessage);
            return false;
        }
    }

    private static void waitForEnter() {
        System.out.println("Presione Enter para continuar...");
        System.console().readLine();
    }

    private static String getInputWithValidation(String prompt, Predicate<String> validator, String errorMessage) {
        String input;
        do {
            System.out.println(prompt);
            input = System.console().readLine();
            if (!validator.test(input)) {
                System.err.println(errorMessage);
            }
        } while (!validator.test(input));
        return input;
    }

    private static int getIntInputWithValidation(String prompt, Predicate<Integer> validator, String errorMessage) {
        int input = 0;
        boolean valid = false;
        do {
            System.out.println(prompt);
            try {
                input = Integer.parseInt(System.console().readLine());
                if (validator.test(input)) {
                    valid = true;
                } else {
                    System.err.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número");
            }
        } while (!valid);
        return input;
    }

    private static <T> T selectItemFromList(List<T> items, Consumer<List<T>> displayMethod,
            Supplier<T> selectionMethod) {
        displayMethod.accept(items);
        return selectionMethod.get();
    }

    private static void displayTelefonosList(List<Telefono> telefonos) {
        System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
        System.out.println("--------------------------------------------");
        telefonos.forEach(telefono -> {
            System.out.printf("%-15s %-15s %-15s\n", telefono.getNumTelefono(), telefono.getOperador().getNombre(),
                    telefono.getTitular());
        });
    }

    private static Telefono getTelefonoInput() {
        return getTelefonoByNumber();
    }

    private static Telefono getTelefonoByNumber() {
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);
        int numTelefono = getIntInputWithValidation(
                "Introduzca el número de teléfono: ",
                num -> telefonos.stream().anyMatch(t -> t.getNumTelefono() == num),
                "Error: Número de teléfono no válido");
        return telefonos.get(telefonos.indexOf(new Telefono(numTelefono, null, "")));
    }

    public static void anhadirTelefono() {
        TerminalUtils.clearScreen();
        Telefono telefono = new Telefono();

        // Establecer operador
        Operador operador = getOperador();
        telefono.setOperador(operador);

        // Obtener número de teléfono
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);
        int numTelefono = getIntInputWithValidation(
                "Introduzca el número de teléfono: ",
                num -> Integer.toString(num).length() == 9
                        && telefonos.stream().noneMatch(t -> t.getNumTelefono() == num),
                "Error: El número de teléfono debe tener 9 dígitos y no debe existir");
        telefono.setNumTelefono(numTelefono);

        // Establecer titular
        String titular = getTitular();
        telefono.setTitular(titular);

        // Guardar teléfono
        processUpdate(() -> restClient.postTelefono(telefono), "Teléfono añadido correctamente",
                "Error al añadir el teléfono");
    }

    public static void visualizarTelefonos() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);
        displayData(telefonos, App2::displayTelefonosList);
    }

    public static void visualizarTelefonosPorOperador() {
        TerminalUtils.clearScreen();
        int codOperador = getOperador().getCodOperador();
        fetchAndDisplayData(
                () -> restClient.getTelefonosPorOperador(codOperador),
                App2::displayTelefonosList,
                "No hay registros para mostrar");
    }

    private static <T> void displayData(List<T> data, Consumer<List<T>> displayMethod) {
        if (data == null || data.isEmpty()) {
            System.out.println("No hay registros para mostrar");
        } else {
            displayMethod.accept(data);
        }
    }

    private static <T> void fetchAndDisplayData(Supplier<CompletableFuture<List<T>>> dataProvider,
            Consumer<List<T>> displayMethod,
            String emptyMessage) {
        List<T> data = new ArrayList<>();
        dataProvider.get().thenAccept(result -> {
            if (result == null || result.isEmpty()) {
                System.out.println(emptyMessage);
            } else {
                data.addAll(result);
            }
        }).join();

        if (!data.isEmpty()) {
            displayMethod.accept(data);
        }
    }

    private static Operador getOperador() {
        List<Operador> operadores = fetchData(restClient::getOperadores);
        return selectItemFromList(operadores, App2::displayOperadoresList, () -> selectOperadorByCode(operadores));
    }

    private static void displayOperadoresList(List<Operador> operadores) {
        System.out.printf("%-15s %s\n", "Nombre", "Código");
        System.out.println("--------------------------------------------");
        operadores.forEach(operador -> {
            System.out.printf("%-18s %s\n", operador.getNombre(), operador.getCodOperador());
        });
        System.out.println("--------------------------------------------");
    }

    private static Operador selectOperadorByCode(List<Operador> operadores) {
        int codOperador = getIntInputWithValidation(
                "Seleccione un operador:(Introduzca codigo) ",
                code -> operadores.stream().anyMatch(op -> op.getCodOperador() == code),
                "Error: Código de operador no válido");
        TerminalUtils.clearScreen();
        return operadores.get(operadores.indexOf(new Operador(codOperador, "")));
    }

    private static String getTitular() {
        Set<String> titulares = new HashSet<>();
        fetchData(restClient::getTitulares).forEach(titulares::add);

        if (titulares.isEmpty()) {
            System.err.println("No hay registros para mostrar");
            return "";
        }

        return selectItemFromList(
                new ArrayList<>(titulares),
                App2::displayTitularesList,
                () -> selectTitularByNombre(titulares));
    }

    private static void displayTitularesList(List<String> titulares) {
        System.out.printf("%-15s\n", "Titulares");
        System.out.println("--------------------------------------------");
        titulares.forEach(System.out::println);
    }

    private static String selectTitularByNombre(Set<String> titulares) {
        String titular = getInputWithValidation(
                "Introduzca el nombre del titular: ",
                titulares::contains,
                "Error: Titular no válido");
        TerminalUtils.clearScreen();
        return titular;
    }

    public static void visualizarTelefonosPorTitular() {
        TerminalUtils.clearScreen();
        String titular = getTitular();

        if (titular.isEmpty()) {
            return;
        }

        String encodedTitular = encodeTitular(titular);
        if (encodedTitular == null) {
            return;
        }

        fetchAndDisplayData(
                () -> restClient.getTelefonosTitular(encodedTitular),
                App2::displayTelefonosList,
                "No hay registros para mostrar");
    }

    private static String encodeTitular(String titular) {
        if (titular.trim().contains(" ")) {
            try {
                return URLEncoder.encode(titular, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println("Error al codificar el titular");
                return null;
            }
        }
        return titular;
    }

    public static void visualizarHistorialNumero() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);

        if (telefonos.isEmpty()) {
            System.out.println("No hay registros para mostrar");
            return;
        }

        displayNumerosList(telefonos);
        int numTelefono = selectNumeroTelefono(telefonos);

        TerminalUtils.clearScreen();
        fetchAndDisplayData(() -> restClient.getHistorialTelefono(numTelefono),
                App2::displayHistorialList,
                "No hay registros para mostrar");
    }

    private static void displayNumerosList(List<Telefono> telefonos) {
        System.out.printf("%-15s\n", "Números");
        System.out.println("----------------------");
        telefonos.forEach(telefono -> {
            System.out.printf("%-15s\n", telefono.getNumTelefono());
        });
    }

    private static int selectNumeroTelefono(List<Telefono> telefonos) {
        return getIntInputWithValidation(
                "Seleccione un número de teléfono:(Introduzca número) ",
                num -> telefonos.stream().anyMatch(t -> t.getNumTelefono() == num),
                "Error: Número de teléfono no válido");
    }

    public static void displayHistorialList(List<Historial> historial) {
        System.out.printf("%-5s %-20s %-20s %-20s %-20s\n", "Id", "Número Telefono", "Operador Antiguo",
                "Operador Nuevo", "Motivos");
        System.out.println("--------------------------------------------");
        historial.forEach(historial1 -> {
            System.out.printf("%-5s %-20s %-20s %-20s %-20s\n",
                    historial1.getId(), historial1.getTelefono().getNumTelefono(),
                    historial1.getOperadorAntiguo().getNombre(),
                    historial1.getOperadorNuevo().getNombre(),
                    historial1.getMotivos());
        });
    }
}