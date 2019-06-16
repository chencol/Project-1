/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.locationChunk;

import is203.se.DAO.LocationLookupDAO;
import java.util.Date;
/**
 * Class represents a users stay at a specific LocationID for a period of time
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationChunk {
    
    String macAddress;
    int locationID;
    Date startWindow;
    Date endWindow;
    
    /**
     * Constructor method
     * @param macAddress of the user
     * @param locationID where the user stays
     * @param startWindow when the user begins their stay (inclusive)
     * @param endWindow when the user ends their stay (exclusive)
     */
    
    public LocationChunk(String macAddress, int locationID, Date startWindow, Date endWindow) {
        this.macAddress = macAddress;
        this.locationID = locationID;
        this.startWindow = startWindow;
        this.endWindow = endWindow;
    }
    
    /**
     * Returns the MAC-Address tied to this LocationChunk;
     * @return String MAC-Address
     */
    public String getMacAddress() {
        return macAddress;
    }
    
    /**
     * Returns locatonID tied to this LocationChunk
     * @return int locationID
     */
    public int getLocationID() {
        return locationID;
    }
    
    /**
     * Returns the semanticPlace the LocationChunk is tied to
     * @return String, semanticPlace tied to the locationID in the LocationChunk object, or "outsideSIS" if user is outside SIS with locationID = -1.
     */
    public String getSemanticPlace() {
        if(locationID==-1) {
            return "outsideSIS";
        } else {
            return LocationLookupDAO.getLocationLookup(""+locationID).getSemanticPlace();
        }
    }
    
    /**
     * Returns the time the LocationChunk starts at
     * @return Date startWindow
     */
    public Date getStartWindow() {
        return startWindow;
    }
    
    /**
     * Returns the time the LocationChunk ends at
     * @return Date endWindow
     */
    public Date getEndWindow() {
        return endWindow;
    }
    
    /**
     * Method finds the duration of the chunk
     * @return long, number of seconds the chunk represents
     */
    public long getDuration() {
        return (endWindow.getTime() - startWindow.getTime()) / 1000 ;
    }

    /**
     * Method counts the number of overlapping seconds between two LocationChunks
     * @param other other LocationChunk
     * @return long, number of seconds overlapping
     */
    public long getOverlappingSeconds(LocationChunk other) {
        
        LocationChunk commonChunk = LocationChunkUtility.getCommonLocationChunk(this, other);
        if(commonChunk != null) {
            return (commonChunk.getEndWindow().getTime() - commonChunk.getStartWindow().getTime()) / 1000;
        } else {
            return 0;
        }
        
//        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
//        
//        Date thisStartWindow = startWindow;
//        Date thisEndWindow = endWindow;
//        
//        Date otherStartWindow = other.getStartWindow();
//        Date otherEndWindow = other.getEndWindow();

////        System.out.println("thisStart: " + sdf.format(thisStartWindow));
////        System.out.println("thisEnd: : " + sdf.format(thisEndWindow));
////        System.out.println("otherStart: " + sdf.format(otherStartWindow));
////        System.out.println("otherEnd: " + sdf.format(otherEndWindow));
//        
//        //other chunk IN this chunk
//        //  |------------|          |-----------|       |---------|
//        //      |----|              |--------|              |-----|
//        if((otherStartWindow.after(thisStartWindow) || otherStartWindow.equals(thisStartWindow))&& (otherEndWindow.before(thisEndWindow) || otherEndWindow.equals(thisStartWindow))) {
//            System.out.println("S1");
//            return (otherEndWindow.getTime() - otherStartWindow.getTime()) / 1000;    
//        
//        //other chunk starts before and ends inside this chunk
//        //      |------------|        |---------|
//        //  |--------|            |-------------|
//        } else if (otherStartWindow.before(thisStartWindow) && ((otherEndWindow.after(thisStartWindow) && otherEndWindow.before(thisEndWindow)) || otherEndWindow.equals(thisEndWindow))) {
//            System.out.println("S2");
//            return (otherEndWindow.getTime() - thisStartWindow.getTime()) / 1000;
//        
//        //other chunk starts inside this chunk and ends after this chunk
//        //  |-----------|           |---------------|
//        //      |------------|      |-------------------|
//        } else if (((otherStartWindow.after(thisStartWindow) && otherStartWindow.before(thisEndWindow)) || otherStartWindow.equals(thisStartWindow) )&& otherEndWindow.after(thisEndWindow)) {    
//            System.out.println("S3");
//            return (thisEndWindow.getTime() - otherStartWindow.getTime()) / 1000;            
//            
//        //this chunk is INSIDE other chunk    
//        //      |----|
//        //  |-------------|
//        } else if (otherStartWindow.before(thisStartWindow) && otherEndWindow.after(thisEndWindow)) {
//            System.out.println("S4");
//            return (thisEndWindow.getTime() - thisStartWindow.getTime()) / 1000;
//        
//        //exact overlap
//        //  |-------------|
//        //  |-------------|
//        } else if (otherStartWindow.getTime() == thisStartWindow.getTime() && otherEndWindow.getTime() == thisEndWindow.getTime()) {
//            System.out.println("S5");
//            return (thisEndWindow.getTime() - thisStartWindow.getTime()) / 1000;
//            
//        //no overlap at all
//        //  |-------------|
//        //                    |-------------|
//        } else {    
//            System.out.println("S6");
//            return 0;
//        }
    }
}
