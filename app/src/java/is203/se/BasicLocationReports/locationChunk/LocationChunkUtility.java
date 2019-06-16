
package is203.se.BasicLocationReports.locationChunk;

import is203.se.Entity.Location;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
/**
 * Class contains utility methods for the LocationChunk class
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationChunkUtility {
    
    /**
     * Method takes in a list of Location objects and returns a list of LocationChunks that represent a period of time a user spends at a location
     * @param locations list of Location objects to be processed
     * @param startWindow Date start time of the search period the list of locations where found in
     * @param endWindow Date end time of the search period the list of locations were found in
     * @return list of LocationChunks sorted by increasing time
     */
    public static ArrayList<LocationChunk> getLocationChunks(ArrayList<Location> locations, Date startWindow, Date endWindow) {
        //System.out.println("getLocationChunks locations size: " + locations.size());
        //list to return
        ArrayList<LocationChunk> retList = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        
        //sort locations by time increasing
        Collections.sort(locations);
        
        //add placeholder for the endWindow
        Location endWindowPlaceholder = new Location(sdf.format(endWindow), "placeholderMAC", -1);
        locations.add(endWindowPlaceholder);
        
        String macAddress = null;
        
        //loop all locations and create chunks 
        Location prevLocation = null;
        for(Location currLocation : locations) {
            
            //set mac once
            if(macAddress == null) {
                macAddress = currLocation.getMacAddress();
            }
            
            Date currTimestamp = null;
            Date prevTimestamp = null;
            
            //set currTImestamp
            try {
                currTimestamp = sdf.parse(currLocation.getTimestamp());
            } catch (ParseException ex) {
                //System.out.println("Fail parse HERE");
                ex.printStackTrace();
            }
            
            //IF first update is AFTER the start window, not ON the start window
            if(prevLocation == null) {  
                //user is outside of SIS until first update
                //System.out.print("first and after: ");
                //System.out.println("Chunk made: " + macAddress + " " + -1 + " " + sdf.format(startWindow) + " | " + sdf.format(currTimestamp));
                LocationChunk chunk = new LocationChunk(macAddress, -1, startWindow, currTimestamp);
                retList.add(chunk);
                prevLocation = currLocation;
                //System.out.print("zz0: ");
                //System.out.println("Chunk made: " + macAddress + " " + prevLocation.getLocationID() + " " + sdf.format(startWindow) + " | " + sdf.format(currTimestamp));
                continue;
            }
     
            //set prevTimestamp
            try {
                prevTimestamp = sdf.parse(prevLocation.getTimestamp());
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            
            //IF diff between prev and curr timestamp is less than 5 mins
            if(currTimestamp.getTime() - prevTimestamp.getTime() <= (1000 * 60 * 5)) {  
                //User is located at previous location till currTimestamp
                //System.out.print("zz1: ");
                //System.out.println("Chunk made: " + macAddress + " " + prevLocation.getLocationID() + " " + sdf.format(prevTimestamp) + " | " + sdf.format(currTimestamp));
                LocationChunk chunk = new LocationChunk(macAddress, prevLocation.getLocationID(), prevTimestamp, currTimestamp);
                retList.add(chunk);
                prevLocation = currLocation;
                continue;
            }
            
            //IF diff between prev and this timestamp is more than 5 mins
            if(currTimestamp.getTime() - prevTimestamp.getTime() > (1000 * 60 * 5)) {   
                //User is at previous location for 5mins
                //System.out.print("zz2: ");
                Date plusFiveMins = new Date();
                plusFiveMins.setTime(prevTimestamp.getTime() + (1000 * 60 * 5));
                //System.out.println("Chunk made: " + macAddress + " " + prevLocation.getLocationID() + " " + sdf.format(prevTimestamp) + " | " + sdf.format(plusFiveMins));
                LocationChunk chunk = new LocationChunk(macAddress, prevLocation.getLocationID(), prevTimestamp, plusFiveMins);
                retList.add(chunk);
                
                //User is outside SIS for the remaining time
                //System.out.print("zz3: ");
                //System.out.println("Chunk made: " + macAddress + " " + -1 + " " + sdf.format(plusFiveMins) + " | " + sdf.format(currTimestamp));
                chunk = new LocationChunk(macAddress, -1, plusFiveMins, currTimestamp);
                retList.add(chunk);
                prevLocation = currLocation;
                continue;
            }
        }
        //System.out.println("chunks made: " + retList.size());
        return retList;
    }
    
    /**
     * This method smooths a list of chunks, if 2 subsequent chunks are a the same semantic place, they will be merged into one single chunk
     * @param chunks list of LocationChunks to be smoothed
     * @return list of merged chunks sorted by increasing time
     */
    public static ArrayList<LocationChunk> smoothChunksBySemanticPlace(ArrayList<LocationChunk> chunks) {
        
        ArrayList<LocationChunk> retList = new ArrayList<>();
        
        LocationChunk prevChunk = null;
        for(LocationChunk currChunk : chunks) {
            
            if(prevChunk == null) { //first chunk in list
                prevChunk = currChunk;
            
            } else {    
                
                //get semantic places of both chunks
                String prevSemanticPlace = prevChunk.getSemanticPlace();
                String currSemanticPlace = currChunk.getSemanticPlace();
                
                Date prevEndWindow = prevChunk.getEndWindow();
                Date currStartWindow = currChunk.getStartWindow();
                
                //prev and curr chunks are at the same place, combine them
                if(prevSemanticPlace.equals(currSemanticPlace) && prevEndWindow.equals(currStartWindow)) {
                    
                    String macAddress = prevChunk.getMacAddress();
                    Date prevChunkStartTime = prevChunk.getStartWindow();
                    Date currChunkEndTime = currChunk.getEndWindow();
                    
                    int prevLocationID = prevChunk.getLocationID(); //doesnt matter which location ID I use, semantic place should be the same
                    
                    //set prev chunk as a combination of these 2 chunks
                    prevChunk = new LocationChunk(macAddress, prevLocationID,prevChunkStartTime, currChunkEndTime);
                    
                } else {    //prev and curr chunks are at different places
                    
                    //add merged prev chunks and set a new prevChunk
                    retList.add(prevChunk);
                    prevChunk = currChunk;
                }
            }
        }
        retList.add(prevChunk);
        return retList;
    }
    
     /**
     * This method smooths a list of chunks, if 2 subsequent chunks have the same LocationID, they will be merged           into a single chunk
     * @param chunks list of chunks to be smoothed
     * @return list of merged chunks sorted by increasing time
     */
    public static ArrayList<LocationChunk> smoothChunksByLocationId(ArrayList<LocationChunk> chunks){
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        
        LocationChunk prevChunk = null;
        String macAddress = null;
        
        ArrayList<LocationChunk> retList = new ArrayList<>();
        for(LocationChunk currChunk: chunks){   //loop all chunks 
            
            if(prevChunk==null){    //first chunk
                
                macAddress = currChunk.getMacAddress();
                prevChunk = currChunk;
                
            }else{
                
                int prevLocationID = prevChunk.getLocationID();
                int currLocationID = currChunk.getLocationID();
                
                Date prevEndWindow = prevChunk.getEndWindow();
                Date currStartWindow = currChunk.getStartWindow();
                
                if(prevLocationID==currLocationID && prevEndWindow.equals(currStartWindow)){ //chunks refer to the same place and are side by side
                    Date startWindow = prevChunk.getStartWindow();
                    Date endWindow = currChunk.getEndWindow();
                    prevChunk = new LocationChunk(macAddress,prevLocationID,startWindow,endWindow);
                    
                }else{  //chunks refer to different places, stop merging
                    retList.add(prevChunk);
                    prevChunk = currChunk;
                }
            }
            
        }
        retList.add(prevChunk);
        return retList;
    }
    
    /**
     * Method returns the number of seconds of companionship between two peoples LocationChunks
     * @param usersChunks
     * @param othersChunks
     * @return number of seconds of overlap between two lists of LocationChunks
     */
    public static int countTotalOverlappingTime(ArrayList<LocationChunk> usersChunks, ArrayList<LocationChunk> othersChunks) {
        
        int secondsTogether = 0;
        
        //loop target users chunks
        for(LocationChunk userChunk : usersChunks) {
            
            //loop some other guys chunks
            for(LocationChunk otherChunk : othersChunks) {
                
                if(userChunk.getLocationID() != -1 && (userChunk.getLocationID() == otherChunk.getLocationID())) {   //if loctionIDs match
                    
//                    //DEBUG
//                    System.out.println("Comparing chunks...");
//                    System.out.println("\tChunk1: " + userChunk.getLocationID() + "\t" + userChunk.getStartWindow() + "\t" + userChunk.getEndWindow());
//                    System.out.println("\tChunk2: " + otherChunk.getLocationID() + "\t" + otherChunk.getStartWindow() + "\t" + otherChunk.getEndWindow());
                    
                    secondsTogether += userChunk.getOverlappingSeconds(otherChunk);
                    
//                    //DEBUG
//                    System.out.println("\tOverlapping time: " + secondsTogether);
                }   
            }
        }
        return secondsTogether;
    }
    
    /**
     * Method returns a common LocationChunk between two LocationChunks based on the times found in both LocationChunks given.
     * @param firstChunk 
     * @param secondChunk 
     * @return  LocationChunk if common time found, null if no common time
     */
    public static LocationChunk getCommonLocationChunk(LocationChunk firstChunk, LocationChunk secondChunk) {
        
        int locationID = firstChunk.getLocationID();
        
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        
        Date thisStartWindow = firstChunk.getStartWindow();
        Date thisEndWindow = firstChunk.getEndWindow();
        
        Date otherStartWindow = secondChunk.getStartWindow();
        Date otherEndWindow = secondChunk.getEndWindow();
        
//        System.out.println("thisStart: " + sdf.format(thisStartWindow));
//        System.out.println("thisEnd: : " + sdf.format(thisEndWindow));
//        System.out.println("otherStart: " + sdf.format(otherStartWindow));
//        System.out.println("otherEnd: " + sdf.format(otherEndWindow));


        //Squash non overlap conditions
        //       |---|      |----|
        // |---|                    |----|
        if(otherEndWindow.before(thisStartWindow) || otherStartWindow.after(thisEndWindow)) {
            return null;
        }
        
        Date startCommonWindow = null;
        Date endCommonWindow = null;
        
        //determine start of common window
        //   |----------
        //      |------
        if(otherStartWindow.after(thisStartWindow)) {
            startCommonWindow = otherStartWindow;
            
        //      |-------        |-----
        //  |-----------        |-----
        } else {
            startCommonWindow = thisStartWindow;
        }
        
        //determine end of common window
        //  -------|
        //  ---|
        if(otherEndWindow.before(thisEndWindow)) {
            endCommonWindow = otherEndWindow;
            
        //  --------|           ------|
        //  -----------|        ------|
        } else {
            endCommonWindow = thisEndWindow;
        }
        
        return new LocationChunk("placeholderMAC", locationID, startCommonWindow, endCommonWindow);
                
    }

    
}
