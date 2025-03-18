package examenrest;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import examenrest.models.Historial;
import examenrest.menu.Menu;
import examenrest.menu.MenuItem;
import examenrest.menu.SubMenu;
import examenrest.models.Operador;
import examenrest.models.Telefono;
import examenrest.restclient.SendRequest;
import examenrest.utiles.TerminalUtils;

public class App2 {
    static SendRequest sendRequest = new SendRequest();

    public static void main(String[] args) {
        Menu menu = new Menu("Menú principal");
        addSubmenus(menu);
        menu.execute();
    }

    private static void addSubmenus(Menu menu) {
        menu.addComponent(createSubMenu("Visualizar Registros",
                new MenuItem("Ver teléfonos de un operador", App2::visualizarTelefonosPorOperador),
                new MenuItem("Ver teléfonos de un titular", App2::visualizarTelefonosPorTitular),
                new MenuItem("Ver historial de un número", App2::visualizarHistorialNumero),
                new MenuItem("Ver todos los teléfonos", App2::visualizarTelefonos)));

        menu.addComponent(createSubMenu("Actualizar Registros",
                new MenuItem("Actualizar operador", App2::actualizarOperador)));

        menu.addComponent(createSubMenu("Insertar Registros",
                new MenuItem("Añadir teléfono", App2::anhadirTelefono)));

        menu.addComponent(new SubMenu(new Menu("Eliminar Registros")));
    }

    private static SubMenu createSubMenu(String title, MenuItem... items) {
        SubMenu subMenu = new SubMenu(new Menu(title));
        for (MenuItem item : items) {
            subMenu.getMenu().addComponent(item);
        }
        return subMenu;
    }

    public static void actualizarOperador() {
        TerminalUtils.clearScreen();
        List<Telefono> telefonos = sendRequest.getTelefonos().resultNow();
        Telefono telefono = getTelefono(telefonos);

        TerminalUtils.clearScreen();
        Operador nuevoOperador = getOperador();
        Operador operadorAntiguo = telefono.getOperador();
        telefono.setOperador(nuevoOperador);

        if (sendRequest.putTelefono(telefono).resultNow()) {
            System.out.println("Teléfono actualizado correctamente");
            solicitarInput("Presione Enter para continuar...");
            String motivo = solicitarMotivo();

            if (!motivo.trim().isEmpty()) {
                Historial historial = new Historial(0, telefono, operadorAntiguo, operadorAntiguo, motivo);
                sendRequest.postHistorial(historial).thenAccept(success -> {
                    if (success)
                        System.out.println("Historial añadido correctamente");
                    else
                        System.err.println("Error al añadir el historial");
                });
            }
        } else {
            System.err.println("Error al actualizar el teléfono");
        }
    }

    private static Telefono getTelefono(List<Telefono> telefonos) {
        mostrarTelefonos(telefonos);

        while (true) {
            try {
                int numTelefono = Integer.parseInt(solicitarInput("Introduzca el número de teléfono: "));
                return telefonos.stream()
                        .filter(t -> t.getNumTelefono() == numTelefono)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Número de teléfono no válido"));
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número válido.");
            }
        }
    }

    public static void anhadirTelefono() {
        TerminalUtils.clearScreen();
        Telefono telefono = new Telefono();
        telefono.setOperador(getOperador());

        int numTelefono = solicitarNumeroTelefono(sendRequest.getTelefonos().resultNow());
        telefono.setNumTelefono(numTelefono);

        telefono.setTitular(getTitular());

        sendRequest.postTelefono(telefono).thenAccept(success -> {
            if (success)
                System.out.println("Teléfono añadido correctamente");
            else
                System.err.println("Error al añadir el teléfono");
        });
    }

    public static void visualizarTelefonos() {
        TerminalUtils.clearScreen();
        mostrarTelefonos(sendRequest.getTelefonos().resultNow());
    }

    public static void visualizarTelefonosPorOperador() {
        TerminalUtils.clearScreen();
        int codOperador = getOperador().getCodOperador();

        List<Telefono> telefonos = sendRequest.getTelefonosPorOperador(codOperador).resultNow();
        if (telefonos.isEmpty())
            System.out.println("No hay registros para mostrar");
        else
            mostrarTelefonos(telefonos);
    }

    public static void visualizarTelefonosPorTitular() {
        TerminalUtils.clearScreen();
        String titular = getTitular();

        List<Telefono> telefonos = sendRequest.getTelefonosTitular(titular).resultNow();
        if (telefonos.isEmpty())
            System.out.println("No hay registros para mostrar");
        else
            mostrarTelefonos(telefonos);
    }

    public static void visualizarHistorialNumero() {
        TerminalUtils.clearScreen();
        int numTelefono = solicitarNumeroTelefono(sendRequest.getTelefonos().resultNow());

        List<Historial> historial = sendRequest.getHistorialTelefono(numTelefono).resultNow();
        if (historial.isEmpty())
            System.out.println("No hay registros para mostrar");
        else
            historial.forEach(System.out::println);
    }

    private static void mostrarTelefonos(List<Telefono> telefonos) {
        System.out.printf("%-15s %-15s %-15s\n", "Número", "Operador", "Titular");
        System.out.println("--------------------------------------------");
        telefonos.forEach(t -> System.out.printf("%-15s %-15s %-15s\n", t.getNumTelefono(), t.getOperador().getNombre(),
                t.getTitular()));
    }

    private static Operador getOperador() {
        List<Operador> operadores = sendRequest.getOperadores().resultNow();

        operadores.forEach(o -> System.out.printf("%-15s %s\n", o.getNombre(), o.getCodOperador()));

        while (true) {
            try {
                int codOperador = Integer.parseInt(solicitarInput("Seleccione un operador (código): "));
                return operadores.stream()
                        .filter(o -> o.getCodOperador() == codOperador)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Código de operador no válido"));
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número válido.");
            }
        }
    }

    private static String solicitarInput(String mensaje) {
        System.out.println(mensaje);
        return System.console().readLine();
    }

    private static String solicitarMotivo() {
        String motivo;
        do {
            motivo = solicitarInput("Introduzca el motivo del cambio de operador: ");
        } while (motivo.trim().isEmpty());
        return motivo;
    }

    private static int solicitarNumeroTelefono(List<Telefono> telefonos) {
        while (true) {
            try {
                int numTelefono = Integer.parseInt(solicitarInput("Introduzca el número de teléfono: "));
                if (Integer.toString(numTelefono).length() == 9
                        && telefonos.stream().noneMatch(t -> t.getNumTelefono() == numTelefono)) {
                    return numTelefono;
                }
                System.err.println("Error: Número de teléfono no válido o ya existente");
            } catch (NumberFormatException e) {
                System.err.println("Error: Debe introducir un número válido");
            }
        }
    }

    private static String getTitular() {
        Set<String> titulares = new HashSet<>();
        sendRequest.getTitulares().thenAccept(titulares::addAll).join();

        while (true) {
            System.out.println("Titulares disponibles:");
            titulares.forEach(System.out::println);

            System.out.println("Introduzca el nombre del titular: ");
            String titular = System.console().readLine();
            if (titulares.contains(titular))
                return titular;

            System.err.println("Error: Titular no válido");
        }
    }
}
