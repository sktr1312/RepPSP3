package clienteHttp;
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

import modelo.Cliente;

public class Request {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static List<Cliente> sendGetRequest(String requestUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Content-Type", "application/json; utf-8")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        Type listType = new TypeToken<List<Cliente>>() {}.getType();
        return gson.fromJson(response.body(), listType);
    }

    public static String sendPostRequest(String requestUrl, String jsonInputString) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Content-Type", "Application/json; utf-8")
                .POST(BodyPublishers.ofString(jsonInputString))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    public static String sendPutRequest(String requestUrl, String jsonInputString) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Content-Type", "Clientelication/json; utf-8")
                .header("Accept", "Application/json")
                .PUT(BodyPublishers.ofString(jsonInputString))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    public static String sendDeleteRequest(String requestUrl) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(requestUrl))
                .header("Accept", "Application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }
}