package restclient.client;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import examenrest.models.Operador;
import restclient.core.HttpClientWrapper;
import restclient.core.JsonResponseParser;

public class OperadorRestClient {
    private final HttpClientWrapper httpClient;

    public OperadorRestClient(HttpClientWrapper httpClient) {
        this.httpClient = httpClient;
    }

    public CompletableFuture<List<Operador>> getOperadores() {
        HttpRequest request = httpClient.buildRequest("/operadores", "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        return JsonResponseParser.parseOfList(
                                JsonResponseParser.extractJsonData(response), httpClient.getGson(), Operador.class);
                    } else {
                        System.out.println("Error al obtener operadores. Código: " + response.statusCode());
                        return null;
                    }
                },
                true);
    }

    public CompletableFuture<Operador> getOperador(int codOperador) {
        HttpRequest request = httpClient.buildRequest("/operadores/" + codOperador, "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        List<Operador> operadores = JsonResponseParser.parseOfList(
                                JsonResponseParser.extractJsonData(response), httpClient.getGson(), Operador.class);
                        return operadores.isEmpty() ? null : operadores.get(0);
                    } else {
                        return null;
                    }
                },
                true);
    }

    public CompletableFuture<Boolean> postOperador(Operador operador) {
        JsonObject body = new JsonObject();
        body.addProperty("nombre", operador.getNombre());
        HttpRequest request = httpClient.buildRequest("/operadores", "POST", body);

        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        System.out.println(response.body());
                        return true;
                    } else {
                        System.out.println("Error al insertar operador. Código: " + response.statusCode());
                        return false;
                    }
                },
                true);
    }
}