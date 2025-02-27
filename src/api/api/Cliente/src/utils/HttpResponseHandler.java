package api.api.Cliente.src.utils;

public class HttpResponseHandler {
    public static void handleStatusCode(int statusCode, String body) throws Exception {
        switch (statusCode) {
            case 200:
                // OK - No necesita manejo especial
                break;
            case 201:
                // Created - No necesita manejo especial
                break;
            case 204:
                // No Content
                if (body == null || body.isEmpty()) {
                    throw new Exception("No se recibieron datos del servidor");
                }
                break;
            case 400:
                throw new Exception("Error de solicitud: " + body);
            case 401:
                throw new Exception("No autorizado: Se requiere autenticación");
            case 403:
                throw new Exception("Acceso prohibido: No tiene permisos necesarios");
            case 404:
                throw new Exception("Recurso no encontrado: " + body);
            case 409:
                throw new Exception("Conflicto en la solicitud: " + body);
            case 500:
                throw new Exception("Error interno del servidor: " + body);
            case 503:
                throw new Exception("Servicio no disponible");
            default:
                if (statusCode >= 300 && statusCode < 400) {
                    throw new Exception("Error de redirección: " + statusCode);
                } else if (statusCode >= 400 && statusCode < 500) {
                    throw new Exception("Error del cliente: " + statusCode + " - " + body);
                } else if (statusCode >= 500) {
                    throw new Exception("Error del servidor: " + statusCode + " - " + body);
                }
        }
    }
}