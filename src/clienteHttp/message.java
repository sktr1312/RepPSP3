package clienteHttp;

import java.util.List;

public class message {
    private static String solicitarDatosClienteComoJson() {
        StringBuilder jsonInput = new StringBuilder();
        String line;
        do {
            line = System.console().readLine();
            jsonInput.append(line);
        } while (!line.isEmpty());
        return jsonInput.toString();
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
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("Crear usuario");
                    String jsonInput = solicitarDatosClienteComoJson();
                    try {
                        String response = Request.sendPostRequest("http://localhost/api/index.php/clientes", jsonInput);
                        System.out.println("Respuesta: " + response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("Eliminar usuario");
                    System.out.println("Ingrese el ID del usuario a eliminar: ");
                    String userId = System.console().readLine();
                    try {
                        String response = Request
                                .sendDeleteRequest("http://localhost/api/index.php/clientes/" + userId);
                        System.out.println("Respuesta: " + response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    // hacer get para listar usuarios y luego modificar el seleccionado
                    System.out.println("Modificar un usuario");
                    try {
                        List<Cliente> clientes = Request.sendGetRequest("http://localhost/api/index.php/clientes");
                        clientes.forEach(System.out::println);
                        System.out.println("Ingrese el ID del usuario a modificar: ");
                        String userIdM = System.console().readLine();
                        Cliente cliente = clientes.stream().filter(c -> c.getCodCliente() == Integer.parseInt(userIdM))
                                .findFirst().orElse(null);
                        if (cliente != null) {
                            // ingresa los nuevos datos Nombre, apellidos, codProvincia, vip cuando termina
                            // de ingresar los datos se escribe vacio y se envia
                            System.out.println("Ingrese los nuevos datos del usuario en formato JSON: ");
                            String jsonInputM = solicitarDatosClienteComoJson();
                            String response = Request
                                    .sendPutRequest("http://localhost/api/index.php/clientes/" + userIdM, jsonInputM);
                            System.out.println("Respuesta: " + response);

                        } else {
                            System.out.println("Usuario no encontrado");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
