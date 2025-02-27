package api.api.Cliente.src;

import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import api.api.Cliente.src.modelo.Cliente;
import  api.api.Cliente.src.utils.Request;

public class App {
    private static boolean exitM = false;
    private static int codCliente ;


    private static String leerCampoNoVacio(String nombreCampo) {
        String valor;
        do {
            System.out.println(nombreCampo + ": ");
            valor = System.console().readLine().trim();
            if (valor.isEmpty()) {
                System.out.println("Error: El campo " + nombreCampo + " no puede estar vacío");
            }
        } while (valor.isEmpty());
        return valor;
    }

    public static String solicitarDatosClienteComoJson() {

        System.out.println("Ingrese los datos del usuario: ");
        String nombre = leerCampoNoVacio("Nombre");
        String apellidos = leerCampoNoVacio("Apellidos");
        String codProvincia = leerCampoNoVacio("codProvincia");
        String vip = leerCampoNoVacio("vip (true/false)");
        String respuesta = String.format("""
            {
                "nombre": "%s",
                "apellidos": "%s",
                "codProvincia": %s,
                "vip": %s
            }
            """, nombre, apellidos, codProvincia, vip.toLowerCase());
            System.err.println(respuesta);
        return respuesta ;
    }


    public static void main(String[] args) {
        System.out.println("\nBienvenido al panel de control");
        do {
            System.out.println("1. Listar usuarios");
            System.out.println("2. Crear usuario");
            System.out.println("3. Eliminar usuario");
            System.out.println("4. Modificar un usuario");
            System.out.println("5. Salir");
            System.out.println("Seleccione una opcion: ");
            
            int opcion = Integer.parseInt(System.console().readLine());
            switch (opcion) {
                case 1:
                System.out.println("_".repeat(20));
                    System.out.println("\nListar usuarios");
                    try {
                        List<Cliente> response = Request.sendGetRequest("http://localhost/api/index.php/clientes");
                        response.forEach(System.out::println);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    break;
                case 2:
                    System.out.println("Crear usuario");
                    String jsonInput = solicitarDatosClienteComoJson();
                    try {
                        String[] response = Request.sendPostRequest("http://localhost/api/index.php/clientes", jsonInput);
                        String responseBody = response[1];
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                        int id = jsonResponse.get("id").getAsInt();
                        System.out.println("Cliente creado con éxito. ID: " + id);
                        codCliente = id;
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    break;
                case 3:
                    System.out.println("Eliminar usuario");
           
                    try {
                        List<Cliente> clientes = Request.sendGetRequest("http://localhost/api/index.php/clientes");
                        clientes.forEach(System.out::println);
                        System.out.println("Ingrese el ID del usuario a eliminar: ");
                        String userId = System.console().readLine();
                        String response = Request.sendDeleteRequest("http://localhost/api/index.php/clientes/" + userId);
                        System.out.println("Respuesta: " + response);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    break;
                case 4:
                    //hacer get para listar usuarios y luego modificar el seleccionado 
                    System.out.println("Modificar un usuario");
                    try {
                        List<Cliente> clientes = Request.sendGetRequest("http://localhost/api/index.php/clientes");
                        clientes.forEach(System.out::println);
                        System.out.println("Ingrese el ID del usuario a modificar: ");
                        String userIdM = System.console().readLine();
                        Cliente cliente = clientes.stream().filter(c -> c.getCodCliente() == Integer.parseInt(userIdM)).findFirst().orElse(null);
                        if (cliente != null) {
                            //ingresa los nuevos datos Nombre, apellidos, codProvincia, vip cuando termina de ingresar los datos se escribe vacio y se envia
                            System.out.println("Ingrese los nuevos datos del usuario en formato JSON: ");
                            String jsonInputM = solicitarDatosClienteComoJson();
                            String response = Request.sendPutRequest("http://localhost/api/index.php/clientes/" + userIdM, jsonInputM);     
                            System.out.println("Respuesta: " + response);
                            List<Cliente> clienteActualizado = Request.sendGetRequest("http://localhost/api/index.php/clientes/"+userIdM);
                            clienteActualizado.forEach(System.out::println);
                        } else {
                            System.out.println("Usuario no encontrado");
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }

                case 5: 
                    System.out.println("Salir");
                    exitM = true;
                    break;
                default:
                    System.out.println("Opcion no valida");
                    break;
            }
        } while (!exitM);
    }
}