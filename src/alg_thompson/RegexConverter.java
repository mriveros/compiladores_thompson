/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/


package alg_thompson;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class RegexConverter {
    
    /** Mapa de precedencia de los operadores. */
	private final Map<Character, Integer> precedenciaOperadores;
        
        //constructor
	public RegexConverter()
        {
		Map<Character, Integer> map = new HashMap<>();
		map.put('(', 1); // parentesis
		map.put('|', 2); // Union o or
		map.put('.', 3); // explicit concatenation operator
		map.put('?', 4); // | €
		map.put('*', 4); // kleene
		map.put('+', 4); // positivo
		precedenciaOperadores = Collections.unmodifiableMap(map);
              
	};
       
	private Integer getPrecedencia(Character c) {
		Integer precedencia = precedenciaOperadores.get(c);
                //si obtiene un valor nulo retrona 6 por default
		return precedencia == null ? 6 : precedencia;
	}

      
        private String insertCharAt(String s, int pos, Object ch){
            return s.substring(0,pos)+ch+s.substring(pos+1);
        }
        
        private String appendCharAt(String s, int pos, Object ch){
            String val = s.substring(pos,pos+1);
            return s.substring(0,pos)+val+ch+s.substring(pos+1);
            
        }
        
       
        public String abreviaturaInterrogacion(String regex)
        {   
            for (int i = 0; i<regex.length();i++){
                Character ch = regex.charAt(i);
                 
                if (ch.equals('?'))
                {
                    if (regex.charAt(i-1) == ')')
                    {
                        regex = insertCharAt(regex,i,"|"+Main.EPSILON+")");
                        
                        int j =i;
                        while (j!=0)
                        {
                            if (regex.charAt(j)=='(')
                            {
                                break;
                            }
                            
                        j--;
                        
                        }
                        
                        regex=appendCharAt(regex,j,"(");
                         
                    }
                    else
                    {
                        regex = insertCharAt(regex,i,"|"+Main.EPSILON+")");
                        regex = insertCharAt(regex,i-1,"("+regex.charAt(i-1));
                    }
                }
            }
            regex = balancearParentesis(regex);
            return regex;
        }
        
       
        private int parentesisIzq (String regex){
            int P1=0;
            for (int i = 0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch.equals('(')){
                    P1++;
                }
              
            }
            return P1;
        }
        
        private int parentesisDer (String regex){
            int P1=0;
             for (int i = 0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch.equals(')')){
                    P1++;
                }
            }
            return P1;
        }
       
        private String balancearParentesis(String regex){
            //corregir parentesis de la expresion en caso que no esten balanceados
            int P1 = parentesisIzq(regex);
            int P2 = parentesisDer(regex);
            
            
            while(P1 != P2){
                if (P1>P2)
                    regex +=")";
                if (P2>P1)
                    regex ="(" + regex;
                P1 = parentesisIzq(regex);
                P2 = parentesisDer(regex);
            }
            return regex;
        }
        
       
        public String abreviaturaCerraduraPositiva(String regex){
            //sirve para buscar el '(' correcto cuando  hay () en medio
            // de la cerradura positiva
            int compare = 0; 
            
            for (int i = 0; i<regex.length();i++){
                 Character ch = regex.charAt(i);
                 
                if (ch.equals('+'))
                {
                    //si hay un ')' antes de un operador
                    //significa que hay que buscar el '(' correspondiente
                    if (regex.charAt(i-1) == ')'){
                        
                        int fixPosicion = i;
                        
                        while (fixPosicion != -1)
                        {
                            if (regex.charAt(fixPosicion)==')')
                            {
                               compare++;
                               
                            }
                            
                            if (regex.charAt(fixPosicion)=='(')
                            {
                                
                                compare--;
                                if (compare ==0)
                                    break;
                            }
                            
                            
                        fixPosicion--;
                        
                        }
                      
                        String regexAb = regex.substring(fixPosicion,i);
                        regex = insertCharAt(regex,i,regexAb+"*");
                        
                      
                    }
                    //si no hay parentesis, simplemente se inserta el caracter
                    else
                    {
                        regex = insertCharAt(regex,i,regex.charAt(i-1)+"*");
                    }
                    
                   
                }
                
            }
           
            regex = balancearParentesis(regex);
            
            return regex;
        }
	
	public  String formatRegEx(String regex) {
                regex = regex.trim();
                regex = abreviaturaInterrogacion(regex);
                regex = abreviaturaCerraduraPositiva(regex);
		String  regexExplicit = new String();
		List<Character> operadores = Arrays.asList('|', '?', '+', '*');
		List<Character> operadoresBinarios = Arrays.asList('|');
                
                
                //recorrer la cadena
		for (int i = 0; i < regex.length(); i++)
                {
                    Character c1 = regex.charAt(i);
                   
                    if (i + 1 < regex.length()) 
                    {
                        
                        Character c2 = regex.charAt(i + 1);
                        
                        regexExplicit += c1;
                        
                        //mientras la cadena no incluya operadores definidos, será una concatenación implicita
                        if (!c1.equals('(') && !c2.equals(')') && !operadores.contains(c2) && !operadoresBinarios.contains(c1))
                        {
                            regexExplicit += '.';
                           
                        }
                        
                    }
		}
		regexExplicit += regex.charAt(regex.length() - 1);
                

		return regexExplicit;
	}
        
        public String abreviacionOr(String regex){
            String resultado = new String();
            try{        
            for (int i=0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch =='[' ){
                    if (regex.charAt(i+2)=='-'){
                        int inicio = regex.charAt(i+1);
                        int fin = regex.charAt(i+3);
                        resultado +="(";
                        for (int j = 0;j<=fin-inicio;j++)
                        {
                            if (j==(fin-inicio))
                                resultado+= Character.toString((char)(inicio+j));
                            else
                             resultado+= Character.toString((char)(inicio+j))+'|';
                        }
                        resultado +=")";
                        i=i+4;
                    }
                    else{
                        resultado +=ch;
                    }
                }
                else{
                    resultado+=ch;
                }
                
            }
            } catch (Exception e){
                System.out.println("Error en la conversión " + regex);
                resultado = " ";
            }
            
            return resultado;
        }
        
          public String abreviacionAnd(String regex){
            String resultado = new String();
           try{         
            for (int i=0;i<regex.length();i++){
                Character ch = regex.charAt(i);
                if (ch =='[' ){
                    if (regex.charAt(i+2)=='.'){
                        int inicio = regex.charAt(i+1);
                        int fin = regex.charAt(i+3);
                        resultado +="(";
                        for (int j = 0;j<=fin-inicio;j++)
                        {
                           
                            resultado+= Character.toString((char)(inicio+j));
                        }
                        resultado +=")";
                        i=i+4;
                    }
                }
                else{
                    resultado+=ch;
                }
                //System.out.println(resultado);
            }
           }catch (Exception e){
               System.out.println("Error en la conversion "+regex);
               resultado = "(a|b)*abb";
           }
            return resultado;
        }
        
        
       
	public  String infixToPostfix(String regex) {
		String postfix = new String();
                regex = abreviacionOr(regex);
                regex = abreviacionAnd(regex);
		Stack<Character> stack = new Stack<>();

		String formattedRegEx = formatRegEx(regex);
                //System.out.println(formattedRegEx);
		for (Character c : formattedRegEx.toCharArray()) {
			switch (c) {
				case '(':
					stack.push(c);
					break;

				case ')':
					while (!stack.peek().equals('(')) {
						postfix += stack.pop();
					}
					stack.pop();
					break;

				default:
					while (stack.size() > 0) 
                                        {
						Character peekedChar = stack.peek();

						Integer peekedCharPrecedence = getPrecedencia(peekedChar);
						Integer currentCharPrecedence = getPrecedencia(c);

						if (peekedCharPrecedence >= currentCharPrecedence) 
                                                {
							postfix += stack.pop();
                                                       
						} 
                                                else 
                                                {
							break;
						}
					}
					stack.push(c);
					break;
			}

		}

		while (stack.size() > 0)
			postfix += stack.pop();

		return postfix;
	}

}
