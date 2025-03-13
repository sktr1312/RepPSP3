package clienteHttp.commands;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import clienteHttp.TelefonoDeserializer;
import clienteHttp.models.Operador;
import clienteHttp.models.Telefono;

public class SendRequest {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URI_BASE = "http://localhost/rest/index.php";
    private final Gson gson;

    public SendRequest() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Telefono.class, new TelefonoDeserializer())
                .create();
    }

    // Método genérico para construir peticiones HTTP
    private HttpRequest buildRequest(String endpoint, String method, Object body) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + endpoint));

        switch (method) {
            case "GET":
                requestBuilder.GET();
                break;
            case "POST":
                requestBuilder.header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
                break;
            case "PUT":
                requestBuilder.header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
                break;
            case "DELETE":
                requestBuilder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Método HTTP no soportado: " + method);
        }

        return requestBuilder.build();
    }

    // Método genérico para enviar peticiones asíncronas
    private <T> CompletableFuture<T> sendAsyncRequest(HttpRequest request,
            JsonResponseHandler<T> responseHandler,
            boolean showLoadingAnimation) {
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(
                request, HttpResponse.BodyHandlers.ofString());

        if (showLoadingAnimation) {
            showLoadingAnimation(responseFuture);
        }

        return responseFuture.thenApply(response -> {
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return responseHandler.handleResponse(response);
            } else {
                System.out.println("Error en la petición. Código: " + response.statusCode());
                return null;
            }
        }).exceptionally(ex -> {
            System.err.println("Error al enviar la petición: " + ex.getMessage());
            return null;
        });
    }

    // Interfaz funcional para manejar las respuestas JSON
    @FunctionalInterface
    private interface JsonResponseHandler<T> {
        T handleResponse(HttpResponse<String> response);
    }

    // Extrae los datos JSON comunes
    private JsonArray extractJsonData(HttpResponse<String> response) {
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonObject.getAsJsonArray("data");
    }

    private void showLoadingAnimation(CompletableFuture<?> future) {
        new Thread(() -> {
            int dotsCount = 0;
            int maxDots = 3;

            System.out.print("Esperando respuesta del servidor");

            while (!future.isDone()) {
                // Borra los puntos anteriores
                for (int i = 0; i < maxDots; i++) {
                    System.out.print("\b \b");
                }

                // Muestra los nuevos puntos
                dotsCount = (dotsCount + 1) % (maxDots + 1);
                for (int i = 0; i < dotsCount; i++) {
                    System.out.print(".");
                }

                try {
                    Thread.sleep(300); // Pausa para que sea visible la animación
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Limpiar la línea completa
            System.out.print("\r" + " ".repeat(40) + "\r");
            System.out.println("Datos recibidos correctamente");
        }).start();
    }

    // Métodos para telefonos - todos asíncronos
    public CompletableFuture<List<Telefono>> getTelefonos() {
        HttpRequest request = buildRequest("/telefonos", "GET", null);
        return sendAsyncRequest(request,
                response -> gson.fromJson(extractJsonData(response),
                        new TypeToken<List<Telefono>>() {
                        }.getType()),
                true);
    }

    public CompletableFuture<Telefono> getTelefono(int numTelefono) {
        HttpRequest request = buildRequest("/telefonos/" + numTelefono, "GET", null);
        return sendAsyncRequest(request,
                response -> {
                    List<Telefono> telefonos = gson.fromJson(extractJsonData(response),
                            new TypeToken<List<Telefono>>() {
                            }.getType());
                    return telefonos.isEmpty() ? null : telefonos.get(0);
                },
                false);
    }

    public CompletableFuture<List<Telefono>> getTelefonosTitular(String titular) {
        HttpRequest request = buildRequest("/telefonos/titular/" + titular, "GET", null);
        return sendAsyncRequest(request,
                response -> gson.fromJson(extractJsonData(response),
                        new TypeToken<List<Telefono>>() {
                        }.getType()),
                false);
    }

    public CompletableFuture<List<Telefono>> getTelefonosPorOperador(int codOperador) {
        HttpRequest request = buildRequest("/telefonos/operador/" + codOperador, "GET", null);
        return sendAsyncRequest(request,
                response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        System.out.println("Datos recibidos correctamente");
                        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                        JsonArray jsonData = jsonObject.getAsJsonArray("data");
                        return gson.fromJson(jsonData, new TypeToken<List<Telefono>>() {
                        }.getType());
                    } else {
                        System.out.println("Error al enviar la petición GET. Código: " + response.statusCode());
                        return null;

                    }
                },
                false);

    }

    public CompletableFuture<Boolean> postTelefono(Telefono telefono) {
        HttpRequest request = buildRequest("/telefonos", "POST", telefono);
        return sendAsyncRequest(request,
                response -> {
                    System.out.println(response.body());
                    return true;
                },
                false);
    }

    public CompletableFuture<Boolean> putTelefono(Telefono telefono) {
        HttpRequest request = buildRequest("/telefonos/" + telefono.getNumTelefono(), "PUT", telefono);
        return sendAsyncRequest(request,
                response -> {
                    System.out.println(response.body());
                    return true;
                },
                false);
    }

    public CompletableFuture<Boolean> deleteTelefono(int numTelefono) {
        HttpRequest request = buildRequest("/telefonos/" + numTelefono, "DELETE", null);
        return sendAsyncRequest(request,
                response -> {
                    System.out.println(response.body());
                    return true;
                },
                false);
    }

    // Métodos para operadores - todos asíncronos
    public CompletableFuture<List<Operador>> getOperadores() {
        HttpRequest request = buildRequest("/operadores", "GET", null);
        return sendAsyncRequest(request,
                response -> gson.fromJson(extractJsonData(response),
                        new TypeToken<List<Operador>>() {
                        }.getType()),
                false);
    }

    public CompletableFuture<Operador> getOperador(int codOperador) {
        HttpRequest request = buildRequest("/operadores/" + codOperador, "GET", null);
        return sendAsyncRequest(request,
                response -> {
                    List<Operador> operadores = gson.fromJson(extractJsonData(response),
                            new TypeToken<List<Operador>>() {
                            }.getType());
                    return operadores.isEmpty() ? null : operadores.get(0);
                },
                false);
    }

    public static void main(String[] args) {
        SendRequest sr = new SendRequest();

        // Ejemplo de GET - Obtener todos los teléfonos
        sr.getTelefonos()
                .thenAccept(telefonos -> {
                    System.out.println("\n=== EJEMPLO GET (todos los telefonos) ===");
                    if (telefonos != null && !telefonos.isEmpty()) {
                        System.out.println("Total de teléfonos recuperados: " + telefonos.size());
                        System.out.println("Primer teléfono: " + telefonos.get(0).getNumTelefono());
                    }
                });

        // Ejemplo de GET - Obtener un teléfono específico
        int numTelefonoEjemplo = 123456789; // Reemplazar con un número real
        sr.getTelefono(numTelefonoEjemplo)
                .thenAccept(telefono -> {
                    System.out.println("\n=== EJEMPLO GET (teléfono específico) ===");
                    if (telefono != null) {
                        System.out.println("Teléfono encontrado: " + telefono.getNumTelefono());
                        System.out.println("Titular: " + telefono.getTitular());
                    } else {
                        System.out.println("No se encontró el teléfono: " + numTelefonoEjemplo);
                    }
                });

        // Ejemplo de GET - Buscar teléfonos por titular
        String titularEjemplo = "sktr"; // Reemplazar con un nombre real
        sr.getTelefonosTitular(titularEjemplo)
                .thenAccept(telefonos -> {
                    System.out.println("\n=== EJEMPLO GET (teléfonos por titular) ===");
                    if (telefonos != null && !telefonos.isEmpty()) {
                        System.out.println("Teléfonos de " + titularEjemplo + ": " + telefonos.size());
                        telefonos.forEach(t -> System.out.println(" - " + t.getNumTelefono()));
                    } else {
                        System.out.println("No se encontraron teléfonos para: " + titularEjemplo);
                    }
                });

        // Ejemplo de POST - Crear un nuevo teléfono
        Telefono nuevoTelefono = new Telefono();
        nuevoTelefono.setNumTelefono(987654321); // Reemplazar con datos reales
        nuevoTelefono.setTitular("Ana García");
        nuevoTelefono.setOperador(new Operador(1, "Movistar"));

        sr.postTelefono(nuevoTelefono)
                .thenAccept(resultado -> {
                    System.out.println("\n=== EJEMPLO POST (crear teléfono) ===");
                    if (Boolean.TRUE.equals(resultado)) {
                        System.out.println("Teléfono creado correctamente: " + nuevoTelefono.getNumTelefono());
                    } else {
                        System.out.println("Error al crear el teléfono");
                    }
                });

        // Ejemplo de PUT - Actualizar un teléfono existente
        sr.getTelefono(987654321) // Obtener primero el teléfono a modificar
                .thenCompose(telefono -> {
                    System.out.println("\n=== EJEMPLO PUT (actualizar teléfono) ===");
                    if (telefono != null) {
                        // Modificar los datos
                        telefono.setTitular("Ana García Martínez");
                        // Enviar la actualización
                        return sr.putTelefono(telefono);
                    } else {
                        System.out.println("No se encontró el teléfono para actualizar");
                        return CompletableFuture.completedFuture(false);
                    }
                })
                .thenAccept(resultado -> {
                    if (Boolean.TRUE.equals(resultado)) {
                        System.out.println("Teléfono actualizado correctamente");
                    } else {
                        System.out.println("Error al actualizar el teléfono");
                    }
                });

        // Ejemplo de DELETE - Eliminar un teléfono
        int telefonoAEliminar = 987654321; // Reemplazar con un número real
        sr.deleteTelefono(telefonoAEliminar)
                .thenAccept(resultado -> {
                    System.out.println("\n=== EJEMPLO DELETE (eliminar teléfono) ===");
                    if (Boolean.TRUE.equals(resultado)) {
                        System.out.println("Teléfono eliminado correctamente: " + telefonoAEliminar);
                    } else {
                        System.out.println("Error al eliminar el teléfono");
                    }
                });

        // Ejemplo combinado - Obtener operadores y luego buscar detalles del primer
        // operador
        sr.getOperadores()
                .thenCompose(operadores -> {
                    System.out.println("\n=== EJEMPLO COMBINADO (operadores) ===");
                    if (operadores != null && !operadores.isEmpty()) {
                        System.out.println("Total de operadores: " + operadores.size());
                        int primerOperadorId = operadores.get(0).getCodOperador();
                        System.out.println("Buscando detalles del operador: " + primerOperadorId);
                        return sr.getOperador(primerOperadorId);
                    } else {
                        System.out.println("No se encontraron operadores");
                        return CompletableFuture.completedFuture(null);
                    }
                })
                .thenAccept(operador -> {
                    if (operador != null) {
                        System.out.println("Detalles del operador:");
                        System.out.println(" - Código: " + operador.getCodOperador());
                        System.out.println(" - Nombre: " + operador.getNombre());
                    }
                });

        // Esperar a que todas las operaciones asíncronas terminen (solo para
        // demostración)
        try {
            System.out.println("\nEsperando a que se completen todas las operaciones...");
            Thread.sleep(5000);
            System.out.println("Fin del programa");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}