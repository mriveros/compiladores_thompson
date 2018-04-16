/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


package alg_thompson;

import java.util.ArrayList;
import java.util.Collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;


public class alg_afd {
    
    private automata afd;
    private automata afdDirecto;
    private automata afdMinimo;
    private final simulador simulador;
    private final HashMap resultadoFollowPos;
  
    
    public alg_afd(){
        this.resultadoFollowPos = new HashMap();
        simulador = new simulador();
        afd = new automata();
    }
    
    
   
    public void conversionAFN(automata afn){
      
        automata automata = new automata();
       
        Queue<HashSet<estados_transiciones>> cola = new LinkedList();
       
        estados_transiciones inicial = new estados_transiciones(0);
        automata.setEstadoInicial(inicial);
        automata.addEstados(inicial);
       

       
        HashSet<estados_transiciones> array_inicial = simulador.eClosure(afn.getEstadoInicial());
       
        for (estados_transiciones aceptacion:afn.getEstadosAceptacion()){
            if (array_inicial.contains(aceptacion))
                automata.addEstadosAceptacion(inicial);
        }
        
        
        cola.add(array_inicial);
      
        ArrayList<HashSet<estados_transiciones>> temporal = new ArrayList();
       
       int indexEstadoInicio = 0;
       while (!cola.isEmpty()){
           
            HashSet<estados_transiciones> actual = cola.poll();
           
            for (Object simbolo: afn.getAlfabeto())
            {
                
                HashSet<estados_transiciones> move_result = simulador.move(actual, (String) simbolo);

                HashSet<estados_transiciones> resultado = new HashSet();
                
                for (estados_transiciones e : move_result) 
                {
                    resultado.addAll(simulador.eClosure(e));
                }

                estados_transiciones anterior = (estados_transiciones) automata.getEstados().get(indexEstadoInicio);
                
                if (temporal.contains(resultado))
                {
                    ArrayList<estados_transiciones> array_viejo = automata.getEstados();
                    estados_transiciones estado_viejo = anterior;
                   
                    estados_transiciones estado_siguiente = array_viejo.get(temporal.indexOf(resultado)+1);
                    estado_viejo.setTransiciones(new transiciones(estado_viejo,estado_siguiente,simbolo));

                }
                
                else
                {
                    temporal.add(resultado);
                    cola.add(resultado);

                    estados_transiciones nuevo = new estados_transiciones(temporal.indexOf(resultado)+1);
                    anterior.setTransiciones(new transiciones(anterior,nuevo,simbolo));
                    automata.addEstados(nuevo);
                   
                    for (estados_transiciones aceptacion:afn.getEstadosAceptacion()){
                        if (resultado.contains(aceptacion))
                            automata.addEstadosAceptacion(nuevo);
                    }
                }
               

            }
            indexEstadoInicio++;

           }
        
        this.afd = automata;
        
        definirAlfabeto(afn);
        this.afd.setTipo("AFD");
        System.out.println(afd);
        
    }
    
   
    public void creacionDirecta(SyntaxTree arbolSintactico){
        
        
        generarNumeracionNodos(arbolSintactico);
        
        ArrayList<Nodo> arrayNodos = arbolSintactico.getArrayNodos();      
        
        for (int i = 0;i<arrayNodos.size();i++){
            if (arrayNodos.get(i).getId().equals("*")||arrayNodos.get(i).getId().equals("."))
                followPos(arrayNodos.get(i));
        }
        toStringFollowPos();
        
        
        crearEstados(arbolSintactico);
       
        
        
    }
    
    
    public boolean nullable(Nodo expresion){
      
        if (expresion.getId().equals(Main.EPSILON))
            return true;
        
        else if (expresion.getId().equals("*"))
            return true;
       
        else if (expresion.getId().equals("|"))
            return (nullable(expresion.getIzquierda()))||(nullable(expresion.getDerecha()));
        
        else if (expresion.getId().equals("."))
            return (nullable(expresion.getIzquierda()))&&(nullable(expresion.getDerecha()));
         
        else if (expresion.isIsLeaf()==true)
            return false;
      
        
       
        return false;
        
    }
    
    
    public TreeSet firstPos(Nodo nodoEval){
        TreeSet resultado = new TreeSet();
       
        if (nodoEval.getId().equals(Main.EPSILON))
            return resultado;
       
        else if (nodoEval.isIsLeaf()){
            resultado.add(nodoEval);
            //return resultado;
        }
        
        else if (nodoEval.getId().equals("|")){
           resultado.addAll(firstPos(nodoEval.getIzquierda()));
           resultado.addAll(firstPos(nodoEval.getDerecha()));
           return resultado;
           
        }
       
        else if (nodoEval.getId().equals(".")){
            if (nullable(nodoEval.getIzquierda())){
                resultado.addAll(firstPos(nodoEval.getIzquierda()));
                resultado.addAll(firstPos(nodoEval.getDerecha()));
            }
            else{
                resultado.addAll(firstPos(nodoEval.getIzquierda()));
            }
        }
        
        else if (nodoEval.getId().equals("*")){
            resultado.addAll(firstPos(nodoEval.getIzquierda()));
        }
        
        return resultado;
    }
    
    
    public ArrayList lastPos(Nodo nodoEval){
        ArrayList resultado = new ArrayList();
        
        if (nodoEval.getId().equals(Main.EPSILON))
            return resultado;
          
        else if (nodoEval.isIsLeaf()){
           resultado.add(nodoEval);
           return resultado;
        }
        else if (nodoEval.getId().equals("*")){
            resultado.addAll(lastPos(nodoEval.getIzquierda()));
        }
        else if (nodoEval.getId().equals("|")){
            resultado.addAll(lastPos(nodoEval.getIzquierda()));
            resultado.addAll(lastPos(nodoEval.getDerecha()));
        }
        else if (nodoEval.getId().equals(".")){
            if (nullable(nodoEval.getDerecha())){
                
                resultado.addAll(lastPos(nodoEval.getIzquierda()));
                resultado.addAll(lastPos(nodoEval.getDerecha()));
            }
            else{
                resultado.addAll(lastPos(nodoEval.getDerecha()));
            }
        }
        
        return resultado;
    }
    
    
    public void followPos(Nodo nodoEval){
        
        if (nodoEval.getId().equals("*")){
            
            
            ArrayList<Nodo> lastPosition = lastPos(nodoEval);
           
            TreeSet<Nodo> firstPosition = firstPos(nodoEval);
              
            for(int j=0;j<lastPosition.size();j++){
                int numero = lastPosition.get(j).getNumeroNodo();

                if(resultadoFollowPos.containsKey(numero)){
                 
                    firstPosition.addAll((Collection)resultadoFollowPos.get(numero));
                    
                }
               
                    
                    resultadoFollowPos.put(numero,firstPosition);
                   
            }
        }
       
        else if (nodoEval.getId().equals(".")){
           
            ArrayList<Nodo> lastPosition = lastPos(nodoEval.getIzquierda());
           
            TreeSet<Nodo> firstPosition = firstPos(nodoEval.getDerecha());
            
            
            for (int i = 0;i<lastPosition.size();i++){
                int numero = (int) lastPosition.get(i).getNumeroNodo();
               
                if (resultadoFollowPos.containsKey(numero)){
                   
                    firstPosition.addAll((Collection) resultadoFollowPos.get(numero));//merge
                    
                }
                    
                
                resultadoFollowPos.put(numero, firstPosition);
                firstPosition = firstPos(nodoEval.getDerecha());
            }
            
        }
        
       
    }
    
    
    private void generarNumeracionNodos(SyntaxTree arbol){
        ArrayList<Nodo> arrayNodos = arbol.getArrayNodos();
        int index = 1;
        for (int i = 0 ;i<arrayNodos.size();i++){
            if (arrayNodos.get(i).isIsLeaf()){
                arrayNodos.get(i).setNumeroNodo(index);
                index++;
            }
        }
      
        arbol.setArrayNodos(arrayNodos);
        
    }
    
   
    public void crearEstados(SyntaxTree arbolSintactico){
        automata afd_result = new automata();
        afd_result.setTipo("AFD DIRECTO");
        
        definirAlfabeto(afd_result, arbolSintactico);
       
        estados_transiciones inicial = new estados_transiciones(0);
        TreeSet<Nodo> resultadoInicial = firstPos(arbolSintactico.getRoot());
        afd_result.setEstadoInicial(inicial);
        afd_result.addEstados(inicial);
        
       
        ArrayList<ArrayList<TreeSet>> estadosCreados = new ArrayList();
       
        ArrayList conversionInicial = new ArrayList(resultadoInicial);
        
        estadosCreados.add(conversionInicial);
        
         for (Nodo temp: (ArrayList<Nodo>)conversionInicial){
            if (temp.getId().equals("#"))
                afd_result.addEstadosAceptacion(inicial);
        }
        
        int indexEstadoInicio=0;
        int indexEstados=1;
       
        Queue<ArrayList> cola = new LinkedList();
        cola.add(conversionInicial);
        
        while(!cola.isEmpty()){
            
           
            ArrayList<Nodo> actual = cola.poll();
            boolean control = true;
            for (String letra: (HashSet<String>)afd_result.getAlfabeto()){
                
                
                
                
                ArrayList temporal = new ArrayList();
                
                for (Nodo n: actual){
                    if (n.getId().equals(letra)){
                        temporal.addAll((TreeSet<Nodo>) resultadoFollowPos.get(n.getNumeroNodo()));

                    }
                   
                    
                }
                if (control){
                
                if (!estadosCreados.contains(temporal)){

                    estados_transiciones siguiente = new estados_transiciones(indexEstados);
                    indexEstados++;
                    estados_transiciones estadoAnterior = afd_result.getEstados(indexEstadoInicio);
                    
                    estadoAnterior.setTransiciones(new transiciones(estadoAnterior,siguiente,letra));
                    afd_result.addEstados(siguiente);

                    cola.add(temporal);
                    estadosCreados.add(temporal);
                   
                    
                    for (Nodo temp: (ArrayList<Nodo>)temporal){
                        if (temp.getId().equals("#")&&!afd_result.getEstadosAceptacion().contains(siguiente))
                            afd_result.addEstadosAceptacion(siguiente);
                    }
                }
                else{
                   
                    estados_transiciones estadoAnterior = afd_result.getEstados(indexEstadoInicio);
                    estados_transiciones estadoSiguiente = afd_result.getEstados(estadosCreados.indexOf(temporal));
                    estadoAnterior.setTransiciones(new transiciones(estadoAnterior,estadoSiguiente,letra));
                }
                }
          
            }
            indexEstadoInicio++;
            
        }
        
       
        
        System.out.println(afd_result);
        this.afdDirecto=afd_result;
        
    }
    
    public automata quitarEstadosTrampa(automata afd){
        ArrayList<estados_transiciones> estadoAQuitar = new ArrayList();
        
        for (int i = 0;i<afd.getEstados().size();i++){
            int verificarCantidadTransiciones = afd.getEstados().get(i).getTransiciones().size();
            int contadorTransiciones=0;
            for (transiciones t : (ArrayList<transiciones>)afd.getEstados().get(i).getTransiciones()){
                if (afd.getEstados().get(i)==t.getFin()){
                    contadorTransiciones++;
                }
                
            }
            if (verificarCantidadTransiciones==contadorTransiciones&&contadorTransiciones!=0){
                
              estadoAQuitar.add(afd.getEstados().get(i));
            }
            
        }
       
        for (int i = 0;i<estadoAQuitar.size();i++){
              for (int j = 0;j<afd.getEstados().size();j++){
                    ArrayList<transiciones> arrayT = afd.getEstados().get(j).getTransiciones();
                    int cont =0;
                   
                    while(arrayT.size()>cont){
                        transiciones t = arrayT.get(cont);
                       
                        if (t.getFin()==estadoAQuitar.get(i)){
                            afd.getEstados().get(j).getTransiciones().remove(t);
                            cont--;
                        }
                        cont++;

                    }
                }
               
                afd.getEstados().remove(estadoAQuitar.get(i));
        }
       
        for (int i = 0;i<afd.getEstados().size();i++){
            afd.getEstados().get(i).setId(i);
        }
        
        
        return afd;
    }
    
   
    private void toStringFollowPos() {
        System.out.println("follow pos");
       
        Iterator it = resultadoFollowPos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer,Nodo> e = (Map.Entry)it.next();
            System.out.println(e.getKey() + " " + e.getValue());
        }
    }  
   
    /**
     *  Algoritmo de Hopcroft 
     */
    public void minimizacionAFD(automata AFD){
        ArrayList<ArrayList<estados_transiciones>> particionP = new ArrayList();
       
        ArrayList<estados_transiciones> estadosSinAceptacion = new ArrayList();
        for (int i = 0 ; i<AFD.getEstados().size();i++){
            if (!AFD.getEstadosAceptacion().contains(AFD.getEstados().get(i))){
               estadosSinAceptacion.add(AFD.getEstados(i));
            }
        }
        particionP.add(estadosSinAceptacion);
        particionP.add(AFD.getEstadosAceptacion());
       
      
            
        
       
        int key= 0;
        HashMap<estados_transiciones,ArrayList<Integer>> L = new HashMap();
        
        for (int p=0;p<particionP.size();p++){
           ArrayList<estados_transiciones> grupoG = particionP.get(p);
          
            for (estados_transiciones s: grupoG){
                 ArrayList<Integer> Ds = new ArrayList();
               
                for (String alfabeto: (HashSet<String>)AFD.getAlfabeto()){
                    estados_transiciones t = simulador.move(s, alfabeto);
                    
                    for (int j = 0 ;j<particionP.size();j++){
                        
                        
                        if (particionP.get(j).contains(t)){
                            Ds.add(j);
                            
                        }
                        L.put(s, Ds);
                       
                      
                    }
                }
                
                
               
            key++;    
            }
             
           
            int i = 0;
           
         ArrayList Ki = new ArrayList();
         while (!L.isEmpty()){  
                HashMap<ArrayList<Integer>, ArrayList<estados_transiciones>> tabla2 = new HashMap();
                for (estados_transiciones e : grupoG) {
                        ArrayList<Integer> alcanzados = L.get(e);
                        if (tabla2.containsKey(alcanzados))
                            tabla2.get(alcanzados).add(e);
                        else {
                            ArrayList<estados_transiciones> tmp = new ArrayList();
                            tmp.add(e);
                            tabla2.put(alcanzados, tmp);
                        }
                    }
              
                
             
             
            i++;
                
            
            System.out.println("----");
            System.out.println(particionP);
            System.out.println(Ki);
            System.out.println(grupoG);
            System.out.println("----");
            if (Ki.get(0)!=grupoG){
                particionP.remove(grupoG);
                System.out.println(Ki);
                System.out.println("Ti" + Ki.get(1));
               for (int j  =0 ;j<Ki.size();j++){
                   particionP.add((ArrayList<estados_transiciones>) Ki.get(j));
               }
                
            }
           
        }
         System.out.println(particionP);
        }
        
    }
    
    public void definirAlfabeto(automata afd, SyntaxTree arbol){
      HashSet alfabeto = new HashSet();
      String expresion = arbol.getRoot().postOrder();
      for (Character ch: expresion.toCharArray()){
          if (ch!='*'&&ch!='.'&&ch!='|'&&ch!='#'&&ch!=Main.EPSILON_CHAR){
              alfabeto.add(Character.toString(ch));
          }
      }
      afd.setAlfabeto(alfabeto);

  }
    
    
    private void definirAlfabeto(automata afn){
        
        this.afd.setAlfabeto(afn.getAlfabeto());
    }
    
    
    public automata minimizar (automata AFD){
        HashMap<estados_transiciones,ArrayList<Integer>> tablaGruposAlcanzados;
        HashMap<ArrayList<Integer>, ArrayList<estados_transiciones>> tablaParticiones;
        
        ArrayList<ArrayList<estados_transiciones>> particion = new ArrayList();
        
        
        ArrayList<estados_transiciones> estadosSinAceptacion = new ArrayList();
        for (int i = 0 ; i<AFD.getEstados().size();i++){
            if (!AFD.getEstadosAceptacion().contains(AFD.getEstados().get(i))){
               estadosSinAceptacion.add(AFD.getEstados(i));
            }
        }
       
        particion.add(estadosSinAceptacion);
        particion.add(AFD.getEstadosAceptacion());      
        
        
        ArrayList<ArrayList<estados_transiciones>> nuevaParticion;
        while (true) {
           
            nuevaParticion = new ArrayList();
            
            for (ArrayList<estados_transiciones> grupoG : particion) {
                
                if (grupoG.size() == 1) {
                   
                    nuevaParticion.add(grupoG);
                }
                else {
                    
                    tablaGruposAlcanzados = new HashMap();
                    
                    
                    for (estados_transiciones s : grupoG)
                    {
                        ArrayList<Integer> gruposAlcanzados = new ArrayList();
                       
                        for (String a : (HashSet<String>)AFD.getAlfabeto())
                        {
                            
                            estados_transiciones t = simulador.move(s, a);

                            for (int pos=0; pos < particion.size(); pos++) 
                            {
                                ArrayList grupoH = particion.get(pos);

                                if (grupoH.contains(t)) 
                                {
                                    gruposAlcanzados.add(pos);

                                   
                                    break;
                                }
                            }

                        }
                    
                        
                        tablaGruposAlcanzados.put(s, gruposAlcanzados);
                    }
                    
                    tablaParticiones = new HashMap();
                    for (estados_transiciones e : grupoG) {
                        ArrayList<Integer> alcanzados = tablaGruposAlcanzados.get(e);
                        if (tablaParticiones.containsKey(alcanzados))
                            tablaParticiones.get(alcanzados).add(e);
                        else {
                            ArrayList<estados_transiciones> tmp = new ArrayList();
                            tmp.add(e);
                            tablaParticiones.put(alcanzados, tmp);
                        }
                    }
                    

                    for (ArrayList<estados_transiciones> c : tablaParticiones.values())
                        nuevaParticion.add(c);
                }
            }
            
            
            
            if (nuevaParticion.equals(particion))
                break;
            else
                particion = nuevaParticion;
        }
        System.out.println("grupos particionados");
        System.out.println(particion);
    
       
        automata afd_min = new automata();
        HashMap<estados_transiciones, estados_transiciones> gruposMin = new HashMap<>();
        
        for (int i =0;i<particion.size();i++){
             ArrayList<estados_transiciones> grupoJ = particion.get(i);
            //se crea un nuevo estado con cada grupo de la partición
            estados_transiciones nuevo = new estados_transiciones(i);
            
           
            if (particion.get(i).contains(AFD.getEstadoInicial())){
                
               afd_min.setEstadoInicial(nuevo);
            }
           
            for (int j = 0 ;j<AFD.getEstadosAceptacion().size();j++){
                 if (particion.get(i).contains(AFD.getEstadosAceptacion().get(j)))
                     afd_min.addEstadosAceptacion(nuevo);
            }
           
           afd_min.addEstados(nuevo);
            
          
            for (estados_transiciones clave : grupoJ)
                gruposMin.put(clave, afd_min.getEstados(i));
          
            
        }
        
        
        System.out.println(gruposMin);
        
        for (int i=0; i < particion.size(); i++) {
            
            estados_transiciones representante = particion.get(i).get(0);
            
           
            estados_transiciones origen = afd_min.getEstados(i);
            
            
            for (transiciones trans :(ArrayList<transiciones>) representante.getTransiciones()) {
                estados_transiciones destino = gruposMin.get(trans.getFin());
                origen.setTransiciones(new transiciones(origen,destino, trans.getSimbolo()));
            }
        }
        afd_min.setAlfabeto(AFD.getAlfabeto());
        afd_min.setTipo("AFD Minimizado: ");
        System.out.println(afd_min);
        
      
        afd_min = quitarEstadosTrampa(afd_min);
        
        
        this.afdMinimo=afd_min;
        return afd_min;
    }
       
    
    
    public automata getAfd() {
        return afd;
    }
   
    public automata getAfdDirecto(){
        return this.afdDirecto;
    }
    
    
    public automata getAfdMinimo() {
        return afdMinimo;
    }
    
    
  

}

