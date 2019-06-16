
package is203.se.Entity;

/**
 * This class/entity represents an entry found in the Location Lookup table in the database
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationLookup {
    
    private int locationID;
    private String semanticPlace;
    
    /**
     * Constructor for the entity
     * @param locationID
     * @param semanticPlace 
     */
    public LocationLookup(int locationID, String semanticPlace) {
        this.locationID = locationID;
        this.semanticPlace = semanticPlace;
    }
    
    /**
     * 
     * @return returns int that represents the Location ID
     */
    public int getLocationID() {
        return locationID;
    }
    
    /**
     * Sets location ID
     * @param locationID 
     */
    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
    
    /**
     * 
     * @return returns String semantic place
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }
    
    /**
     * Sets semantic place
     * @param semanticPlace 
     */
    public void setSemanticPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }
    
}
