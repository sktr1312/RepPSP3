package Servidor;

public class Cliente {
    String nombre; 
    int NumConexiones;
    
    public Cliente(String nombre, int NumConexiones){
        this.nombre = nombre;
        this.NumConexiones = NumConexiones;
    }
    public String getNombre(){
        return nombre;
    }
    public int getNumConexiones(){
        return NumConexiones;
    }
    public void sumarConexion(){
        NumConexiones++;
    }
}
