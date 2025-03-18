package examenrestcopy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import examenrestcopy.models.Historial;
import examenrestcopy.menu.Menu;
import examenrestcopy.menu.MenuItem;
import examenrestcopy.menu.SubMenu;
import examenrestcopy.models.Operador;
import examenrestcopy.models.Telefono;
import examenrestcopy.restclient.SendRequest;
import examenrestcopy.utiles.TerminalUtils;

public class App {
    static SendRequest sendRequest = new SendRequest();

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
        // añadir opciones a visualizar registros
        verRegistros.getMenu()
                .addComponent(new MenuItem("Ver telefonos de un operador", App::visualizarTelefonosPorOperador));
        verRegistros.getMenu()
                .addComponent(new MenuItem("Ver telefonos de un titular", App::visualizarTelefonosPorTitular));
        verRegistros.getMenu()
                .addComponent(new MenuItem("Ver historial de un número", App::visualizarHistorialNumero));
        verRegistros.getMenu()
                .addComponent(new MenuItem("Ver todos los telefonos", App::visualizarTelefonos));
        // submenus actualizar registros
        SubMenu actualizarRegistros = new SubMenu(new Menu("Actualizar Registros"));
        // añadir opciones a actualizar registros
        actualizarRegistros.getMenu()
                .addComponent(new MenuItem("Actualizar operador", App::actualizarOperador));
        // submenus eliminar registros
        SubMenu eliminarRegistros = new SubMenu(new Menu("Eliminar Registros"));
        // submenu insertar registros
        SubMenu insertarRegistros = new SubMenu(new Menu("Insertar Registros"));
        // añadir opciones a insertar registros
        insertarRegistros.getMenu()
                .addComponent(new MenuItem("Añadir telefono", App::anhadirTelefono));
        // añadir submenus al menu principal
        menu.addComponent(verRegistros);
        menu.addComponent(actualizarRegistros);
        menu.addComponent(eliminarRegistros);
        menu.addComponent(insertarRegistros);

    }

    public static void actualizarOperador() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = sendRequest.getTelefonos().resultNow();
        Telefono telefono = getTelefono(telefonos);
        TerminalUtils.clearScreen();
        Operador operador = getOperador();
        Operador operadorAntiguo = telefono.getOperador();
        telefono.setOperador(operador);
        if (sendRequest.putTelefono(telefono).resultNow()) {
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
                    sendRequest.postHistorial(historial).thenAccept(create -> {
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
        List<Telefono> telefonos = sendRequest.getTelefonos().resultNow();
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
        sendRequest.postTelefono(telefono).thenAccept(create -> {
            if (create) {
                System.out.println("Teléfono añadido correctamente");
            } else {
                System.err.println("Error al añadir el teléfono");
            }
        });

    }

    public static void visualizarTelefonos() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = sendRequest.getTelefonos().resultNow();
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

        sendRequest.getTelefonosPorOperador(codOperador).thenAccept(telefono -> {
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
        // telefonos.forEach(System.out::println);

    }

    private static Operador getOperador() {
        int codOperador = 0;
        List<Operador> operadores = new ArrayList<>();
        sendRequest.getOperadores().thenAccept(operador -> {
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
        sendRequest.getTitulares().thenAccept(titular1 -> {
            if (titular1 == null || titular1.isEmpty()) {
                System.out.println("No hay registros para mostrar");

            } else {
                titulares.addAll(titular1);
            }
        });
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
        sendRequest.getTelefonosTitular(titular).thenAccept(telefono -> {
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
        sendRequest.getTelefonos().thenAccept(telefono -> {
            if (telefono == null || telefono.isEmpty()) {
                System.out.println("No hay registros para mostrar");

            } else
                telefonos.addAll(telefono);
        });
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
        sendRequest.getHistorialTelefono(numTelefono).thenAccept(hist -> {
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
