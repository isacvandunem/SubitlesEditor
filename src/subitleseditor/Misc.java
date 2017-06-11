/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subitleseditor;

/**
 *
 * @author isacv
 */
public class Misc {
    
    /**
     * Plurifies a simple text in which the plural form takes only an extra 's', depending on the count.
     * @param count Number of elements
     * @param singularForm singular form of the word to be plurified
     * @return The plural version if the count is greater than 1 or the singular form otherwise
     */
    public static String plurify(int count, String singularForm){
        if (Math.abs(count) == 1){
            return "" + count + " " + singularForm;
        }
        else {
            return "" + count + " " + singularForm + "s";
        }
    }
}
