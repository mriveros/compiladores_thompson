/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


package alg_thompson;

import java.util.ArrayList;


public class estados_transiciones<T> {

    
    private T id;  
    
    private ArrayList<transiciones> transiciones = new ArrayList();
        
    
    public estados_transiciones(T id, ArrayList<transiciones> transiciones) {
        this.id = id;
        this.transiciones = transiciones;
    }
   
    public estados_transiciones(T identificador) {
        this.id = identificador;
        
    }
    
    
    public T getId() {
        return id;
    }
  
    public void setId(T id) {
        this.id = id;
    }
    
    public ArrayList<transiciones> getTransiciones() {
       
        return transiciones;
    }
    
    public void setTransiciones(transiciones tran) {
        this.transiciones.add(tran);
    }
    
    @Override
    public String toString(){
        return this.id.toString();
    }
    
    
}
