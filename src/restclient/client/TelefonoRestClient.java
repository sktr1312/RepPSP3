package restclient.client;

import java.net.http.HttpRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import examenrest.models.Telefono;
import restclient.core.HttpClientWrapper;
import restclient.core.JsonResponseParser;

public class TelefonoRestClient {
    private final HttpClientWrapper httpClient;

    public TelefonoRestClient(HttpClientWrapper httpClient) {
        this.httpClient = httpClient;
    }

    public CompletableFuture<List<Telefono>> getTelefonos() {
        HttpRequest request = httpClient.buildRequest("/telefonos", "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        return JsonResponseParser.parseOfList(
                                JsonResponseParser.extractJsonData(response), httpClient.getGson(), Telefono.class);
                    } else {
                        System.out.println("Error al obtener teléfonos. Código: " + response.statusCode());
                        return null;
                    }
                },
                true);
    }

    public CompletableFuture<Telefono> getTelefono(int numTelefono) {
        HttpRequest request = httpClient.buildRequest("/telefonos/" + numTelefono, "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    JsonArray jsonData = JsonResponseParser.extractJsonData(response);
                    Gson gson = httpClient.getGson();
                    List<Telefono> telefonos = JsonResponseParser.parseOfList(jsonData, gson, Telefono.class);
                    return telefonos.isEmpty() ? null : telefonos.get(0);
                },
                true);
    }

    public CompletableFuture<List<Telefono>> getTelefonosTitular(String titular) {
        HttpRequest request = httpClient.buildRequest("/telefonos/titular/" + titular, "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    List<Telefono> telefonos = null;
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        telefonos = JsonResponseParser.parseOfList(
                                JsonResponseParser.extractJsonData(response), httpClient.getGson(), Telefono.class);
                    }
                    return telefonos != null && !telefonos.isEmpty() ? telefonos : null;
                },
                true);
    }

    public CompletableFuture<List<Telefono>> getTelefonosPorOperador(int codOperador) {
        HttpRequest request = httpClient.buildRequest("/telefonos/operador/" + codOperador, "GET", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        return JsonResponseParser.parseOfList(
                                JsonResponseParser.extractJsonData(response), httpClient.getGson(), Telefono.class);
                    } else {
                        System.out.println("Error al obtener teléfonos por operador. Código: " + response.statusCode());
                        return null;
                    }
                },
                true);
    }

    public CompletableFuture<Set<String>> getTitulares() {
        HttpRequest request = httpClient.buildRequest("/telefonos/titulares", "GET", null);
        return httpClient.sendAsyncRequest(request, response -> {
            if (JsonResponseParser.isSuccessResponse(response)) {
                JsonArray jsonResponse = JsonResponseParser.extractJsonData(response);
                Set<String> titulares = new HashSet<>();
                for (JsonElement element : jsonResponse) {
                    JsonObject obj = element.getAsJsonObject();
                    titulares.add(obj.get("titular").getAsString());
                }
                return titulares;
            } else {
                System.out.println("Error al obtener titulares. Código: " + response.statusCode());
                return null;
            }
        }, true);
    }

    public CompletableFuture<Boolean> postTelefono(Telefono telefono) {
        HttpRequest request = httpClient.buildRequest("/telefonos", "POST", telefono);
        return httpClient.sendAsyncRequest(request,
                JsonResponseParser::isSuccessResponse,
                true);
    }

    public CompletableFuture<Boolean> putTelefono(Telefono telefono) {
        HttpRequest request = httpClient.buildRequest("/telefonos/" + telefono.getNumTelefono(), "PUT", telefono);
        return httpClient.sendAsyncRequest(request,
                JsonResponseParser::isSuccessResponse,
                true);
    }

    public CompletableFuture<Boolean> deleteTelefono(int numTelefono) {
        HttpRequest request = httpClient.buildRequest("/telefonos/" + numTelefono, "DELETE", null);
        return httpClient.sendAsyncRequest(request,
                response -> {
                    if (JsonResponseParser.isSuccessResponse(response)) {
                        System.out.println(response.body());
                        return true;
                    } else {
                        System.out.println("Error al eliminar teléfono. Código: " + response.statusCode());
                        return false;
                    }
                },
                true);
    }

    public CompletableFuture<Boolean> patchOperadorTelefono(Telefono telefono) {
        JsonObject body = new JsonObject();
        body.addProperty("codOperador", telefono.getOperador().getCodOperador());

        HttpRequest request = httpClient.buildRequest("/telefonos/" + telefono.getNumTelefono(), "PATCH", body);
        return httpClient.sendAsyncRequest(request,
                JsonResponseParser::isSuccessResponse,
                true);
    }
}