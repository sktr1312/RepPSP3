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

/**
 * Aplicación cliente para consumir servicios REST relacionados con la gestión de teléfonos.
 * Implementa un sistema de menús para facilitar la interacción con los servicios.
 */
public class AppDocumentada {
    /** Cliente REST utilizado para realizar peticiones al servidor */
    static RestClientFacade restClient = new RestClientFacade();

    /**
     * Punto de entrada de la aplicación.
     * Crea y ejecuta el menú principal con todas sus opciones.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        Menu menu = new Menu("Menú principal");
        addSubmenus(menu);
        menu.execute();
    }

    // ==================== CONFIGURACIÓN DE MENÚS ====================

    /**
     * Configura la estructura de menús de la aplicación.
     * 
     * @param menu El menú principal al que se añadirán los submenús
     */
    private static void addSubmenus(Menu menu) {
        // Submenú para visualizar registros
        SubMenu verRegistros = createSubmenu("Visualizar Registros");
        addMenuItem(verRegistros, 
                new MenuItem("Ver todos los operadores", AppDocumentada::visualizarTelefonosPorOperador),
                new MenuItem("Ver todos los titulares", AppDocumentada::visualizarTelefonosPorTitular),
                new MenuItem("Ver todos los historiales", AppDocumentada::visualizarHistorialNumero),
                new MenuItem("Ver todos los telefonos", AppDocumentada::visualizarTelefonos));

        // Submenú para actualizar registros
        SubMenu actualizarRegistros = createSubmenu("Actualizar Registros");
        addMenuItem(actualizarRegistros, 
                new MenuItem("Actualizar operador", AppDocumentada::actualizarOperador));

        // Submenú para eliminar registros (sin opciones implementadas)
        SubMenu eliminarRegistros = createSubmenu("Eliminar Registros");

        // Submenú para insertar registros
        SubMenu insertarRegistros = createSubmenu("Insertar Registros");
        addMenuItem(insertarRegistros, 
                new MenuItem("Añadir teléfono", AppDocumentada::anhadirTelefono));

        // Añadir todos los submenús al menú principal
        addSubmenuToMenu(menu, verRegistros, actualizarRegistros, eliminarRegistros, insertarRegistros);
    }

    /**
     * Crea un nuevo submenú con el título especificado.
     * 
     * @param title Título del submenú
     * @return SubMenu creado
     */
    private static SubMenu createSubmenu(String title) {
        return new SubMenu(new Menu(title));
    }

    /**
     * Añade elementos de menú a un submenú.
     * 
     * @param submenu Submenú al que se añadirán los elementos
     * @param items Elementos a añadir
     */
    private static void addMenuItem(SubMenu submenu, MenuItem... items) {
        for (MenuItem item : items) {
            submenu.getMenu().addComponent(item);
        }
    }

    /**
     * Añade submenús al menú principal.
     * 
     * @param menu Menú principal
     * @param submenu Array de submenús a añadir
     */
    private static void addSubmenuToMenu(Menu menu, SubMenu... submenu) {
        for (SubMenu sub : submenu)
            menu.addComponent(sub);
    }

    // ==================== FUNCIONALIDADES PRINCIPALES ====================

    /**
     * Actualiza el operador de un teléfono y registra el cambio en el historial.
     */
    public static void actualizarOperador() {
        TerminalUtils.clearScreen();
        
        // Seleccionar teléfono a actualizar
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);
        Telefono telefono = selectItemFromList(telefonos, AppDocumentada::displayTelefonosList, AppDocumentada::getTelefonoInput);

        // Seleccionar nuevo operador
        TerminalUtils.clearScreen();
        Operador operador = getOperador();
        Operador operadorAntiguo = telefono.getOperador();
        telefono.setOperador(operador);

        // Actualizar teléfono
        if (processUpdate(() -> restClient.patchTelefonoTitular(telefono), 
                "Teléfono actualizado correctamente",
                "Error al actualizar el teléfono")) {
            
            // Registrar cambio en historial
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
            processUpdate(() -> restClient.postHistorial(historial), 
                    "Historial añadido correctamente",
                    "Error al añadir el historial");
        }
    }

    /**
     * Añade un nuevo teléfono al sistema.
     */
    public static void anhadirTelefono() {
        TerminalUtils.clearScreen();
        Telefono telefono = new Telefono();

        // Establecer operador
        Operador operador = getOperador();
        telefono.setOperador(operador);

        // Obtener número de teléfono (validando que sea único y de 9 dígitos)
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
        processUpdate(() -> restClient.postTelefono(telefono), 
                "Teléfono añadido correctamente",
                "Error al añadir el teléfono");
    }

    // ==================== FUNCIONES DE VISUALIZACIÓN ====================

    /**
     * Muestra todos los teléfonos disponibles.
     */
    public static void visualizarTelefonos() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);
        displayData(telefonos, AppDocumentada::displayTelefonosList);
    }

    /**
     * Muestra los teléfonos filtrados por operador.
     */
    public static void visualizarTelefonosPorOperador() {
        TerminalUtils.clearScreen();
        int codOperador = getOperador().getCodOperador();
        fetchAndDisplayData(
                () -> restClient.getTelefonosPorOperador(codOperador),
                AppDocumentada::displayTelefonosList,
                "No hay registros para mostrar");
    }

    /**
     * Muestra los teléfonos filtrados por titular.
     */
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
                AppDocumentada::displayTelefonosList,
                "No hay registros para mostrar");
    }

    /**
     * Muestra el historial de cambios de operador para un teléfono específico.
     */
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
                AppDocumentada::displayHistorialList,
                "No hay registros para mostrar");
    }

    // ==================== UTILIDADES DE DATOS ====================

    /**
     * Obtiene datos de forma asíncrona utilizando el proveedor especificado.
     * 
     * @param <T> Tipo de datos a obtener
     * @param dataProvider Proveedor de los datos
     * @return Datos obtenidos
     */
    private static <T> T fetchData(Supplier<CompletableFuture<T>> dataProvider) {
        return dataProvider.get().join();
    }

    /**
     * Realiza una operación de actualización y maneja los mensajes de éxito/error.
     * 
     * @param updateOperation Operación de actualización a realizar
     * @param successMessage Mensaje a mostrar en caso de éxito
     * @param errorMessage Mensaje a mostrar en caso de error
     * @return true si la operación fue exitosa, false en caso contrario
     */
    private static boolean processUpdate(Supplier<CompletableFuture<Boolean>> updateOperation,
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

    /**
     * Obtiene datos de forma asíncrona y los muestra utilizando el método especificado.
     * 
     * @param <T> Tipo de datos a obtener
     * @param dataProvider Proveedor de los datos
     * @param displayMethod Método para mostrar los datos
     * @param emptyMessage Mensaje a mostrar si no hay datos
     */
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

    /**
     * Muestra datos utilizando el método especificado.
     * 
     * @param <T> Tipo de datos a mostrar
     * @param data Datos a mostrar
     * @param displayMethod Método para mostrar los datos
     */
    private static <T> void displayData(List<T> data, Consumer<List<T>> displayMethod) {
        if (data == null || data.isEmpty()) {
            System.out.println("No hay registros para mostrar");
        } else {
            displayMethod.accept(data);
        }
    }

    /**
     * Codifica un titular para su uso en URLs.
     * 
     * @param titular Titular a codificar
     * @return Titular codificado o null si hubo un error
     */
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

    // ==================== UTILIDADES DE INTERFAZ DE USUARIO ====================

    /**
     * Espera a que el usuario presione Enter para continuar.
     */
    private static void waitForEnter() {
        System.out.println("Presione Enter para continuar...");
        System.console().readLine();
    }

    /**
     * Obtiene una entrada de texto del usuario con validación.
     * 
     * @param prompt Mensaje a mostrar al usuario
     * @param validator Validador para la entrada
     * @param errorMessage Mensaje de error a mostrar si la entrada no es válida
     * @return Entrada validada
     */
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

    /**
     * Obtiene una entrada numérica del usuario con validación.
     * 
     * @param prompt Mensaje a mostrar al usuario
     * @param validator Validador para la entrada
     * @param errorMessage Mensaje de error a mostrar si la entrada no es válida
     * @return Entrada numérica validada
     */
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

    /**
     * Selecciona un elemento de una lista utilizando los métodos especificados.
     * 
     * @param <T> Tipo de elementos en la lista
     * @param items Lista de elementos
     * @param displayMethod Método para mostrar los elementos
     * @param selectionMethod Método para seleccionar un elemento
     * @return Elemento seleccionado
     */
    private static <T> T selectItemFromList(List<T> items, Consumer<List<T>> displayMethod,
            Supplier<T> selectionMethod) {
        displayMethod.accept(items);
        return selectionMethod.get();
    }

    // ==================== MÉTODOS DE VISUALIZACIÓN DE LISTAS ====================

    /**
     * Muestra una lista de teléfonos.
     * 
     * @param telefonos Lista de teléfonos a mostrar
     */
    private static void displayTelefonosList(List<Telefono> telefonos) {
        System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
        System.out.println("--------------------------------------------");
        telefonos.forEach(telefono -> {
            System.out.printf("%-15s %-15s %-15s\n", telefono.getNumTelefono(), telefono.getOperador().getNombre(),
                    telefono.getTitular());
        });
    }

    /**
     * Muestra una lista de operadores.
     * 
     * @param operadores Lista de operadores a mostrar
     */
    private static void displayOperadoresList(List<Operador> operadores) {
        System.out.printf("%-15s %s\n", "Nombre", "Código");
        System.out.println("--------------------------------------------");
        operadores.forEach(operador -> {
            System.out.printf("%-18s %s\n", operador.getNombre(), operador.getCodOperador());
        });
        System.out.println("--------------------------------------------");
    }

    /**
     * Muestra una lista de titulares.
     * 
     * @param titulares Lista de titulares a mostrar
     */
    private static void displayTitularesList(List<String> titulares) {
        System.out.printf("%-15s\n", "Titulares");
        System.out.println("--------------------------------------------");
        titulares.forEach(System.out::println);
    }

    /**
     * Muestra una lista de números de teléfono.
     * 
     * @param telefonos Lista de teléfonos de los que extraer los números
     */
    private static void displayNumerosList(List<Telefono> telefonos) {
        System.out.printf("%-15s\n", "Números");
        System.out.println("----------------------");
        telefonos.forEach(telefono -> {
            System.out.printf("%-15s\n", telefono.getNumTelefono());
        });
    }

    /**
     * Muestra una lista de registros de historial.
     * 
     * @param historial Lista de registros de historial a mostrar
     */
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

    // ==================== MÉTODOS DE SELECCIÓN DE ELEMENTOS ====================

    /**
     * Obtiene un teléfono por su número.
     * 
     * @return Teléfono seleccionado
     */
    private static Telefono getTelefonoInput() {
        return getTelefonoByNumber();
    }

    /**
     * Obtiene un teléfono por su número.
     * 
     * @return Teléfono seleccionado
     */
    private static Telefono getTelefonoByNumber() {
        List<Telefono> telefonos = fetchData(restClient::getTelefonos);
        int numTelefono = getIntInputWithValidation(
                "Introduzca el número de teléfono: ",
                num -> telefonos.stream().anyMatch(t -> t.getNumTelefono() == num),
                "Error: Número de teléfono no válido");
        return telefonos.get(telefonos.indexOf(new Telefono(numTelefono, null, "")));
    }

    /**
     * Obtiene un operador de la lista de operadores disponibles.
     * 
     * @return Operador seleccionado
     */
    private static Operador getOperador() {
        List<Operador> operadores = fetchData(restClient::getOperadores);
        return selectItemFromList(operadores, AppDocumentada::displayOperadoresList, () -> selectOperadorByCode(operadores));
    }

    /**
     * Selecciona un operador por su código.
     * 
     * @param operadores Lista de operadores disponibles
     * @return Operador seleccionado
     */
    private static Operador selectOperadorByCode(List<Operador> operadores) {
        int codOperador = getIntInputWithValidation(
                "Seleccione un operador:(Introduzca codigo) ",
                code -> operadores.stream().anyMatch(op -> op.getCodOperador() == code),
                "Error: Código de operador no válido");
        TerminalUtils.clearScreen();
        return operadores.get(operadores.indexOf(new Operador(codOperador, "")));
    }

    /**
     * Obtiene un titular de la lista de titulares disponibles.
     * 
     * @return Titular seleccionado
     */
    private static String getTitular() {
        Set<String> titulares = new HashSet<>();
        fetchData(restClient::getTitulares).forEach(titulares::add);

        if (titulares.isEmpty()) {
            System.err.println("No hay registros para mostrar");
            return "";
        }

        return selectItemFromList(
                new ArrayList<>(titulares),
                AppDocumentada::displayTitularesList,
                () -> selectTitularByNombre(titulares));
    }

    /**
     * Selecciona un titular por su nombre.
     * 
     * @param titulares Conjunto de titulares disponibles
     * @return Titular seleccionado
     */
    private static String selectTitularByNombre(Set<String> titulares) {
        String titular = getInputWithValidation(
                "Introduzca el nombre del titular: ",
                titulares::contains,
                "Error: Titular no válido");
        TerminalUtils.clearScreen();
        return titular;
    }

    /**
     * Selecciona un número de teléfono de una lista de teléfonos.
     * 
     * @param telefonos Lista de teléfonos disponibles
     * @return Número de teléfono seleccionado
     */
    private static int selectNumeroTelefono(List<Telefono> telefonos) {
        return getIntInputWithValidation(
                "Seleccione un número de teléfono:(Introduzca número) ",
                num -> telefonos.stream().anyMatch(t -> t.getNumTelefono() == num),
                "Error: Número de teléfono no válido");
    }
}