/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


package alg_thompson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LexerAnalyzer {
    
    
    private final HashMap<Integer,String> cadena;
    
    private final String letter= "[a-zA-z]";
     //System.out.println(letter);
    private final String digit="\\d";
    private final String number=digit+"("+digit+")*";
    private final String ident= letter + "("+letter+"|"+digit+")*"; //identificador
    private final String string="\""+"("+number+"|"+letter+"|[^\\\"])*"+"\"";
    private final String character="\'"+"("+number+"|"+letter+"|[^\\\'])"+"\'";
    private final String espacio = "(\\s)*";
    private boolean strict =false;
    private boolean output = true;
    
    private automata letter_;
    private automata digit_;
    private automata ident_;
    private automata string_;
    private automata character_;
    private automata number_;
    private automata basicSet_;
    private automata igual_;
    private automata plusOrMinus_;
    private automata espacio_;
    private simulador sim;
    
    
 
    public LexerAnalyzer(HashMap cadena){
        this.sim = new simulador();
        this.cadena=cadena;
        
        
    }
    
   
  
    public ArrayList checkExpression (String regex,int lineaActual,int index){
        String cadena_encontrada="";
        String cadena_revisar = this.cadena.get(lineaActual).substring(index);
        ArrayList res = new ArrayList();
        try{
        Pattern pattern = Pattern.compile(regex);
        
       
        Matcher matcher = pattern.matcher(cadena_revisar);
        Pattern p = Pattern.compile("."+"|"+"."+this.espacio);
         Matcher m = p.matcher(cadena_revisar);
       
       if (m.matches())
           return new ArrayList();
        if (matcher.find()) {
            cadena_encontrada=matcher.group();
            
            
            res.add(matcher.end());   
            res.add(cadena_encontrada);
            return res;
            
        }
        else{//si no lo encuentra es porque hay un error
                if (!cadena_revisar.isEmpty())//si 
                    System.out.println("Error en la linea " + lineaActual + ": la cadena " + cadena_revisar + " es inválida");
                else
                    System.out.println("Error en la línea " + lineaActual + ": falta un identificador");
                
            
        }
        } catch(Exception e){
           System.out.println("Error en la linea " + lineaActual+ ": la cadena " + cadena_revisar + " es inválida");
        }
        
       
        return res;
    }
    
    public void check(HashMap<Integer,String> cadena){
        int lineaActual = 1;
        int index = 0;
        ArrayList res = checkExpression("Cocol = \"COMPILER\""+this.espacio,lineaActual,index);
        
      
        int index2  = returnArray(res);
       
        ArrayList res2 = checkExpression(this.ident,lineaActual,index2);
       
        
        ArrayList scan = scannerSpecification(lineaActual);
        if (scan.isEmpty())
            output=false;
        
        
        lineaActual = cadena.size();
        ArrayList res3 = checkExpression("\"END\""+this.espacio,lineaActual,0);
        int index4 = returnArray(res3);
        
        ArrayList res4 = checkExpression(this.ident,lineaActual,index4);
        int index5 = returnArray(res4);
        //revisar identificadores
        if (!res4.isEmpty()&&!res2.isEmpty()){
            if (!res4.get(1).equals(res2.get(1))){
                System.out.println("Error Linea " + lineaActual + ": los identificadores no coinciden");
            }
           
        
            
        }
        
        ArrayList res5 = checkExpression("\'.\'"+this.espacio,lineaActual,index5);
       // System.out.println(res5.get(1));
        
        
        
        
    }
   
    public ArrayList<String> scannerSpecification(int lineaActual)
    {
        int returnIndex=0;
        String returnString= "";
        
        //characters
        lineaActual++;
        //["Characters" = {SetDecl}
        if (!this.cadena.get(lineaActual).contains("CHARACTERS")){
            System.out.println("No contiene la palabra CHARACTERS");
            return new ArrayList();
        }
         lineaActual++;
            
        while (true){
            ArrayList res2 = setDeclaration(lineaActual);
            if (res2.isEmpty()){
                lineaActual--;
                break;
                
            }
            returnIndex += (int)res2.get(0);
            returnString += (String)res2.get(1);
            lineaActual++;
        }
        //keywords
        lineaActual++;
        //whitespaceDecl
        lineaActual++;
              
        ArrayList outputScan = new ArrayList();
        outputScan.add(returnIndex);
        outputScan.add(returnString);
       return outputScan;
    }
    
    
    public ArrayList<String> setDeclaration(int lineaActual){
        
        if (this.cadena.get(lineaActual).contains("\"END\"")||this.cadena.get(lineaActual).contains("KEYWORDS"))
            return new ArrayList();
       
        ArrayList res1 = checkAutomata(this.ident_,lineaActual,0);
        int index1  = returnArray(res1);
        if (res1.isEmpty()){
            return new ArrayList();
        }
      
        ArrayList res2 = checkAutomata(this.igual_,lineaActual,index1);
        
        int index2 = returnArray(res2);
        if (res2.isEmpty())
            return new ArrayList();
        
        //revisar Set
        ArrayList res3 = set(lineaActual,index2+index1);
      
        if (res3.isEmpty())
            return new ArrayList();
        int index3 = returnArray(res3);
        
       
        
        
        return res3;
    }
   
    public ArrayList set(int lineaActual,int lastIndex){
        int index = 0;
        
        String ret ="";
        //Set = BasicSet
        ArrayList basic = basicSet(lineaActual,lastIndex);
        
        if (basic.isEmpty())
            return new ArrayList();
        
        index=(int)basic.get(0);
        ret += (String)basic.get(1);
        lastIndex += index;
        while(true){
            ArrayList bl = checkAutomata(this.plusOrMinus_,lineaActual,lastIndex);
            if (bl.isEmpty()){
                break;
                }
            lastIndex += (int)bl.get(0);
            ArrayList b = basicSet(lineaActual,lastIndex);
            if (b.isEmpty())
                break;
            lastIndex += (int)b.get(0);
           
            index = index + (int)bl.get(0)+(int)b.get(0);
            ret = ret + (String)bl.get(1)+ (String)b.get(1);
            
            
        }
        
        ArrayList fin = new ArrayList();
        fin.add(lastIndex);
        fin.add(ret);
        if (ret.equals(""))
            fin=basic;
        return fin;
    }
    
    public ArrayList<String> basicSet(int lineaActual,int lastIndex){
        ArrayList<String> cadenas = new ArrayList();
      
       
        ArrayList res = checkAutomata(this.basicSet_,lineaActual,lastIndex);
       if (!res.isEmpty()){
            cadenas.add((String)res.get(1));

        }
        
       
        
      
        if (cadenas.isEmpty()){
        ArrayList res3 = basicChar(lineaActual);
            if (!res3.isEmpty())
                cadenas.add((String)res3.get(1));
        }
        
      Collections.sort(cadenas, (String o1, String o2) -> {
          Integer a1 = o1.length();
          Integer a2 = o2.length();
          return a2-a1;
        });
        
        ArrayList fin = new ArrayList();
        
        if (!cadenas.isEmpty()){
           
            fin.add(cadenas.get(0).length());
            fin.add(cadenas.get(0));
        }
       
        return fin;
        
    }
    
    public ArrayList<String> basicChar(int lineaActual){
        ArrayList res = Char(lineaActual);
        if (res.isEmpty())
            return new ArrayList();
        ArrayList res2 = checkExpression("\\.\\.",lineaActual,0);
        if (res2.isEmpty())
            return new ArrayList();
        ArrayList res3 = Char(lineaActual);
        
        if (res3.isEmpty())
            return new ArrayList();
        ArrayList result = new ArrayList();
        result.add((int)res.get(0)+(int)res2.get(0)+(int)res3.get(0));
        result.add((String)res.get(1)+(String)res2.get(1)+(String)res3.get(1));
        return result;
                    
    }
    
    
    public ArrayList<String> Char(int lineaActual){
        
        ArrayList res = checkExpression(this.character,lineaActual,0);
        if (!res.isEmpty()){
           
            return res;
        }
        ArrayList res2 = checkExpression("CHR\\("+this.number+"\\)",lineaActual,0);   
        if (!res2.isEmpty()){
            
            return res2;
        }
        return new ArrayList();
    }
    
    public void getOutput(){
        if (output){
            System.out.println("Archivo Aceptado");
        }
        else{
            System.out.println("Archivo no aceptado");
        }
    }
    
    
    
    public int returnArray(ArrayList param){
        if (!param.isEmpty()){
            //System.out.println(param.get(1));
            return (int)param.get(0);
        }
        //el cero representa que no se corta la cadena
        return 0;
    }
    
    public ArrayList checkAutomata(automata param,int lineaActual, int index){
        String cadena_encontrada="";
        String cadena_revisar = this.cadena.get(lineaActual).substring(index);
        
        int preIndex = 0;
        try{
           
           
            while (cadena_revisar.startsWith(" ")){
                preIndex++;
                cadena_revisar = cadena_revisar.substring(preIndex, cadena_revisar.length());
            }
            if (cadena_revisar.indexOf(" ")!=-1)
                cadena_revisar = cadena_revisar.substring(0, cadena_revisar.indexOf(" ")+1);
        }catch(Exception e){}
        try{
            cadena_revisar = cadena_revisar.substring(0, cadena_revisar.indexOf("."));
        }catch(Exception e){}
        
        ArrayList resultado = new ArrayList();
        
        boolean returnValue=sim.simular(cadena_revisar.trim(), param);
      
        checkIndividualAutomata(param,cadena_revisar);
        if (returnValue){
            resultado.add(cadena_revisar.length()+preIndex);
            resultado.add(cadena_revisar);
            return resultado;
        }
        else{
            if (!cadena_revisar.isEmpty())//si 
                System.out.println("Error en la linea " + lineaActual + ": la cadena " + cadena_revisar + " es inválida");
            
            
        }
        
        
        return resultado;
        
    }
    
    public void vocabulario(){
        RegexConverter convert = new RegexConverter();
        
        String regex = convert.infixToPostfix("[a-z]");
        alg_afn ThomsonAlgorithim = new alg_afn(regex);
        ThomsonAlgorithim.construct();
        letter_ = ThomsonAlgorithim.getAfn();
        
        regex = convert.infixToPostfix("[A-Z]");
        ThomsonAlgorithim = new alg_afn(regex);
        ThomsonAlgorithim.construct();
        automata letterMayuscula_ = ThomsonAlgorithim.getAfn();
        letter_ =  ThomsonAlgorithim.union(letter_, letterMayuscula_);
        regex = convert.infixToPostfix("\\|\"|\'");
        ThomsonAlgorithim = new alg_afn(regex);
        ThomsonAlgorithim.construct();
        automata specialChars = ThomsonAlgorithim.getAfn();
        letter_ = ThomsonAlgorithim.union(letter_, specialChars);
       
       
        letter_.setTipo("Letra");
        
        regex = convert.infixToPostfix("("+" "+")*");
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        espacio_  = ThomsonAlgorithim.getAfn();
        espacio_.setTipo("espacio");
        
        //System.out.println(letter_);
        regex = convert.infixToPostfix("[0-9]");
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        digit_ = ThomsonAlgorithim.getAfn();
       // digit_ = ThomsonAlgorithim.concatenacion(digit_, espacio_);
        //digit_ = ThomsonAlgorithim.concatenacion(espacio_, digit_);
        digit_.setTipo("digit");
        
        
       
        automata digitKleene = ThomsonAlgorithim.cerraduraKleene(digit_);
        //System.out.println(numberKleene);
        number_ = ThomsonAlgorithim.concatenacion(digit_, digitKleene);
        number_.setTipo("número");
        automata letterOrDigit = ThomsonAlgorithim.union(letter_, digit_);
        //System.out.println(letterOrDigit);
        automata letterOrDigitKleene = ThomsonAlgorithim.cerraduraKleene(letterOrDigit);
       // System.out.println(letterOrDigitKleene);
        ident_ = ThomsonAlgorithim.concatenacion(letter_, letterOrDigitKleene);
        ident_.setTipo("identificador");
       // System.out.println(ident_);
        automata ap1 = ThomsonAlgorithim.afnSimple("\"");
        automata ap2 = ThomsonAlgorithim.afnSimple("\"");
        automata stringKleene = ThomsonAlgorithim.union(number_, letter_);
        string_ = ThomsonAlgorithim.cerraduraKleene(stringKleene);
        string_ = ThomsonAlgorithim.concatenacion(ap1, string_);
        string_ = ThomsonAlgorithim.concatenacion(string_,ap2);
        string_.setTipo("string");
      
         
        
        
        automata apch1 = ThomsonAlgorithim.afnSimple("\'");
        automata apch2 = ThomsonAlgorithim.afnSimple("\'");
        automata chKleene = ThomsonAlgorithim.union(number_, letter_);
        character_ = ThomsonAlgorithim.cerraduraKleene(chKleene);
        character_ = ThomsonAlgorithim.concatenacion(ap1, character_);
        character_ = ThomsonAlgorithim.concatenacion(character_,ap2);
        character_.setTipo("character");
        basicSet_ = ThomsonAlgorithim.union(string_, ident_);
        basicSet_.setTipo("Basic Set");
        
        regex = convert.infixToPostfix(" "+"="+" ");
        ThomsonAlgorithim.setRegex(regex);
        ThomsonAlgorithim.construct();
        igual_  = ThomsonAlgorithim.getAfn();
        igual_.setTipo("=");
        
       
        automata plus = ThomsonAlgorithim.afnSimple("+");
        automata minus = ThomsonAlgorithim.afnSimple("-");
        plusOrMinus_ = ThomsonAlgorithim.union(plus, minus);
        plusOrMinus_.setTipo("(+|-)");
        
    }
    
    public void checkIndividualAutomata(automata AFN, String regex){
        ArrayList<automata> conjunto = conjuntoAutomatas();
        ArrayList<Boolean> resultado = new ArrayList();
        for (int i = 0;i<regex.length();i++){
            Character ch = regex.charAt(i);
            for (int j = 0;j<conjunto.size();j++){
                resultado.add(sim.simular(ch.toString(), conjunto.get(j)));
               
            }
           
            ArrayList<Integer> posiciones = checkBoolean(resultado);
            resultado.clear();
            
           
            for (int k = 0;k<posiciones.size();k++){
                
                System.out.println(ch.toString()+ ": " + conjunto.get(posiciones.get(k)).getTipo());
            }
            if (posiciones.isEmpty()){
               System.out.println(ch.toString()+ " no fue reconocido");
            }
        }
    }
    
    public ArrayList<Integer>  checkBoolean(ArrayList<Boolean> bool){
        ArrayList<Integer> posiciones = new ArrayList();
       
        for (int i = 0;i<bool.size();i++){
            if (bool.get(i))
                posiciones.add(i);
        }
        return posiciones;
        
    }
    
    public ArrayList<automata> conjuntoAutomatas(){
        ArrayList<automata> conjunto = new ArrayList();
        conjunto.add(this.letter_);
        conjunto.add(this.digit_);
        conjunto.add(this.number_);
        conjunto.add(this.ident_);
        conjunto.add(this.string_);
        conjunto.add(this.character_);
        conjunto.add(this.plusOrMinus_);
        conjunto.add(this.igual_);
        conjunto.add(this.basicSet_);
        conjunto.add(this.espacio_);
        
        return conjunto;
        
    }
}


