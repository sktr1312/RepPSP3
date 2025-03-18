package restclient.core;

import java.net.http.HttpResponse;

// Interfaz funcional para manejar la repuesta(HttpResponse) de las peticiones(HttpRequest) realizas de forma asincrona
@FunctionalInterface
public interface ResponseHandler<T> {
    T handleResponse(HttpResponse<String> response);
}