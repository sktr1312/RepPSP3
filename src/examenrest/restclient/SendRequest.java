package examenrest.restclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import examenrest.models.Historial;
import examenrest.models.Operador;
import examenrest.models.Telefono;

public class SendRequest {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URI_BASE = "http://localhost/rest/index.php";
    private final Gson gson;

    public SendRequest() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Telefono.class, new TelefonoDeserializer())
                .registerTypeAdapter(Historial.class, new HistorialDeserializer())
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

    public CompletableFuture<Set<String>> getTitulares() {
        HttpRequest request = buildRequest("/telefonos/titulares", "GET", null);
        return sendAsyncRequest(request, response -> {
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonArray jsonResponse = extractJsonData(response);
                // Transformar la lista de objetos en un Set<String> con los valores de
                // "titular"
                Set<String> titulares = new HashSet<>();
                for (JsonElement element : jsonResponse) {
                    JsonObject obj = element.getAsJsonObject();
                    titulares.add(obj.get("titular").getAsString());
                }

                return titulares;
            } else {
                System.out.println("Error al enviar la petición GET. Código: " + response.statusCode());
                return null;
            }
        }, true);
    }

    public CompletableFuture<Boolean> postTelefono(Telefono telefono) {
        HttpRequest request = buildRequest("/telefonos", "POST", telefono);
        return sendAsyncRequest(request,
                response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        System.out.println(response.body());
                        return true;
                    } else {
                        System.out.println("Error al enviar la petición POST. Código: " + response.statusCode());
                        return false;

                    }
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
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        System.out.println(response.body());
                        return true;
                    } else {
                        System.out.println("Error al enviar la petición DELETE. Código: " + response.statusCode());
                        return false;

                    }
                },
                true);
    }

    // Métodos para operadores - todos asíncronos
    public CompletableFuture<List<Operador>> getOperadores() {
        HttpRequest request = buildRequest("/operadores", "GET", null);
        return sendAsyncRequest(request,
                response -> gson.fromJson(extractJsonData(response),
                        new TypeToken<List<Operador>>() {
                        }.getType()),
                true);
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

    public CompletableFuture<Boolean> postOperador(Operador operador) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", operador.getNombre());
        HttpRequest request = buildRequest("/operadores", "POST",
                body);
        return sendAsyncRequest(request,
                response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        System.out.println(response.body());
                        return true;
                    } else {
                        System.out.println("Error al enviar la petición POST. Código: " + response.statusCode());
                        return false;

                    }
                },
                true);
    }

    public static void main(String[] args) {
        SendRequest sendRequest = new SendRequest();
        // Set<String> titulares = sendRequest.getTitulares().join();
        // if (titulares != null && !titulares.isEmpty()) {
        // titulares.forEach(System.out::println);
        // } else {
        // System.out.println("No hay titulares");
        // }
        // if (sendRequest.postOperador(new Operador("Telefunken")).join()) {
        // System.out.println("Operador insertado correctamente");
        // } else {
        // System.out.println("Error al insertar el operador");

        // }
        if (sendRequest.deleteTelefono(611223344).resultNow()) {
            System.out.println("Teléfono eliminado correctamente");
        } else {
            System.out.println("Error al eliminar el teléfono");

        }
    }

    // metodos para historiales
    public CompletableFuture<List<Historial>> getHistoriales() {
        HttpRequest request = buildRequest("/historiales", "GET", null);
        return sendAsyncRequest(request,
                response -> gson.fromJson(extractJsonData(response),
                        new TypeToken<List<Historial>>() {
                        }.getType()),
                false);
    }

    public CompletableFuture<List<Historial>> getHistorialTelefono(int numTelefono) {
        HttpRequest request = buildRequest("/historiales/telefono" + numTelefono, "GET", null);
        return sendAsyncRequest(request,
                response -> {
                    List<Historial> historiales = gson.fromJson(extractJsonData(response),
                            new TypeToken<List<Historial>>() {
                            }.getType());
                    return historiales.isEmpty() ? null : historiales;
                },
                false);
    }

    public CompletableFuture<Boolean> postHistorial(Historial historial) {
        HttpRequest request = buildRequest("/historiales", "POST", historial);
        return sendAsyncRequest(request,
                response -> {
                    System.out.println(response.body());
                    return true;
                },
                false);
    }

    public CompletableFuture<Boolean> putHistorial(Historial historial) {
        HttpRequest request = buildRequest("/historiales/" + historial.getTelefono().getNumTelefono(), "PUT",
                historial);
        return sendAsyncRequest(request,
                response -> {
                    System.out.println(response.body());
                    return true;
                },
                false);
    }

}