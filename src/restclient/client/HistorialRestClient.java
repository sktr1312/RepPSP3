package restclient.client;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonParseException;

import examenrest.models.Historial;
import restclient.core.HttpClientWrapper;
import restclient.core.JsonResponseParser;

public class HistorialRestClient {
    private final HttpClientWrapper httpClient;

    public HistorialRestClient(HttpClientWrapper httpClient) {
        this.httpClient = httpClient;
    }

    public CompletableFuture<List<Historial>> getHistoriales() {
        HttpRequest request = httpClient.buildRequest("/historial", "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        return JsonResponseParser.parseOfList(
                                JsonResponseParser.extractJsonData(response), httpClient.getGson(), Historial.class);
                    } else {
                        System.out.println("Error al obtener historiales. Código: " + response.statusCode());
                        return null;
                    }
                },
                true);
    }

    public CompletableFuture<List<Historial>> getHistorialTelefono(int numTelefono) {
        HttpRequest request = httpClient.buildRequest("/historial/telefono/" + numTelefono, "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        try {
                            return JsonResponseParser.parseOfList(
                                    JsonResponseParser.extractJsonData(response), httpClient.getGson(),
                                    Historial.class);
                        } catch (JsonParseException e) {
                            System.out.println("Error al parsear el JSON: " + e.getMessage());

                            return null;
                        }

                    } else {
                        return null;
                    }
                },
                true);
    }

    public CompletableFuture<Boolean> postHistorial(Historial historial) {
        HttpRequest request = httpClient.buildRequest("/historial", "POST", historial);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        return true;
                    } else {
                        System.out.println("Error al insertar historial. Código: " + response.statusCode());
                        return false;
                    }
                },
                true);
    }

    public CompletableFuture<Boolean> putHistorial(Historial historial) {
        HttpRequest request = httpClient.buildRequest("/historial/" + historial.getTelefono().getNumTelefono(), "PUT",
                historial);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        System.out.println(response.body());
                        return true;
                    } else {
                        System.out.println("Error al actualizar historial. Código: " + response.statusCode());
                        return false;
                    }
                },
                true);
    }
}