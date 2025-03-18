package restclient.utiles;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import restclient.menu.Menu;
import restclient.menu.MenuItem;
import restclient.menu.SubMenu;

public class AplicacionUtils {
    // region UTILIDADES DE MENÚS
    // ==================== UTILIDADES DE MENÚS ====================
    /**
     * Crea un nuevo submenú con el título especificado.
     * 
     * @param title Título del submenú
     * @return SubMenu creado
     */
    public static SubMenu createSubmenu(String title) {
        return new SubMenu(new Menu(title));
    }

    /**
     * Añade elementos de menú a un submenú.
     * 
     * @param submenu Submenú al que se añadirán los elementos
     * @param items   Elementos a añadir
     */
    public static void addMenuItem(SubMenu submenu, MenuItem... items) {
        for (MenuItem item : items) {
            submenu.getMenu().addComponent(item);
        }
    }

    /**
     * Añade submenús al menú principal.
     * 
     * @param menu    Menú principal
     * @param submenu Array de submenús a añadir
     */
    public static void addSubmenuToMenu(Menu menu, SubMenu... submenu) {
        for (SubMenu sub : submenu)
            menu.addComponent(sub);
    }

    // ==================== FIN UTILIDADES DE MENÚS ====================
    // endregion

    // region UTILIDADES DE DATOS
    // ==================== UTILIDADES DE DATOS ====================
    /**
     * Realiza una operación de actualización y maneja los mensajes de éxito/error.
     * 
     * @param updateOperation Operación de actualización a realizar
     * @param successMessage  Mensaje a mostrar en caso de éxito
     * @param errorMessage    Mensaje a mostrar en caso de error
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public static boolean processUpdate(Supplier<CompletableFuture<Boolean>> updateOperation,
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
     * Obtiene datos de forma asíncrona y los muestra utilizando el método
     * especificado.
     * 
     * @param <T>           Tipo de datos a obtener
     * @param dataProvider  Proveedor de los datos
     * @param displayMethod Método para mostrar los datos
     * @param emptyMessage  Mensaje a mostrar si no hay datos
     */
    public static <T> void fetchAndDisplayData(Supplier<CompletableFuture<List<T>>> dataProvider,
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
     * @param <T>           Tipo de datos a mostrar
     * @param data          Datos a mostrar
     * @param displayMethod Método para mostrar los datos
     */
    public static <T> void displayData(List<T> data, Consumer<List<T>> displayMethod) {
        if (data == null || data.isEmpty()) {
            System.out.println("No hay registros para mostrar");
        } else {
            displayMethod.accept(data);
        }
    }

    /**
     * Codifica una cadena para su uso en URLs.
     * 
     * @param string Cadena a codificar
     * @return String codificado o null si hubo un error
     */
    public static String encodeString(String string) {
        if (string.trim().contains(" ")) {
            try {
                return URLEncoder.encode(string, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println("Error al codificar el titular");
                return null;
            }
        }
        return string;
    }

    /**
     * Extrae la data de una respuesta JSON.
     */
    public static <T> T fetchData(Supplier<CompletableFuture<T>> dataProvider) {
        return dataProvider.get().join();
    }

    // ==================== FIN UTILIDADES DE DATOS ====================
    // endregion

    // region UTILIDADES DE INTERFAZ DE USUARIO
    /**
     * Espera a que el usuario presione Enter para continuar.
     */
    public static void waitForEnter() {
        System.out.println("Presione Enter para continuar...");
        System.console().readLine();
    }

    /**
     * Obtiene una entrada de texto del usuario con validación.
     * 
     * @param prompt       Mensaje a mostrar al usuario
     * @param validator    Validador para la entrada
     * @param errorMessage Mensaje de error a mostrar si la entrada no es válida
     * @return Entrada validada
     */
    public static String getInputWithValidation(String prompt, Predicate<String> validator, String errorMessage) {
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
     * @param prompt       Mensaje a mostrar al usuario
     * @param validator    Validador para la entrada
     * @param errorMessage Mensaje de error a mostrar si la entrada no es válida
     * @return Entrada numérica validada
     */
    public static int getIntInputWithValidation(String prompt, Predicate<Integer> validator, String errorMessage) {
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
     * @param <T>             Tipo de elementos en la lista
     * @param items           Lista de elementos
     * @param displayMethod   Método para mostrar los elementos
     * @param selectionMethod Método para seleccionar un elemento
     * @return Elemento seleccionado
     */
    @SuppressWarnings("unused")
    public static <T> T selectItemFromList(List<T> items, Consumer<List<T>> displayMethod,
            Supplier<T> selectionMethod) {
        displayMethod.accept(items);
        return selectionMethod.get();
    }

    // ==================== FIN UTILIDADES DE INTERFAZ DE USUARIO
    // ====================

    // endregion
}
