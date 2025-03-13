package clienteHttp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import clienteHttp.models.Operador;
import clienteHttp.models.Telefono;

public class SendRequest {
    private static final HttpClient client = HttpClient.newHttpClient();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Telefono.class, new TelefonoDeserializer())
            .create();
    private static final String URI_BASE = "http://localhost/rest/index.php";

    public List<Telefono> sendGetTelefonosRequest() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/telefonos"))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        int dotsCount = 0;
        int maxDots = 3; // Máximo número de puntos a mostrar

        System.out.print("Esperando respuesta del servidor");

        while (!responseFuture.isDone()) {
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
                Thread.sleep(1); // Pausa para que sea visible la animación
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Limpiar la línea completa
        System.out.print("\r" + " ".repeat(40) + "\r");

        HttpResponse<String> response = responseFuture.join();

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
    }

    public Telefono sendGetTelefonoRequest(int numTelefono) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/telefonos/" + numTelefono))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

            // Extraer el array "data"
            JsonArray jsonData = jsonObject.getAsJsonArray("data");
            List<Telefono> telefono = gson.fromJson(jsonData, new TypeToken<List<Telefono>>() {
            }.getType());
            return telefono.get(0);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar la petición GET");
            return null;
        }
    }

    public boolean sendPostTelefonoRequest(Telefono telefono) {
        boolean result = false;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/telefonos"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(telefono)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            result = response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar la petición POST");

        }
        return result;
    }

    public List<Telefono> sendGetTelefonosTitular(String titular) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/telefonos/titular/" + titular))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

            // Extraer el array "data"
            JsonArray jsonData = jsonObject.getAsJsonArray("data");
            return gson.fromJson(jsonData, new TypeToken<List<Telefono>>() {
            }.getType());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar la petición GET");
            return null;
        }
    }

    

    public boolean sendPutTelefonoRequest(Telefono telefono) {
        boolean result = false;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/telefonos/" + telefono.getNumTelefono()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(telefono)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            result = response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar la petición PUT");

        }
        return result;
    }

    public boolean sendDeleteTelefonoRequest(int numTelefono) {
        boolean result = false;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/telefonos/" + numTelefono))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            result = response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar la petición DELETE");

        }
        return result;
    }

    public List<Operador> sendGetOperadoresRequest() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/operadores"))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

            // Extraer el array "data"
            JsonArray jsonData = jsonObject.getAsJsonArray("data");
            return gson.fromJson(jsonData, new TypeToken<List<Operador>>() {
            }.getType());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar la petición GET");
            return null;
        }
    }

    public Operador sendGetOperadorRequest(int codOperador) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URI_BASE + "/operadores/" + codOperador))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

            // Extraer el array "data"
            JsonArray jsonData = jsonObject.getAsJsonArray("data");
            List<Operador> operador = gson.fromJson(jsonData, new TypeToken<List<Operador>>() {
            }.getType());
            return operador.get(0);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al enviar la petición GET");
            return null;
        }
    }

    public static void main(String[] args) {
        SendRequest sr = new SendRequest();
        // List<Telefono> telefonos = sr.sendGetTelefonosRequest();
        // Telefono telefono = telefonos.get(0);
        

    }

}
