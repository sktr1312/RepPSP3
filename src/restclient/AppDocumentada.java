package restclient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import examenrest.models.Historial;
import restclient.menu.Menu;
import restclient.menu.MenuItem;
import restclient.menu.SubMenu;
import examenrest.models.Operador;
import examenrest.models.Telefono;
import restclient.utiles.AplicacionUtils;
import restclient.utiles.TerminalUtils;

/**
 * Aplicación cliente para consumir servicios REST relacionados con la gestión
 * de teléfonos.
 * Implementa un sistema de menús para facilitar la interacción con los
 * servicios.
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
        SubMenu verRegistros = AplicacionUtils.createSubmenu("Visualizar Registros");
        AplicacionUtils.addMenuItem(verRegistros,
                new MenuItem("Ver todos los operadores", AppDocumentada::visualizarTelefonosPorOperador),
                new MenuItem("Ver todos los titulares", AppDocumentada::visualizarTelefonosPorTitular),
                new MenuItem("Ver todos los historiales", AppDocumentada::visualizarHistorialNumero),
                new MenuItem("Ver todos los telefonos", AppDocumentada::visualizarTelefonos));

        // Submenú para actualizar registros
        SubMenu actualizarRegistros = AplicacionUtils.createSubmenu("Actualizar Registros");
        AplicacionUtils.addMenuItem(actualizarRegistros,
                new MenuItem("Actualizar operador", AppDocumentada::actualizarOperador));

        // Submenú para eliminar registros (sin opciones implementadas)
        SubMenu eliminarRegistros = AplicacionUtils.createSubmenu("Eliminar Registros");

        // Submenú para insertar registros
        SubMenu insertarRegistros = AplicacionUtils.createSubmenu("Insertar Registros");
        AplicacionUtils.addMenuItem(insertarRegistros,
                new MenuItem("Añadir teléfono", AppDocumentada::anhadirTelefono));

        // Añadir todos los submenús al menú principal
        AplicacionUtils.addSubmenuToMenu(menu, verRegistros, actualizarRegistros, eliminarRegistros, insertarRegistros);
    }

    /**
     * Actualiza el operador de un teléfono y registra el cambio en el historial.
     */
    public static void actualizarOperador() {
        TerminalUtils.clearScreen();

        // Seleccionar teléfono a actualizar
        List<Telefono> telefonos = AplicacionUtils.fetchData(restClient::getTelefonos);
        Telefono telefono = AplicacionUtils.selectItemFromList(telefonos, AppDocumentada::displayTelefonosList,
                AppDocumentada::getTelefonoInput);

        // Seleccionar nuevo operador
        TerminalUtils.clearScreen();
        Operador operador = getOperador();
        Operador operadorAntiguo = telefono.getOperador();
        telefono.setOperador(operador);

        // Actualizar teléfono
        if (AplicacionUtils.processUpdate(() -> restClient.patchTelefonoTitular(telefono),
                "Teléfono actualizado correctamente",
                "Error al actualizar el teléfono")) {

            // Registrar cambio en historial
            TerminalUtils.clearScreen();
            String motivo = AplicacionUtils.getInputWithValidation(
                    "Introduzca el motivo del cambio de operador: ",
                    input -> !input.trim().isEmpty(),
                    "El motivo no puede estar vacío");

            Historial historial = new Historial();
            historial.setTelefono(telefono);
            historial.setOperadorAntiguo(operadorAntiguo);
            historial.setOperadorNuevo(operador);
            historial.setMotivos(motivo);

            TerminalUtils.clearScreen();
            AplicacionUtils.processUpdate(() -> restClient.postHistorial(historial),
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
        List<Telefono> telefonos = AplicacionUtils.fetchData(restClient::getTelefonos);
        int numTelefono = AplicacionUtils.getIntInputWithValidation(
                "Introduzca el número de teléfono: ",
                num -> Integer.toString(num).length() == 9
                        && telefonos.stream().noneMatch(t -> t.getNumTelefono() == num),
                "Error: El número de teléfono debe tener 9 dígitos y no debe existir");
        telefono.setNumTelefono(numTelefono);

        // Establecer titular
        String titular = getTitular();
        telefono.setTitular(titular);

        // Guardar teléfono
        AplicacionUtils.processUpdate(() -> restClient.postTelefono(telefono),
                "Teléfono añadido correctamente",
                "Error al añadir el teléfono");
    }

    // ==================== FUNCIONES DE VISUALIZACIÓN ====================

    /**
     * Muestra todos los teléfonos disponibles.
     */
    public static void visualizarTelefonos() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = AplicacionUtils.fetchData(restClient::getTelefonos);
        AplicacionUtils.displayData(telefonos, AppDocumentada::displayTelefonosList);
    }

    /**
     * Muestra los teléfonos filtrados por operador.
     */
    public static void visualizarTelefonosPorOperador() {
        TerminalUtils.clearScreen();
        int codOperador = getOperador().getCodOperador();
        AplicacionUtils.fetchAndDisplayData(
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

        String encodedTitular = AplicacionUtils.encodeString(titular);
        if (encodedTitular == null) {
            return;
        }

        AplicacionUtils.fetchAndDisplayData(
                () -> restClient.getTelefonosTitular(encodedTitular),
                AppDocumentada::displayTelefonosList,
                "No hay registros para mostrar");
    }

    /**
     * Muestra el historial de cambios de operador para un teléfono específico.
     */
    public static void visualizarHistorialNumero() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = AplicacionUtils.fetchData(restClient::getTelefonos);

        if (telefonos.isEmpty()) {
            System.out.println("No hay registros para mostrar");
            return;
        }

        displayNumerosList(telefonos);
        int numTelefono = selectNumeroTelefono(telefonos);

        TerminalUtils.clearScreen();
        AplicacionUtils.fetchAndDisplayData(() -> restClient.getHistorialTelefono(numTelefono),
                AppDocumentada::displayHistorialList,
                "No hay registros para mostrar");
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
        List<Telefono> telefonos = AplicacionUtils.fetchData(restClient::getTelefonos);
        int numTelefono = AplicacionUtils.getIntInputWithValidation(
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
        List<Operador> operadores = AplicacionUtils.fetchData(restClient::getOperadores);
        return AplicacionUtils.selectItemFromList(operadores, AppDocumentada::displayOperadoresList,
                () -> selectOperadorByCode(operadores));
    }

    /**
     * Selecciona un operador por su código.
     * 
     * @param operadores Lista de operadores disponibles
     * @return Operador seleccionado
     */
    private static Operador selectOperadorByCode(List<Operador> operadores) {
        int codOperador = AplicacionUtils.getIntInputWithValidation(
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
        AplicacionUtils.fetchData(restClient::getTitulares).forEach(titulares::add);

        if (titulares.isEmpty()) {
            System.err.println("No hay registros para mostrar");
            return "";
        }

        return AplicacionUtils.selectItemFromList(
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
        String titular = AplicacionUtils.getInputWithValidation(
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
        return AplicacionUtils.getIntInputWithValidation(
                "Seleccione un número de teléfono:(Introduzca número) ",
                num -> telefonos.stream().anyMatch(t -> t.getNumTelefono() == num),
                "Error: Número de teléfono no válido");
    }
}