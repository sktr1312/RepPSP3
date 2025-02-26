package sockets.ejercicioStock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTES = 3;

    // Mapa para almacenar el stock de materiales
    private Map<String, Integer> stock = new ConcurrentHashMap<>();

    // Mapa para almacenar las transacciones por cliente
    private Map<String, List<String>> transactions = new ConcurrentHashMap<>();

    // Semáforo para controlar el número máximo de clientes
    private Semaphore semaphore = new Semaphore(MAX_CLIENTES);

    public static void main(String[] args) {
        new StockServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en puerto " + PORT);

            while (true) {
                if (semaphore.tryAcquire()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                } else {
                    // Si no hay permisos disponibles, esperar brevemente antes de intentar de nuevo
                    Thread.sleep(100);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String clientId;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                System.out.println("Cliente conectado: " + clientId);

                // Inicializar la lista de transacciones para este cliente
                transactions.putIfAbsent(clientId, new ArrayList<>());

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String response = processCommand(inputLine);
                    out.println(response);

                    if (inputLine.trim().equalsIgnoreCase("SALIR")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error con cliente " + clientId + ": " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar socket: " + e.getMessage());
                }
                semaphore.release();
                System.out.println("Cliente desconectado: " + clientId);
            }
        }

        private String processCommand(String command) {
            if (command.trim().equalsIgnoreCase("INFO")) {
                return generateInfoResponse();
            } else if (command.trim().equalsIgnoreCase("SALIR")) {
                return generateClientTransactions();
            }

            // Usar regex para analizar comandos PUT y GET
            String regex = "([a-zñ]+)\\s+([a-zñ]+)\\s+(\\d+)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(command);

            if (matcher.matches()) {
                String cmd = matcher.group(1).toUpperCase();
                String material = matcher.group(2).toUpperCase();
                int cantidad = Integer.parseInt(matcher.group(3));

                switch (cmd) {
                    case "PUT":
                        return handlePut(material, cantidad);
                    case "GET":
                        return handleGet(material, cantidad);
                    default:
                        return "Comando no reconocido. Use PUT, GET, INFO o SALIR.";
                }
            } else {
                return "Formato de comando incorrecto. Use: [PUT|GET] material cantidad";
            }
        }

        private String handlePut(String material, int cantidad) {
            // Actualizar el stock
            stock.put(material, stock.getOrDefault(material, 0) + cantidad);

            // Registrar la transacción
            String transaction = "PUT " + material + " " + cantidad;
            transactions.get(clientId).add(transaction);

            return "Material: " + material + ", Stock actual: " + stock.get(material);
        }

        private String handleGet(String material, int cantidad) {
            // Verificar si el material existe
            if (!stock.containsKey(material)) {
                return "ERROR: El material " + material + " no existe en stock.";
            }

            // Verificar si hay suficiente stock
            int currentStock = stock.get(material);
            if (currentStock < cantidad) {
                return "ERROR: Stock insuficiente. Disponible: " + currentStock + " unidades.";
            }

            // Actualizar el stock
            stock.put(material, currentStock - cantidad);

            // Registrar la transacción
            String transaction = "GET " + material + " " + cantidad;
            transactions.get(clientId).add(transaction);

            return "Material: " + material + ", Stock actual: " + stock.get(material);
        }

        private String generateInfoResponse() {
            StringBuilder sb = new StringBuilder("--- Transacciones por cliente ---\n");

            for (Map.Entry<String, List<String>> entry : transactions.entrySet()) {
                sb.append("Cliente: ").append(entry.getKey()).append("\n");
                List<String> clientTransactions = entry.getValue();
                if (clientTransactions.isEmpty()) {
                    sb.append("  Sin transacciones\n");
                } else {
                    for (String transaction : clientTransactions) {
                        sb.append("  ").append(transaction).append("\n");
                    }
                }
                sb.append("\n");
            }

            return sb.toString();
        }

        private String generateClientTransactions() {
            StringBuilder sb = new StringBuilder("--- Sus transacciones ---\n");

            List<String> clientTransactions = transactions.get(clientId);
            if (clientTransactions.isEmpty()) {
                sb.append("Sin transacciones\n");
            } else {
                for (String transaction : clientTransactions) {
                    sb.append(transaction).append("\n");
                }
            }

            return sb.toString();
        }
    }
}
