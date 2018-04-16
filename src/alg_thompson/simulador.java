/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


package alg_thompson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;


public class simulador {
    
    private String resultado;
    
    public simulador(){
        
    }
    
    public simulador(automata afn_simulacion, String regex){
        simular(regex,afn_simulacion);
    }
    
    public HashSet<estados_transiciones> eClosure(estados_transiciones eClosureEstado){
        Stack<estados_transiciones> pilaClosure = new Stack();
        estados_transiciones actual = eClosureEstado;
        actual.getTransiciones();
        HashSet<estados_transiciones> resultado = new HashSet();
        
        pilaClosure.push(actual);
        while(!pilaClosure.isEmpty()){
            actual = pilaClosure.pop();
           
            for (transiciones t: (ArrayList<transiciones>)actual.getTransiciones()){
                
                if (t.getSimbolo().equals(Main.EPSILON)&&!resultado.contains(t.getFin())){
                    resultado.add(t.getFin());
                    pilaClosure.push(t.getFin());
                }
            }
        }
        resultado.add(eClosureEstado); 
        return resultado;
    }
    
    public HashSet<estados_transiciones> move(HashSet<estados_transiciones> estados, String simbolo){
       
        HashSet<estados_transiciones> alcanzados = new HashSet();
        Iterator<estados_transiciones> iterador = estados.iterator();
        while (iterador.hasNext()){
            
            for (transiciones t: (ArrayList<transiciones>)iterador.next().getTransiciones()){
                estados_transiciones siguiente = t.getFin();
                String simb = (String) t.getSimbolo();
                if (simb.equals(simbolo)){
                    alcanzados.add(siguiente);
                }
                
            }
            
        }
        return alcanzados;
        
    }
    
    public estados_transiciones move(estados_transiciones estado, String simbolo){
        ArrayList<estados_transiciones> alcanzados = new ArrayList();
           
        for (transiciones t: (ArrayList<transiciones>)estado.getTransiciones()){
            estados_transiciones siguiente = t.getFin();
            String simb = (String) t.getSimbolo();
            
            if (simb.equals(simbolo)&&!alcanzados.contains(siguiente)){
                alcanzados.add(siguiente);
            }

        }
       
        return alcanzados.get(0);
    }
    
   
    public boolean simular(String regex, automata automata)
    {
        estados_transiciones inicial = automata.getEstadoInicial();
        ArrayList<estados_transiciones> estados = automata.getEstados();
        ArrayList<estados_transiciones> aceptacion = new ArrayList(automata.getEstadosAceptacion());
        
        HashSet<estados_transiciones> conjunto = eClosure(inicial);
        for (Character ch: regex.toCharArray()){
            conjunto = move(conjunto,ch.toString());
            HashSet<estados_transiciones> temp = new HashSet();
            Iterator<estados_transiciones> iter = conjunto.iterator();
            
            while (iter.hasNext()){
               estados_transiciones siguiente = iter.next();
             
                temp.addAll(eClosure(siguiente)); 
               
            }
            conjunto=temp;
            
            
        }
        
        
        boolean res = false;
        
        for (estados_transiciones estado_aceptacion : aceptacion){
            if (conjunto.contains(estado_aceptacion)){
                res = true;
            }
        }
        if (res){
            System.out.println("Aceptado");
            this.resultado = "Aceptado";
            return true;
        }
        else{
            System.out.println("NO Aceptado");
             this.resultado = "No Aceptado";
            return false;
        }
    }

    public String getResultado() {
            return resultado;
        }
    
        
        
    public String generarDOT(String nombreArchivo,automata automataFinito){
        String texto = "digraph automata_finito {\n";

        texto +="\trankdir=LR;"+"\n";
        
        texto += "\tgraph [label=\""+nombreArchivo+"\", labelloc=t, fontsize=20]; \n";
        texto +="\tnode [shape=doublecircle, style = filled,color = mediumseagreen];";
        //listar estados de aceptación
        for(int i=0;i<automataFinito.getEstadosAceptacion().size();i++){
            texto+=" "+automataFinito.getEstadosAceptacion().get(i);
        }
        //
        texto+=";"+"\n";
        texto +="\tnode [shape=circle];"+"\n";
        texto +="\tnode [color=midnightblue,fontcolor=white];\n" +"	edge [color=red];"+"\n";
       
        texto +="\tsecret_node [style=invis];\n" + "	secret_node -> "+automataFinito.getEstadoInicial()+" [label=\"inicio\"];" + "\n";
	
        for(int i=0;i<automataFinito.getEstados().size();i++){
            ArrayList<transiciones> t = automataFinito.getEstados().get(i).getTransiciones();
            for (int j = 0;j<t.size();j++){
                texto+="\t"+t.get(j).DOT_String()+"\n";
            }
            
        }
        texto+="}";
       
        
        
       
        File dummy = new File("");
        String path = dummy.getAbsolutePath();
        path+="/";
        String archivo =nombreArchivo+".dot";
        new File(path+"/GeneracionAutomatas/").mkdirs();
        String pathImagen = path+"GeneracionAutomatas/PNG/";
        path+="GeneracionAutomatas/DOT/";
        File TextFile = new File("/GeneracionAutomatas/DOT/"+nombreArchivo+".dot");
        FileWriter TextOut;
    
        try {
            TextOut = new FileWriter(path+nombreArchivo+".dot");
            TextOut.write(texto);
           
            TextOut.close();
        } catch (IOException ex) {
          
        }
        
       
        
        String comando = "dot -Tpng "+path+archivo + " > "+pathImagen+nombreArchivo+".png";
        try
        {
            ProcessBuilder pbuilder;

          
            pbuilder = new ProcessBuilder( "/opt/local/bin/dot", "-Tpng", "-o",pathImagen+nombreArchivo+".png",path+archivo );
            pbuilder.redirectErrorStream( true );
           
            pbuilder.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
          
        }
        
        
        return comando;
    }
   

}
