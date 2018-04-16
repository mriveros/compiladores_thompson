/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


package alg_thompson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;


public class alg_afn<T> {
    
   
    private automata afn;
    private String regex;
   
    
    public alg_afn(String regex) {
        this.regex = regex;
       
        
    }
    
   
   
    public void construct(){
        try {
        Stack pilaAFN = new Stack();
        //Crea un automata por cada operacion de manera a separar completamente una operacion del otro
        for (Character c : this.regex.toCharArray()) {
            switch(c){
                case '.':
                    automata concat_param1 = (automata)pilaAFN.pop();
                    automata concat_param2 = (automata)pilaAFN.pop();
                    automata concat_result = concatenacion(concat_param1,concat_param2);
                   
                    pilaAFN.push(concat_result);
                    this.afn=concat_result;
                    break;
                    
                case '*':
                     automata kleene = cerraduraKleene((automata) pilaAFN.pop());
                     pilaAFN.push(kleene);
                     this.afn=kleene;
                    break;
                
                    
                case '|':
                    
                    automata union_param1 = (automata)pilaAFN.pop();
                    automata union_param2 = (automata)pilaAFN.pop();
                    automata union_result = union(union_param1,union_param2);
                   
                    
                    pilaAFN.push(union_result);
                   
                    this.afn = union_result;
                    break;
                    
                default:
                    //crear un automata con cada simbolo
                    automata simple = afnSimple((T) Character.toString(c));
                    pilaAFN.push(simple);
                    this.afn=simple;
                    
                    
            }
        }
        this.afn.createAlfabeto(regex);
        this.afn.setTipo("AFN");
        
        
        }catch(Exception e){
            System.out.println("La expresion no es correcta");
        }
    
                
    }
   
    public automata afnSimple(T simboloRegex)
    {
        automata automataFN = new automata();
       
        estados_transiciones inicial = new estados_transiciones(0);
        estados_transiciones aceptacion = new estados_transiciones(1);
        
        transiciones tran = new transiciones(inicial, aceptacion,  simboloRegex);
        inicial.setTransiciones(tran);
        
        automataFN.addEstados(inicial);
        automataFN.addEstados(aceptacion);
        
        automataFN.setEstadoInicial(inicial);
        automataFN.addEstadosAceptacion(aceptacion);
        automataFN.setLenguajeR(simboloRegex+"");
        return automataFN;
       
    }   
   
    public automata cerraduraKleene(automata automataFN)
    {
        automata afn_kleene = new automata();
        
        
        estados_transiciones nuevoInicio = new estados_transiciones(0);
        afn_kleene.addEstados(nuevoInicio);
        afn_kleene.setEstadoInicial(nuevoInicio);
        
      
        for (int i=0; i < automataFN.getEstados().size(); i++) {
            estados_transiciones tmp = (estados_transiciones) automataFN.getEstados().get(i);
            tmp.setId(i + 1);
            afn_kleene.addEstados(tmp);
        }
        
       
        estados_transiciones nuevoFin = new estados_transiciones(automataFN.getEstados().size() + 1);
        afn_kleene.addEstados(nuevoFin);
        afn_kleene.addEstadosAceptacion(nuevoFin);
        
       
        estados_transiciones anteriorInicio = automataFN.getEstadoInicial();
        
        ArrayList<estados_transiciones> anteriorFin    = automataFN.getEstadosAceptacion();
        
       
        nuevoInicio.getTransiciones().add(new transiciones(nuevoInicio, anteriorInicio, Main.EPSILON));
        nuevoInicio.getTransiciones().add(new transiciones(nuevoInicio, nuevoFin, Main.EPSILON));
        
      
        for (int i =0; i<anteriorFin.size();i++){
            anteriorFin.get(i).getTransiciones().add(new transiciones(anteriorFin.get(i), anteriorInicio,Main.EPSILON));
            anteriorFin.get(i).getTransiciones().add(new transiciones(anteriorFin.get(i), nuevoFin, Main.EPSILON));
        }
        afn_kleene.setAlfabeto(automataFN.getAlfabeto());
        afn_kleene.setLenguajeR(automataFN.getLenguajeR());
        return afn_kleene;
    }
    
   public automata concatenacion(automata AFN1, automata AFN2){
       
       automata afn_concat = new automata();
            
       
        int i=0;
       
        for (i=0; i < AFN2.getEstados().size(); i++) {
            estados_transiciones tmp = (estados_transiciones) AFN2.getEstados().get(i);
            tmp.setId(i);
           
            if (i==0){
                afn_concat.setEstadoInicial(tmp);
            }
            
            if (i == AFN2.getEstados().size()-1){
                
                for (int k = 0;k<AFN2.getEstadosAceptacion().size();k++)
                {
                    tmp.setTransiciones(new transiciones((estados_transiciones) AFN2.getEstadosAceptacion().get(k),AFN1.getEstadoInicial(),Main.EPSILON));
                }
            }
            afn_concat.addEstados(tmp);

        }
        
        for (int j =0;j<AFN1.getEstados().size();j++){
            estados_transiciones tmp = (estados_transiciones) AFN1.getEstados().get(j);
            tmp.setId(i);

           
            if (AFN1.getEstados().size()-1==j)
                afn_concat.addEstadosAceptacion(tmp);
             afn_concat.addEstados(tmp);
            i++;
        }
       
        HashSet alfabeto = new HashSet();
        alfabeto.addAll(AFN1.getAlfabeto());
        alfabeto.addAll(AFN2.getAlfabeto());
        afn_concat.setAlfabeto(alfabeto);
        afn_concat.setLenguajeR(AFN1.getLenguajeR()+" " + AFN2.getLenguajeR()); 
        
       return afn_concat;
   }
   
    
    public automata union(automata AFN1, automata AFN2){
        automata afn_union = new automata();
        
        estados_transiciones nuevoInicio = new estados_transiciones(0);
       
        nuevoInicio.setTransiciones(new transiciones(nuevoInicio,AFN2.getEstadoInicial(),Main.EPSILON));

        afn_union.addEstados(nuevoInicio);
        afn_union.setEstadoInicial(nuevoInicio);
        int i=0;
        for (i=0; i < AFN1.getEstados().size(); i++) {
            estados_transiciones tmp = (estados_transiciones) AFN1.getEstados().get(i);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
        }
        
        for (int j=0; j < AFN2.getEstados().size(); j++) {
            estados_transiciones tmp = (estados_transiciones) AFN2.getEstados().get(j);
            tmp.setId(i + 1);
            afn_union.addEstados(tmp);
            i++;
        }
        
        
        estados_transiciones nuevoFin = new estados_transiciones(AFN1.getEstados().size() +AFN2.getEstados().size()+ 1);
        afn_union.addEstados(nuevoFin);
        afn_union.addEstadosAceptacion(nuevoFin);
        
       
        estados_transiciones anteriorInicio = AFN1.getEstadoInicial();
        ArrayList<estados_transiciones> anteriorFin    = AFN1.getEstadosAceptacion();
        ArrayList<estados_transiciones> anteriorFin2    = AFN2.getEstadosAceptacion();
        
        
        nuevoInicio.getTransiciones().add(new transiciones(nuevoInicio, anteriorInicio, Main.EPSILON));
        
        
        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin.get(k).getTransiciones().add(new transiciones(anteriorFin.get(k), nuevoFin, Main.EPSILON));
        
        for (int k =0; k<anteriorFin.size();k++)
            anteriorFin2.get(k).getTransiciones().add(new transiciones(anteriorFin2.get(k),nuevoFin,Main.EPSILON));
        
        HashSet alfabeto = new HashSet();
        alfabeto.addAll(AFN1.getAlfabeto());
        alfabeto.addAll(AFN2.getAlfabeto());
        afn_union.setAlfabeto(alfabeto);
        afn_union.setLenguajeR(AFN1.getLenguajeR()+" " + AFN2.getLenguajeR()); 
        return afn_union;
    }
    
    public void setAfn(automata afn) {
        this.afn = afn;
    }

    public String getRegex() {
        return regex;
    }
    
    public automata getAfn() {
        return this.afn;
    }


    public void setRegex(String regex) {
        this.regex = regex;
    }
     
    
    
    

}
