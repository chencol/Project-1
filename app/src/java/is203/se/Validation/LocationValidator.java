
package is203.se.Validation;

import is203.se.DAO.LocationDAO;
import is203.se.DAO.LocationLookupDAO;
import is203.se.Entity.Location;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The class contains methods to validate a Location entry/row and its columns 
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationValidator extends AbstractValidator {
    
    private HashSet<String> allLocationIDs = LocationLookupDAO.getAllLocationIDs();
    
    /**
     * This method validates a row of a CSV file represented by the row number and a HashMap of String,String of key COL_NAME (found in the appropriate DAO class) and value string
     * <p>
     * Checks if rows are not blank and contain valid Location ID, MAC Address, timestamp values
     * @param rowMap
     * @return          List of all row/entry error messages found
     */
    @Override
    public ArrayList<String> isValid(HashMap<String, String> rowMap) {
        
        //rowErrorMsgs
        ArrayList<String> rowErrorMsgs = new ArrayList<>();
        
        //Validate all cols
        //check if for blank cols -> follow csv headers left to right
        for(String colName : LocationDAO.CSV_COL_ORDER) {
            if(!checkBlank(rowMap.get(colName))) {
                rowErrorMsgs.add(colName.replace("_", " ") + " is blank");  
            }
        }

        //if any cols are blank, return error msgs. no need to further check
        if(!rowErrorMsgs.isEmpty()) {
            return rowErrorMsgs;
        }
        
        //column values
        String timestamp = rowMap.get(LocationDAO.TIMESTAMP_COL_NAME);
        String macAddress = rowMap.get(LocationDAO.MACADDRESS_COL_NAME);
        String locationID = rowMap.get(LocationDAO.LOCATION_ID_COL_NAME);
        
        //following error messages follow the order given in the course brief wiki
        if(!checkLocationID(locationID)) {
            rowErrorMsgs.add("invalid location");
        }
        if(!checkMacAddress(macAddress)) {
            rowErrorMsgs.add("invalid mac address");
        }
        if(!checkTimestamp(timestamp)) {
            rowErrorMsgs.add("invalid timestamp");
        }

        return rowErrorMsgs;
    }
    
    /**
     * Method checks if the given String matches the date format found in Location.DATE_FORMAT_PATTERN
     * @param timestamp
     * @return  true if valid; false if not
     */
    public boolean checkTimestamp(String timestamp) {
        String regex = "(\\d){4}-(\\d){2}-(\\d){2} (\\d){2}:(\\d){2}:(\\d){2}";
        if(!timestamp.matches(regex)) {
            return false;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        sdf.setLenient(false);
        try {
            sdf.parse(timestamp);       //try to convert dateString based on PATTERN
        } catch (ParseException ex) {   //if conversion fails, return false
            return false;
        }
        return true;                    //successful conversion
    }
    
    /**
     * Method checks if the locationID is valid and can be found in the LocationLookup DB table 
     * @param locationID
     * @return              true if valid; false if not
     */
    //checks if locationID is found in LocationLookup table
    public boolean checkLocationID(String locationID) {
        return allLocationIDs.contains(locationID);
    }
    
    /**
     * Method checks if the row has a duplicate entry in the database, based on primary key of timestamp and MAC Address 
     * @param timeStamp
     * @param macAddress
     * @return              true if valid; false if not
     */
    public boolean checkDuplicate(String timeStamp, String macAddress) {
        Location loc = LocationDAO.getLocation(timeStamp, macAddress);
        
        if(loc != null) {
            //System.out.println("dupe found");
            return false;
        } else {
            return true;
        }
    }
    
}
