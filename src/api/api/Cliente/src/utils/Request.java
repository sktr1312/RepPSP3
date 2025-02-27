package api.api.Cliente.src.utils;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import api.api.Cliente.src.modelo.Cliente;


public class Request {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static List<Cliente> sendGetRequest(String requestUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Content-Type", "application/json; utf-8")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();
        HttpResponseHandler.handleStatusCode(statusCode, responseBody);

        if (statusCode == 200) {
            Type listType = new TypeToken<List<Cliente>>() {}.getType();
            return gson.fromJson(responseBody, listType);
        }
        return List.of(); // Lista vacía en caso de respuesta exitosa sin contenido
    }

    public static String[] sendPostRequest(String requestUrl, String jsonInputString) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Content-Type", "application/json; utf-8")
                .header("Accept", "application/json")
                .POST(BodyPublishers.ofString(jsonInputString))
                .build();
    
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        HttpResponseHandler.handleStatusCode(statusCode, responseBody);
        return new String[]{
            String.valueOf(statusCode),
            responseBody
        };
    }   

    public static String sendPutRequest(String requestUrl, String jsonInputString) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Content-Type", "application/json; utf-8")
                .header("Accept", "application/json")
                .PUT(BodyPublishers.ofString(jsonInputString))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();
        HttpResponseHandler.handleStatusCode(statusCode, responseBody);
        return responseBody;
    }

    public static String sendDeleteRequest(String requestUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json; utf-8")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();
        String responseBody = response.body();

        HttpResponseHandler.handleStatusCode(statusCode, responseBody);
        return responseBody;
    }
}