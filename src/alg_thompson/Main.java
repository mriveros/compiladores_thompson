/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


package alg_thompson;

import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JOptionPane;


public class Main {
   
    public static String EPSILON = "ε";
    public static char EPSILON_CHAR = EPSILON.charAt(0);
  
    public static void main(String[] args) {
       
        Scanner teclado = new Scanner(System.in);
        ReadFile read = new ReadFile();
        HashMap input = read.leerArchivo();
        LexerAnalyzer lexer = new LexerAnalyzer(input);
        lexer.vocabulario();
        lexer.check(input);  
        String regex;
        String cadena;
          
        
        System.out.println("ingrese la expresión regular, el string epsilon es: " + EPSILON);
        regex = teclado.next();
          

       
        System.out.println("ingrese la cadena x a comprobar");
        cadena = teclado.next();      
      
       String lenguaje = regex;
        
        RegexConverter convert = new RegexConverter();
        try{
            
           regex = convert.infixToPostfix(regex);
            
        }catch(Exception e){
            System.out.println("Expresión mal ingresada");
        }
       
        alg_afn ThomsonAlgorithim = new alg_afn(regex);
        
        double afnCreateStart = System.nanoTime();
        ThomsonAlgorithim.construct();
        double afnCreateStop = System.nanoTime();
        
        System.out.println(regex);
      
        automata afn_result = ThomsonAlgorithim.getAfn();
        System.out.println(afn_result);
       
        
         simulador simulador = new simulador();
      
         
        double afnSimulateStart = System.nanoTime();
        simulador.simular(cadena,afn_result);
        double afnSimulateStop = System.nanoTime();
       
        
        afn_result.addResultadoRegex(0, lenguaje);
        afn_result.addResultadoRegex(1, cadena);
        afn_result.addResultadoRegex(2, simulador.getResultado());
        
        
        //Crear Archivos
        crearArchivos(afn_result, (afnCreateStop-afnCreateStart), (afnSimulateStop-afnSimulateStart), "AFN");
        
       
        alg_afd AFD = new alg_afd();
        
        
        double afdConvertStart = System.nanoTime();
        AFD.conversionAFN(afn_result);
        double afdConvertStop = System.nanoTime();
        
        automata afd_result = AFD.getAfd();
        
      
     
       
        double afdSimulateStart = System.nanoTime();
        simulador.simular(cadena,afd_result);
        double afdSimulateStop = System.nanoTime();
        
        
        afd_result.addResultadoRegex(0, lenguaje);
        afd_result.addResultadoRegex(1, cadena);
        afd_result.addResultadoRegex(2, simulador.getResultado());
        
        crearArchivos(afd_result, (afdConvertStop-afdConvertStart), (afdSimulateStop-afdSimulateStart), "AFD Subconjuntos");
     
       
        String regexExtended = regex+"#.";
       
        
        SyntaxTree syntaxTree = new SyntaxTree();
        syntaxTree.buildTree(regexExtended);
      
        System.out.println(syntaxTree.getRoot().postOrder());
      
        //creación directa del AFD
        double afdDirectStart = System.nanoTime();
        AFD.creacionDirecta(syntaxTree);
        double afdDirectStop = System.nanoTime();
        
        automata afd_directo = AFD.getAfdDirecto();
        //simulación de la creación Directa AFD
        double afdDirectStartSim = System.nanoTime();
        simulador.simular(cadena,afd_directo);
        double afdDirectStopSim = System.nanoTime();
        
        afd_directo.addResultadoRegex(0, lenguaje);
        afd_directo.addResultadoRegex(1, cadena);
        afd_directo.addResultadoRegex(2, simulador.getResultado());
        
       //crearArchivos(afd_directo, (afdDirectStop-afdDirectStart),(afdDirectStopSim-afdDirectStartSim),"AFD Directo");
       
       
        
        
        //minimizar el AFD Directo 
        double minTimeStart = System.nanoTime();
        automata afd_min = AFD.minimizar(afd_directo);
        double minTimeStop = System.nanoTime();
        
        //simular minimización AFD Directo
        double minSimStart = System.nanoTime();
        simulador.simular(regex, afd_min);
        double minSimStop = System.nanoTime();
        
        afd_min.addResultadoRegex(0, lenguaje);
        afd_min.addResultadoRegex(1, cadena);
        afd_min.addResultadoRegex(2, simulador.getResultado());
        
        //crearArchivos(afd_min,(minTimeStop-minTimeStart),(minSimStop-minSimStart),"AFD Minimo Directo");
        
        
        //Minimizar el AFD Subconjuntos
        minTimeStart = System.nanoTime();
        automata afd_min_sub = AFD.minimizar(afd_result);
        minTimeStop = System.nanoTime();
        
        
    }
    /*
    * Método para crear los archivos TXT y DOT
    */
    public static void crearArchivos(automata tipoAutomata, double tiempoCreacion, double tiempoSimulacion, String tipo){
        
        FileCreator creadorArchivo = new FileCreator();
        simulador generadorGrafico = new simulador();
        
        creadorArchivo.crearArchivo(tipoAutomata.toString(), tiempoCreacion, tiempoSimulacion, tipo);
        
        generadorGrafico.generarDOT(tipo, tipoAutomata);
        
    }

}
