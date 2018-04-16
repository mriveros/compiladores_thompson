package alg_thompson;
/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


import java.util.ArrayList;
import java.util.Stack;



public class SyntaxTree<T> {

    private Nodo<T> root;       //nodo raiz del arbol
    private Nodo<T> actual;     //un nodo actual, sirve para despues definir el raiz
    private final Stack pila;         //en realidad es una cola, para meter nodos
    private ArrayList arrayNodos;//se guardan todos los nodos creados
    
    
   /**
    * Constructor 
    * inicializa la raiz del arbol
    */
    public SyntaxTree(){
        this.arrayNodos = new ArrayList();
        this.pila = new Stack();
        this.root= new Nodo("");
    }

 
    public void buildTree(String cadenaEnPrefix){
        
        this.root = new Nodo(cadenaEnPrefix);
        pila.add(this.root);
        buildPostFixTree((Nodo<T>) this.root);
        this.root=this.actual;
        
        

        
    }
    
   
    private void buildPostFixTree(Nodo<T> nodo){
       
      
        String texto_postfix = (String) nodo.getRegex();
       
        char letra_inicial = texto_postfix.charAt(0);
       
      
        if(letra_inicial!='*'&&letra_inicial!='|'&&letra_inicial!='.'){
            
            String sub_cadena = texto_postfix.substring(1);
            //System.out.println(sub_cadena);
            Nodo nuevo = new Nodo((sub_cadena));
            nuevo.setId(""+letra_inicial);
            nuevo.setIsLeaf(true);
            arrayNodos.add(nuevo);
            
            //nuevo.setIsLeaf(true);
           
            pila.remove(this.root);
            pila.add(nuevo);
            buildPostFixTree(nuevo);
           
           
        }
         else//verificar que no sea terminal
            
        {
            
            if(letra_inicial == '*'){
              
                String sub_cadena = texto_postfix.substring(1);
                //System.out.println(sub_cadena);
                Nodo nuevo = new Nodo(sub_cadena);
                nuevo.setId((T) (""+letra_inicial));
                
                Nodo nodoPila = (Nodo)pila.pop();
                nuevo.setIzquierda(nodoPila);
                arrayNodos.add(nuevo);
                
               
                pila.add(nuevo);
                buildPostFixTree(nuevo);
               
           
            }

            //si es un operador unario (como |, concat)
            else if(letra_inicial=='|'||letra_inicial=='.'){
               
               
               
                String sub_cadena = texto_postfix.substring(1);
        
                Nodo nuevo = new Nodo(sub_cadena);
                nuevo.setId(""+letra_inicial);
              
                nuevo.setDerecha((Nodo) pila.pop());
               
                
                if (!pila.isEmpty())
                    nuevo.setIzquierda((Nodo)pila.pop());
                else
                    nuevo.setIzquierda(nodo);
               
                 pila.add(nuevo);
                 arrayNodos.add(nuevo);
                 this.actual = nuevo;
                 if (!sub_cadena.isEmpty())
                    buildPostFixTree(nuevo);
                
            }
        }
        
        
        
        
    }

    public Nodo<T> getRoot() {
        return root;
    }

    public void setRoot(Nodo<T> root) {
        this.root = root;
    }
    
    public Nodo<T> getResultado() {
        return actual;
    }

    public void setResultado(Nodo<T> resultado) {
        this.actual = resultado;
    }

    public ArrayList getArrayNodos() {
        return arrayNodos;
    }

    public void setArrayNodos(ArrayList arrayNodos) {
        this.arrayNodos = arrayNodos;
    }
   
    
    
}