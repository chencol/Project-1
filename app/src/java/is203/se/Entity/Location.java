
package is203.se.Entity;

import is203.se.DAO.LocationLookupDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class/entity represents an entry found in the Location table in the database
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class Location implements Comparable<Location>{
    
    /**
     *Pattern for date string that is used by the database
     */
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private String timestamp;
    private String macAddress;
    private int locationID;
    
    /**
     * Constructor method
     * @param timestamp
     * @param macAddress
     * @param locationID 
     */
    public Location(String timestamp, String macAddress, int locationID) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.locationID = locationID;
    }
    
    /**
     * Method compares Location by timestamps
     * @param other
     * @return -1 if this timestamp is before other timestamp
     */
    @Override
    public int compareTo(Location other) {
        //if this object has a higher number, return -1
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        Date thisDate = null;
        Date otherDate = null;
        
        try {
            
            thisDate = sdf.parse(timestamp);
            otherDate = sdf.parse(other.getTimestamp());
            
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        if(thisDate.after(otherDate)) {
            return 1;
        } else if (thisDate.before(otherDate)) {
            return -1;
        } else {
            return 0;
        }
        
    }

    /**
     * Method returns the timestamp of the Location update
     * @return String timestamp in format "yyyy-MM-dd HH:mm:ss"
     */
    public String getTimestamp() {
        return timestamp;
    }
    
    /**
     * Setter method for timeStamp
     * @param timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Method returns the MAC-Address tied to the location update
     * @return String MAC Address
     */
    public String getMacAddress() {
        return macAddress;
    }
    
    /**
     * Setter method for MAC Address
     * @param macAddress 
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    /**
     * Method returns the Location ID tied to the Location update
     * @return Location ID
     */
    public int getLocationID() {
        return locationID;
    }
    
    /**
     * Setter method for locationID
     * @param locationID 
     */
    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
    
    /**
     *Method returns the semantic place the LocationID of the object refers to
     * @return String semantic place
     */
    
    public String getSemanticPlace(){
        return LocationLookupDAO.getLocationLookup(""+locationID).getSemanticPlace();
    }
    
    /**
     * Method returns the date of the Location update
     * @return Date of the Location update
     */
    public Date getDate(){
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        Date date = null;
        
        try{
            date = format.parse(timestamp);
        }catch(ParseException ex){
            Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return date;
    }
    
    /** Method overrrides object equals method, now compared by timestamp, macAddress and locationID values
     * @param obj
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object obj) {
        
        if(obj instanceof Location) {
            Location otherLocation = (Location)obj;
            
            String thisStringRepresentation = timestamp + macAddress;
            String otherStringRepresentation = otherLocation.getTimestamp() + otherLocation.getMacAddress();
            if(thisStringRepresentation.equals(otherStringRepresentation)) {
                return true;
            }
        }
        
        return false;
    }
    
    /** This method produces a hashcode based on timestamp, macAddress, and locationID
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(timestamp, macAddress);
    }
    
}
