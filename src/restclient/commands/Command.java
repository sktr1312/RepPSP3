package restclient.commands;


//interfaz funcional para los comandos de las opciones del menu
@FunctionalInterface
public interface Command {
    void execute();
}
