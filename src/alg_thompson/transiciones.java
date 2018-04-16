/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/

package alg_thompson;

public class transiciones<T> {
    
    
    private estados_transiciones inicio;
    private estados_transiciones fin;
    private T simbolo;
    
   
    public transiciones(estados_transiciones inicio, estados_transiciones fin, T simbolo) {
        this.inicio = inicio;
        this.fin = fin;
        this.simbolo = simbolo;
    }
   
    public estados_transiciones getInicio() {
        return inicio;
    }
    
    public void setInicio(estados_transiciones inicio) {
        this.inicio = inicio;
    }
    
   
    public estados_transiciones getFin() {
        return fin;
    }

   
    public void setFin(estados_transiciones fin) {
        this.fin = fin;
    }
   
    public T getSimbolo() {
        return simbolo;
    }

   
    public void setSimbolo(T simbolo) {
        this.simbolo = simbolo;
    }
   
    @Override
    public String toString(){
        return "(" + inicio.getId() +"-" + simbolo  +"-"+fin.getId()+")";
    }
    public String DOT_String(){
        return (this.inicio+" -> "+this.fin+" [label=\""+this.simbolo+"\"];");
    }

}
