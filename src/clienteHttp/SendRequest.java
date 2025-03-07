package clienteHttp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

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
        List<Telefono> telefonos = sr.sendGetTelefonosTitular("sktrd");
        Telefono telefono = telefonos.get(0);
        telefono.setTitular("sktr");
        sr.sendPutTelefonoRequest(telefono);
    }

}
