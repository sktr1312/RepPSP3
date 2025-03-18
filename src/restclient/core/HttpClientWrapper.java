package restclient.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.google.gson.Gson;


// Clase que envuelve HttpClient para manejar las peticiones HTTP
public class HttpClientWrapper {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URI_BASE = "http://localhost/rest/index.php";
    private final Gson gson;

    public HttpClientWrapper(Gson gson) {
        this.gson = gson;
    }

    public HttpRequest buildRequest(String endpoint, String method, Object body) {
        HttpRequest.Builder requestBuilder = null;
        try {
            requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(URI_BASE + endpoint));
        } catch (URISyntaxException e) {
            System.err.println("Error al construir la URI: " + e.getMessage());
            return null;
        }

        switch (method) {
            case "GET" ->
                requestBuilder.GET();
            case "POST" ->
                requestBuilder.header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
            case "PUT" ->
                requestBuilder.header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
            case "PATCH" ->
                requestBuilder.header("Content-Type", "application/json")
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
            case "DELETE" ->
                requestBuilder.DELETE();
            default ->
                throw new IllegalArgumentException("Método HTTP no soportado: " + method);
        }

        return requestBuilder.build();
    }

    public <T> CompletableFuture<T> sendAsyncRequest(HttpRequest request,
            Function<HttpResponse<String>,T> responseHandler,
            boolean showLoadingAnimation) {

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(
                request, HttpResponse.BodyHandlers.ofString());

        if (showLoadingAnimation) {
            LoadingAnimation.show(responseFuture);
        }

        return responseFuture.thenApply(response -> {
            if (responseHandler != null) {
                return responseHandler.apply(response);
            } else {
                return null;
            }
        }).exceptionally(ex -> {
            System.err.println("Error al enviar la petición: " + ex.getMessage());
            return null;
        });
    }

    // Gson getter para poder usar el mismo Gson en las clases que extienden
    public Gson getGson() {
        return gson;
    }
}