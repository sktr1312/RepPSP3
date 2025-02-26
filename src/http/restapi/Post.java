package http.restapi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Post {
    public static void main(String[] args) {
        try {
            // URI del recurso
            URI uri = new URI("http://localhost/restapi/restapi.php/usuarios/");

            // Datos del cliente
            String input = "{\"nombre\":\"Juan lopez\",\"email\":\"rshsrh@example.com\",\"telefono\":\"12345432\"}";

            // cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // request HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(input))
                    .build();

            // Enviar request y obtener response
            client.sendAsync(request, BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(System.out::println);
            // HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            // // response.headers().map().forEach((k, v) -> System.out.println(k + ":" + v));

            // // Verificar respuesta
            // if (response.statusCode() != 201) {
            //     throw new RuntimeException(
            //             "Failed : HTTP error code : " + response.statusCode() + "\n" + response.body());
            // }

            // System.out.println("Cliente insertado exitosamente!" + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}