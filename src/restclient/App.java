package restclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import restclient.utiles.TerminalUtils;

public class App {
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
        addMenuItem(verRegistros,
                new MenuItem("Ver todos los operadores", App::visualizarTelefonosPorOperador),
                new MenuItem("Ver todos los titulares", App::visualizarTelefonosPorTitular),
                new MenuItem("Ver todos los historiales", App::visualizarHistorialNumero),
                new MenuItem("Ver todos los telefonos", App::visualizarTelefonos));
        // submenus actualizar registros
        SubMenu actualizarRegistros = createSubmenu("Actualizar Registros");
        // añadir opciones a actualizar registros
        addMenuItem(actualizarRegistros,
                new MenuItem("Actualizar operador", App::actualizarOperador));
        // submenu insertar registros
        SubMenu insertarRegistros = createSubmenu("Insertar Registros");
        // añadir opciones a insertar registros
        addMenuItem(insertarRegistros, new MenuItem("Añadir teléfono", App::anhadirTelefono));
        // añadir submenus al menu principal
        addSubmenuToMenu(menu, verRegistros, actualizarRegistros, insertarRegistros);

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
        List<Telefono> telefonos = restClient.getTelefonos().join();
        Telefono telefono = getTelefono(telefonos);
        TerminalUtils.clearScreen();
        Operador operador = getOperador();
        Operador operadorAntiguo = telefono.getOperador();
        telefono.setOperador(operador);
        if (restClient.patchTelefonoTitular(telefono).join()) {
            System.out.println("Teléfono actualizado correctamente");
            System.out.println("Presione Enter para continuar...");
            System.console().readLine();
            TerminalUtils.clearScreen();
            String motivo = "";
            do {
                System.out.println("Introduzca el motivo del cambio de operador: ");
                motivo = System.console().readLine();
                if (!motivo.trim().isEmpty()) {
                    Historial historial = new Historial();
                    historial.setTelefono(telefono);
                    historial.setOperadorAntiguo(operadorAntiguo);
                    historial.setOperadorNuevo(operador);
                    historial.setMotivos(motivo);
                    TerminalUtils.clearScreen();
                    restClient.postHistorial(historial).thenAccept(create -> {
                        if (create) {
                            System.out.println("Historial añadido correctamente");
                        } else {
                            System.err.println("Error al añadir el historial");
                        }
                    });
                }
            } while (motivo.trim().isEmpty());
        } else {
            System.err.println("Error al actualizar el teléfono");
        }
    }

    private static Telefono getTelefono(List<Telefono> telefonos) {
        System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
        System.out.println("--------------------------------------------");
        telefonos.forEach(telefono -> {
            System.out.printf("%-15s %-15s %-15s\n", telefono.getNumTelefono(), telefono.getOperador().getNombre(),
                    telefono.getTitular());
        });
        int numTelefono = 0;
        boolean valido = false;
        do {
            System.out.println("Introduzca el número de teléfono: ");
            try {
                numTelefono = Integer.parseInt(System.console().readLine());
                final int numTelefonoFinal = numTelefono;
                if (telefonos.stream().anyMatch(telefono -> telefono.getNumTelefono() == numTelefonoFinal)) {
                    valido = true;
                } else {
                    System.err.println("Error: Número de teléfono no válido");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número");
            }
        } while (!valido);
        return telefonos.get(telefonos.indexOf(new Telefono(numTelefono, null, "")));
    }

    public static void anhadirTelefono() {
        TerminalUtils.clearScreen();
        Telefono telefono = new Telefono();
        Operador operador = getOperador();
        telefono.setOperador(operador);
        List<Telefono> telefonos = restClient.getTelefonos().join();
        int numTelefono = 0;
        boolean valido = false;
        do {
            System.out.println("Introduzca el número de teléfono: ");
            try {
                numTelefono = Integer.parseInt(System.console().readLine());
                final int numTelefonoFinal = numTelefono;
                if (Integer.toString(numTelefono).length() == 9) {
                    valido = true;
                } else if (!telefonos.stream()
                        .anyMatch(telefono1 -> telefono1.getNumTelefono() == numTelefonoFinal)) {
                    System.err.println("Error: El número de teléfono ya existe");
                } else {
                    System.err.println("Error: El número de teléfono debe tener 9 dígitos");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número");
            }
        } while (!valido);
        telefono.setNumTelefono(numTelefono);

        String titular = getTitular();
        telefono.setTitular(titular);
        restClient.postTelefono(telefono).thenAccept(create -> {
            if (create) {
                System.out.println("Teléfono añadido correctamente");
            } else {
                System.err.println("Error al añadir el teléfono");
            }
        });
    }

    public static void visualizarTelefonos() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = restClient.getTelefonos().join();
        System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
        System.out.println("--------------------------------------------");
        telefonos.forEach(telefono -> {
            System.out.printf("%-15s %-15s %-15s\n", telefono.getNumTelefono(), telefono.getOperador().getNombre(),
                    telefono.getTitular());
        });
    }

    public static void visualizarTelefonosPorOperador() {
        TerminalUtils.clearScreen();
        int codOperador = getOperador().getCodOperador();
        List<Telefono> telefonos = new ArrayList<>();

        restClient.getTelefonosPorOperador(codOperador).thenAccept(telefono -> {
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
    }

    private static Operador getOperador() {
        int codOperador = 0;
        List<Operador> operadores = new ArrayList<>();
        restClient.getOperadores().thenAccept(operador -> {
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
        return operadores.get(operadores.indexOf(new Operador(codOperador, "")));
    }

    private static String getTitular() {
        String titular = "";
        Set<String> titulares = new HashSet<>();
        restClient.getTitulares().thenAccept(titular1 -> {
            if (titular1 == null || titular1.isEmpty()) {
                System.out.println("No hay registros para mostrar");
            } else {
                titulares.addAll(titular1);
            }
        }).join(); // Añadido .join() para asegurar que se completa la operación

        if (!titulares.isEmpty()) {
            boolean valido = false;
            do {
                TerminalUtils.clearScreen();
                System.out.printf("%-15s\n", "Titulares");
                System.out.println("--------------------------------------------");
                titulares.forEach(System.out::println);

                System.out.println("Introduzca el nombre del titular: ");

                titular = System.console().readLine();
                if (titulares.contains(titular)) {
                    valido = true;
                } else {
                    System.err.println("Error: Titular no válido");
                    System.out.println("Presione Enter para continuar...");
                    System.console().readLine();
                }
            } while (!valido);
            TerminalUtils.clearScreen();
            return titular;
        } else {
            System.err.println("No hay registros para mostrar");
            return titular;
        }
    }

    public static void visualizarTelefonosPorTitular() {
        TerminalUtils.clearScreen();
        String titular = getTitular();
        List<Telefono> telefonos = new ArrayList<>();

        if (titular.trim().contains(" ")) {
            try {
                titular = URLEncoder.encode(titular, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println("Error al codificar el titular");
                return;
            }
        }
        restClient.getTelefonosTitular(titular).thenAccept(telefono -> {
            if (telefono == null || telefono.isEmpty()) {
                System.out.println("No hay registros para mostrar");
            } else
                telefonos.addAll(telefono);
        }).join();
        if (!telefonos.isEmpty()) {
            System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
            System.out.println("--------------------------------------------");
            telefonos.forEach(telefono -> {
                System.out.printf("%-15s %-15s %-15s\n", telefono.getNumTelefono(),
                        telefono.getOperador().getNombre(),
                        telefono.getTitular());
            });
        }
    }

    public static void visualizarHistorialNumero() {
        TerminalUtils.clearScreen();
        int numTelefono = 0;

        List<Telefono> telefonos = new ArrayList<>();
        restClient.getTelefonos().thenAccept(telefono -> {
            if (telefono == null || telefono.isEmpty()) {
                System.out.println("No hay registros para mostrar");
            } else
                telefonos.addAll(telefono);
        }).join(); // Añadido .join() para asegurar que se completa la operación

        if (!telefonos.isEmpty()) {
            System.out.printf("%-15s\n", "Números");
            System.out.println("----------------------");
            telefonos.forEach(telefono -> {
                System.out.printf("%-15s\n", telefono.getNumTelefono());
            });
        }
        boolean valido = false;
        do {
            System.out.println("Seleccione un número de teléfono:(Introduzca número) ");
            try {
                numTelefono = Integer.parseInt(System.console().readLine());
                final int numTelefonoFinal = numTelefono;
                if (telefonos.stream().anyMatch(telefono -> telefono.getNumTelefono() == numTelefonoFinal)) {
                    valido = true;
                } else {
                    System.err.println("Error: Número de teléfono no válido");
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número");
            }
        } while (!valido);
        TerminalUtils.clearScreen();
        List<Historial> historial = new ArrayList<>();
        restClient.getHistorialTelefono(numTelefono).thenAccept(hist -> {
            if (hist == null || hist.isEmpty()) {
                System.out.println("No hay registros para mostrar");
            } else
                historial.addAll(hist);
        }).join();
        if (!historial.isEmpty()) {
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
}