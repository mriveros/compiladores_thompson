/**
* Trabajo Practico de Compiladores
* Marcos Riveros y Sergio Orue
* Marzo 2018
*/

package alg_thompson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JFileChooser;

/**
 *
 * @author mriveros
 */
public class ReadFile {
    
     public HashMap leerArchivo(){
      
        int contador=0;
        int tamaño=0;
        String input="";
        BufferedReader br = null;
 
        try {

                File file = new File("alfabeto.txt");
                br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                String sCurrentLine;
               
               int cantidadLineas=1;
             
               HashMap<Integer,String> detailString = new HashMap();
               while ((sCurrentLine = br.readLine()) != null) {
                   
                    
                    detailString.put(cantidadLineas, sCurrentLine);
                    
                    if (!sCurrentLine.equals("")){
                        input+=sCurrentLine+"\r\n";
                        cantidadLineas++;
                    }
                
                }
             
                
          
        return detailString;
        } catch (IOException e) {
               
        } finally {
                try {
                        if (br != null)br.close();
                } catch (IOException ex) {
                        ex.printStackTrace();
                }
        }
        return null;
        
    }

}
