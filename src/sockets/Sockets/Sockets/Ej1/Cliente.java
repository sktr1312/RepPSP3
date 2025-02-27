public static void main(String[] args) {
    String servidor = "localhost", FIN = "fin", mensaje = "";
    int puerto = 7;
    Scanner sc = new Scanner(System.in);
    Socket socket = null;
    DataInputStream in = null;
    DataOutputStream out = null;

    try {
        System.out.println("Introduzca el servidor:");
        servidor = sc.nextLine();
        socket = new Socket(servidor, puerto);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        
        System.out.println("Conectado con el servidor");
        while (!mensaje.equalsIgnoreCase(FIN)) {
            try {
                String strRecibido = in.readUTF();
                if(strRecibido.contains("apagará")) break;
                System.out.println("Cliente recibe: " + strRecibido);
                mensaje = sc.nextLine();
                out.writeUTF(mensaje);
                System.out.println("Cliente envía: " + mensaje);
            } catch (SocketException e) {
                System.out.println("El servidor se ha apagado. No se pueden enviar más comandos.");
                break;
            }
        }
    } catch (IOException e) {
        System.out.println("Error de conexión: " + "500");
    } finally {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (sc != null) {
                sc.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}