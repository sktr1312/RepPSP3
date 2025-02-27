package sockets.chat.server;

public class ServidorChatSocket {

    public void iniciar() {
        System.out.println("Servidor de chat iniciado");
    }

    public void enviarMensagem(String mensagem) {
        System.out.println("Mensagem enviada: " + mensagem);
    }

    public void encerrar() {
        System.out.println("Servidor de chat encerrado");
    }

    public void receberMensagem(String mensagem) {
        System.out.println("Mensagem recebida: " + mensagem);
    }

    public void enviarMensagemParaTodos(String mensagem) {
        System.out.println("Mensagem enviada para todos: " + mensagem);
    }

    public void enviarMensagemPara(String mensagem, String destinatario) {
        System.out.println("Mensagem enviada para " + destinatario + ": " + mensagem);
    }

    public void enviarMensagemParaTodosMenos(String mensagem, String remetente) {
        System.out.println("Mensagem enviada para todos menos " + remetente + ": " + mensagem);
    }

    public void enviarMensagemParaTodosMenos(String mensagem, String remetente, String destinatario) {
        System.out.println("Mensagem enviada para todos menos " + remetente + " e " + destinatario + ": " + mensagem);
    }

    public void enviarMensagemParaTodosMenos(String mensagem, String remetente, String destinatario1, String destinatario2) {
        System.out.println("Mensagem enviada para todos menos " + remetente + ", " + destinatario1 + " e " + destinatario2 + ": " + mensagem);
    }

    public void enviarMensagemParaTodosMenos(String mensagem, String remetente, String destinatario1, String destinatario2, String destinatario3) {
        System.out.println("Mensagem enviada para todos menos " + remetente + ", " + destinatario1 + ", " + destinatario2 + " e " + destinatario3 + ": " + mensagem);
    }

    public void enviarMensagemParaTodosMenos(String mensagem, String remetente, String destinatario1, String destinatario2, String destinatario3, String destinatario4) {
        System.out.println("Mensagem enviada para todos menos " + remetente + ", " );
    }
}
