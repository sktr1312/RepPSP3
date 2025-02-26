package concesionario;

import java.util.Random;
class Coche {
 static final boolean VER_MOVIMIENTOS=true;

 private String modelo;
 private int numCoche;
 private Cliente propietario;
 private boolean ocupado;
 private int visitas;
 private static Random random=new Random();

 public Coche (String modelo, int numCoche) {
 this.modelo=modelo;
 this.numCoche=numCoche;
 this.propietario=null;
 this.ocupado=false;
 this.visitas=0;
 }

 public synchronized boolean intentarComprar(Cliente cliente) {
 ocupado=true;
 try { Thread.sleep(random.nextInt(20)); } catch (InterruptedException ex) {}

 if(random.nextInt(100)<++visitas) {
 vendido(cliente);
 if (VER_MOVIMIENTOS) System.out.println(cliente+" compra "+this);
 return true;
 }
 else {
 ocupado=false;
 if (VER_MOVIMIENTOS) System.out.println(cliente+" NO LE CONVENCE "+this);
 return false;
 }
 }
 @Override
 public String toString() {
 String str= modelo+" "+numCoche+" ("+visitas+")";
 if(propietario!=null) str+=" > "+propietario;
 return str;
 }

 public synchronized void vendido(Cliente cliente) {
 this.propietario=cliente;
 }

 public synchronized boolean estaOcupado() { return ocupado; }
}