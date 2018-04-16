/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/

package alg_thompson;

import java.util.ArrayList;
import java.util.HashSet;


public class automata {
    
    private HashSet alfabeto;
    private String tipo;
    private estados_transiciones inicial;
    private final ArrayList<estados_transiciones> aceptacion;
    private final ArrayList<estados_transiciones> estados;
    private String[] resultadoRegex;
    private String lenguajeR ;
    
    public automata()
    {
        this.alfabeto = new HashSet();
        this.resultadoRegex = new String[3];
        this.estados = new ArrayList();
        this.aceptacion = new ArrayList();
        
        
    }
    
  
    public estados_transiciones getEstadoInicial() {
        return inicial;
    }
    
    public void setEstadoInicial(estados_transiciones inicial) {
        this.inicial = inicial;
    }
    
    public ArrayList<estados_transiciones> getEstadosAceptacion() {
        return aceptacion;
    }
   
    public void addEstadosAceptacion(estados_transiciones fin) {
        this.aceptacion.add(fin);
    }

    
    public ArrayList<estados_transiciones> getEstados() {
        return estados;
    }
   
    public estados_transiciones getEstados(int index){
        return estados.get(index);
    }
    
    
    public void addEstados(estados_transiciones estado) {
        this.estados.add(estado);
    }
    
    public HashSet getAlfabeto() {
        return alfabeto;
    }
    
    
    public void createAlfabeto(String regex) {
        for (Character ch: regex.toCharArray()){
           
            if (ch != '|' && ch != '.' && ch != '*' && ch != Main.EPSILON_CHAR)
                this.alfabeto.add(Character.toString(ch));
        }
    }
    
    public void setAlfabeto(HashSet alfabeto){
        this.alfabeto=alfabeto;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }
    
    public String getTipo(){
        return this.tipo;
    }

    public estados_transiciones getInicial() {
        return inicial;
    }

    public void setInicial(estados_transiciones inicial) {
        this.inicial = inicial;
    }

    public String[] getResultadoRegex() {
        return resultadoRegex;
    }

    public void addResultadoRegex(int key, String value) {
        this.resultadoRegex[key] = value;
    }
    
    

    @Override
    public String toString(){
        String res = new String();
        res += "-------"+this.tipo+"---------\r\n";
        res += "Alfabeto: " + this.alfabeto+"\r\n";
        res += "Estado inicial: " + this.inicial +"\r\n";
        res += "Estados de aceptacion: " + this.aceptacion +"\r\n";
        res += "Estados: " + this.estados.toString()+"\r\n";
        res += "Transiciones: ";
        for (int i =0 ; i<this.estados.size();i++){
             estados_transiciones est = estados.get(i);
             res += est.getTransiciones()+"-";
        }
        res += "\r\n";
        res += "Lenguaje R: " +this.lenguajeR + "\r\n";
        res += "Cadena x ingresada: "+this.resultadoRegex[1] + "\r\n";
        res += "Resultado Operacion: "+ this.resultadoRegex[2] + "\r\n";
        
        
        return res;
    }

    public String getLenguajeR() {
        return lenguajeR;
    }

    public void setLenguajeR(String lenguajeR) {
        this.lenguajeR = lenguajeR;
    }
    
   

}
