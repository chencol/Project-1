
package is203.se.Validation;

import is203.se.DAO.DemographicDAO;
import is203.se.Entity.Demographic;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The class contains methods to validate a Demographic entry/row and its columns 
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class DemographicValidator extends AbstractValidator {
    
    /**
     * This method validates a row of a CSV file represented by the row number and a HashMap of String,String of key COL_NAME (found in the appropriate DAO class) and value string
     * <p>
     * Checks if rows are not blank and contain valid MAC Address, password, email and gender values
     * @param rowMap
     * @return          List of all row/entry error messages found
     */
    public ArrayList<String> isValid(HashMap<String, String> rowMap) {
        
        //rowErrorMsgs
        ArrayList<String> rowErrorMsgs = new ArrayList<>();
        
        //Validate all cols
        //check if for blank cols -> follow csv headers left to right
        for(String colName : DemographicDAO.CSV_COL_ORDER) {
            if(!checkBlank(rowMap.get(colName))) {
                rowErrorMsgs.add(colName.replace("_", " ") + " is blank");  
            }
        }
        
        //if any cols are blank, return error msgs. no need to further check
        if(!rowErrorMsgs.isEmpty()) {
            return rowErrorMsgs;
        }

        //column values
        String macAddress = rowMap.get(DemographicDAO.MAC_ADDRESS_COL_NAME);
        String name = rowMap.get(DemographicDAO.NAME_COL_NAME);
        String password = rowMap.get(DemographicDAO.PASSWORD_COL_NAME);
        String email = rowMap.get(DemographicDAO.EMAIL_COL_NAME);
        String gender = rowMap.get(DemographicDAO.GENDER_COL_NAME);
        
        if (!checkMacAddress(macAddress)) {
            rowErrorMsgs.add("invalid mac address");
        }
        if (!checkPassword(password)) {
            rowErrorMsgs.add("invalid password");
        }
        if (!checkEmail(email)) {
            rowErrorMsgs.add("invalid email");
        }
        if (!checkGender(gender)) {
            rowErrorMsgs.add("invalid gender");
        }
        
        return rowErrorMsgs;
    }
    
    /**
     * Method checks if a String is a valid password. Valid if it has no white spaces and has more than or equals to 8 characters
     * @param password
     * @return true if valid; false if not
     */
    public boolean checkPassword(String password) {
        return !(password.contains(" ") || password.length() < 8);
    }
    
    /**
     * Method checks if a String is a valid email
     * @param email
     * @return true if valid; false if not
     */
    public boolean checkEmail(String email) {
        String pattern = "^([a-zA-Z0-9]+(\\.){1})+[0-9]{4}(@){1}(business|accountancy|sis|economics|law|socsc){1}(\\.smu\\.edu\\.sg){1}$";
        //^ start of string
        //$ end of string
        //[a-zA-Z.] matchs a-z, A-Z, or '.'
        // + one or more times
        
        if(!email.matches(pattern)) {
            return false;
        }
        
        //beena.pillay.2013@economics.smu.edu.sg
        boolean failed = false;
        String[] atSplit = email.split("@");
        
        String[] dotSplit = atSplit[0].split("\\.");
        
        String yearStr = dotSplit[dotSplit.length - 1];     //2013
        
        int year = Integer.parseInt(yearStr);
        
        if(year < 2013) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Method checks if the String is a valid gender based on values found in Demographic.GENDER-FEMALE and Demographic.GENDER_MALE
     * @param gender
     * @return  true if valid; false if not
     */
    public boolean checkGender(String gender) {
        //if string is 1 char long, and matches either gender values predefined in Demographic field
        if (gender.length() == 1 && (gender.toUpperCase().charAt(0) == Demographic.GENDER_FEMALE || gender.toUpperCase().charAt(0) == Demographic.GENDER_MALE)) {
            return true;
        } else {
            return false;
        }
    }
}
