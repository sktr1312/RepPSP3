package restclient;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import examenrest.models.Historial;
import examenrest.models.Operador;
import examenrest.models.Telefono;
import restclient.client.HistorialRestClient;
import restclient.client.OperadorRestClient;
import restclient.client.TelefonoRestClient;
import restclient.config.RestClientConfig;
import restclient.core.HttpClientWrapper;

/**
 * Fachada para facilitar el acceso a los diferentes clientes REST
 */
public class RestClientFacade {
    private final TelefonoRestClient telefonoClient;
    private final OperadorRestClient operadorClient;
    private final HistorialRestClient historialClient;

    public RestClientFacade() {
        // Inicializar el cliente HTTP con la configuración del Gson
        HttpClientWrapper httpClient = new HttpClientWrapper(RestClientConfig.createGson());

        // Inicializar los clientes específicos
        this.telefonoClient = new TelefonoRestClient(httpClient);
        this.operadorClient = new OperadorRestClient(httpClient);
        this.historialClient = new HistorialRestClient(httpClient);
    }

    // Métodos para teléfonos
    public CompletableFuture<List<Telefono>> getTelefonos() {
        return telefonoClient.getTelefonos();
    }

    public CompletableFuture<Telefono> getTelefono(int numTelefono) {
        return telefonoClient.getTelefono(numTelefono);
    }

    public CompletableFuture<List<Telefono>> getTelefonosTitular(String titular) {
        return telefonoClient.getTelefonosTitular(titular);
    }

    public CompletableFuture<List<Telefono>> getTelefonosPorOperador(int codOperador) {
        return telefonoClient.getTelefonosPorOperador(codOperador);
    }

    public CompletableFuture<Set<String>> getTitulares() {
        return telefonoClient.getTitulares();
    }

    public CompletableFuture<Boolean> postTelefono(Telefono telefono) {
        return telefonoClient.postTelefono(telefono);
    }

    public CompletableFuture<Boolean> putTelefono(Telefono telefono) {
        return telefonoClient.putTelefono(telefono);
    }

    public CompletableFuture<Boolean> patchTelefonoTitular(Telefono telefono) {
        return telefonoClient.patchOperadorTelefono(telefono);
    }

    public CompletableFuture<Boolean> deleteTelefono(int numTelefono) {
        return telefonoClient.deleteTelefono(numTelefono);
    }

    // Métodos para operadores
    public CompletableFuture<List<Operador>> getOperadores() {
        return operadorClient.getOperadores();
    }

    public CompletableFuture<Operador> getOperador(int codOperador) {
        return operadorClient.getOperador(codOperador);
    }

    public CompletableFuture<Boolean> postOperador(Operador operador) {
        return operadorClient.postOperador(operador);
    }

    // Métodos para historiales
    public CompletableFuture<List<Historial>> getHistoriales() {
        return historialClient.getHistoriales();
    }

    public CompletableFuture<List<Historial>> getHistorialTelefono(int numTelefono) {
        return historialClient.getHistorialTelefono(numTelefono);
    }

    public CompletableFuture<Boolean> postHistorial(Historial historial) {
        return historialClient.postHistorial(historial);
    }

    public CompletableFuture<Boolean> putHistorial(Historial historial) {
        return historialClient.putHistorial(historial);
    }

    // Ejemplo de uso
    public static void main(String[] args) {
        RestClientFacade restClient = new RestClientFacade();

        // Ejemplo de uso para eliminar un teléfono
        if (restClient.deleteTelefono(611223344).join()) {
            System.out.println("Teléfono eliminado correctamente");
        } else {
            System.out.println("Error al eliminar el teléfono");
        }
    }
}