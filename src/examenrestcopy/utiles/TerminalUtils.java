package examenrestcopy.utiles;

import java.io.IOException;

public class TerminalUtils {
    /**
     * Limpia la pantalla de la terminal
     */
    public static void clearScreen() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                // En Windows usamos el comando 'cls'
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // En Unix/Linux/Mac usamos códigos ANSI o el comando 'clear'
                try {
                    // Primero intentamos con códigos ANSI (funciona en la mayoría de terminales
                    // modernos)
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                } catch (Exception e) {
                    // Si falla, intentamos con el comando 'clear'
                    new ProcessBuilder("clear").inheritIO().start().waitFor();
                }
            }
        } catch (IOException | InterruptedException e) {
            // Si todos los métodos fallan, imprimimos varias líneas en blanco como
            // alternativa
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
