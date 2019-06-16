
package is203.se.Validation;

import is203.se.DAO.LocationLookupDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * The class contains methods to validate a LocationLookup entry/row and its columns 
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationLookupValidator extends AbstractValidator{
    
    /**
     * This method validates a row of a CSV file represented by the row number and a HashMap of String,String of key COL_NAME (found in the appropriate DAO class) and value string
     * <p>
     * Checks if rows are not blank and contain valid Location ID and Semantic Place values
     * @param rowMap
     * @return          List of all row/entry error messages found
     */
    public ArrayList<String> isValid(HashMap<String, String> rowMap){
        
        //rowErrorMsgs
        ArrayList<String> rowErrorMsgs = new ArrayList<>();
        
        //Validate all cols
        //check if for blank cols -> follow csv headers left to right
        for(String colName : LocationLookupDAO.CSV_COL_ORDER) {
            if(!checkBlank(rowMap.get(colName))) {
                rowErrorMsgs.add(colName.replace("_", " ") + " is blank");  
            }
        }
        
        //if any cols are blank, return error msgs. no need to further check
        if(!rowErrorMsgs.isEmpty()) {
            return rowErrorMsgs;
        }
        
        //column values
        String locationID = rowMap.get(LocationLookupDAO.LOCATION_ID_COL_NAME);
        String semanticPlace = rowMap.get(LocationLookupDAO.SEMANTIC_PLACE_COL_NAME);
        
        //if no cols are blank, check validation -> follow order given in brief. location-id > semantic-place
        if (!checkLocationID(locationID)) {
            //add to error
            rowErrorMsgs.add("invalid location id");
        }
        if (!checkSemanticPlace(semanticPlace)){
            //add to error
            rowErrorMsgs.add("invalid semantic place");
        }
        
        return rowErrorMsgs;
    }
    
    /**
     * This method checks if the given String is a valid positive integer
     * @param locationIDStr
     * @return true if string is valid, false if invalid
     */
    public boolean checkLocationID(String locationIDStr) {
        try {
            int locationID = Integer.parseInt(locationIDStr);
            
            //check if positive
            if(locationID <= 0) {
                return false;
            }
            
        } catch (NumberFormatException ex) {    //thrown when locationIDStr is not a valid integer
            return false;
        }
        
        return true;
    }
    
    /**
     * This method checks if the given String matches the regex "(SMUSISL|SMUSISB)(\\d{1})(.+)$"
     * @param semanticPlace
     * @return true if string is valid, false if invalid
     */
    public boolean checkSemanticPlace(String semanticPlace) {
        String regex = "^(SMUSISL|SMUSISB)([1-5]{1})(.+)$";
        //(SMUSISL|SMUSISB) either string matches
        // \d one number 0-9
        //(.+) any caracter 1 or more times
        //^ start of string
        //$ end of string
        
        //checks if the string given matches the regex
        return Pattern.matches(regex, semanticPlace);
    }
}
