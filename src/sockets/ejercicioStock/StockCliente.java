package sockets.ejercicioStock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class StockCliente {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)) {
            System.out.println("Conectado al servidor. Comandos disponibles:");
            System.out.println("- PUT material cantidad: para añadir stock");
            System.out.println("- GET material cantidad: para retirar stock");
            System.out.println("- INFO: para ver todas las transacciones");
            System.out.println("- SALIR: para desconectar");

            String userInput;
            while (true) {
                System.out.print("\n> ");
                userInput = scanner.nextLine();

                // Enviar comando al servidor
                out.println(userInput);

                // Leer y mostrar la respuesta del servidor
                String response = in.readLine();

                // Para respuestas multilínea (INFO y SALIR)
                if (userInput.trim().equalsIgnoreCase("INFO") ||
                        userInput.trim().equalsIgnoreCase("SALIR")) {
                    System.out.println(formatMultilineResponse(response));

                    if (userInput.trim().equalsIgnoreCase("SALIR")) {
                        break;
                    }
                } else {
                    System.out.println(response);
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Host desconocido: " + SERVER_HOST);
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    // Método para formatear respuestas multilínea
    private static String formatMultilineResponse(String response) {
        // Reemplazamos el carácter \n enviado como texto por saltos de línea reales
        return response.replace("\\n", System.lineSeparator());
    }
}
