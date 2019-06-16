
package is203.se.Validation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract class for validator objects. Contains common methods to validate mac address and blank fields
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public abstract class AbstractValidator {
    
    /**
     * This method validates a row of a CSV file represented by the row number and a HashMap String,String of key COL_NAME (found in the appropriate DAO class) and value string 
     * @param rowMap
     * @return          All row/entry error messages found
     */
    public abstract ArrayList<String> isValid(HashMap<String, String> rowMap);
           
    /**
     * This method checks if the string given is blank
     * @param value     the string to be checked
     * @return          true if valid, false if invalid
     */
    public boolean checkBlank(String value) {
        if(value == null || value.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * This method takes in a String MAC Address and checks if it follows a SHA1 patter "[a-fA-F0-9]{40}$"
     * @param macAddress    the MAC Address to be checked
     * @return              true if the row is valid; false if it is not
     */
    public boolean checkMacAddress(String macAddress) {
        String sha1Pattern = "[a-fA-F0-9]{40}$";
        // [a-fA-F0-9] matches 1 char a-f, A-F or 0-9
        // {40} //matches the previous 40 times
        //^ start of string
        //$ end of string
        
        if(macAddress.matches(sha1Pattern)) {
            return true;
        } else {
            return false;
        }
    }
    
}
