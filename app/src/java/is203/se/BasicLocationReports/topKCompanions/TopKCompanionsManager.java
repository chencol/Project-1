
package is203.se.BasicLocationReports.topKCompanions;

import is203.se.BasicLocationReports.locationChunk.LocationChunk;
import is203.se.BasicLocationReports.locationChunk.LocationChunkUtility;
import is203.se.DAO.DemographicDAO;
import is203.se.DAO.LocationDAO;
import is203.se.Entity.Demographic;
import is203.se.Entity.Location;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class contains the methods required to find top-K companions
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class TopKCompanionsManager {
    /**
     * Method finds the top-K companions based on a specified user, date and K
     * @param dateString time of the query, search window will be 15min before and 15min after
     * @param macAddress of the specified user, method finds companions of THIS user
     * @param kString int between 1-11
     * @return list of Companions, the top-k companions
     */
    public static ArrayList<Companion> getTopKCompanions(String dateString, String macAddress, String kString) {
        
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        
        int k = Integer.parseInt(kString);
        
        //Processing window 
        Date endWindow = null;
        Date startWindow = new Date();
        
        try {
            endWindow = sdf.parse(dateString.replace('T',' '));
            //Set startWindow to 15mins before given time
            startWindow.setTime(endWindow.getTime() - (1000 * 60 * 15));     //units in milliseconds > 1000 miliseconds = 1second
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
             
        //get all location updates of the TARGET user
        ArrayList<Location> usersLocations = LocationDAO.getAllLocationsInWindow(macAddress, sdf.format(startWindow), sdf.format(endWindow));
        //System.out.println("userLocations: " + usersLocations.size());
        
        //get target users chunks
        ArrayList<LocationChunk> targetUsersChunks = LocationChunkUtility.getLocationChunks(usersLocations, startWindow, endWindow);
        
        //get all location updates excluding the target user in the processing window
        ArrayList<Location> allOtherLocations = LocationDAO.getAllLocationsExcludingMAC(macAddress, sdf.format(startWindow), sdf.format(endWindow));
        
        //sort all other location updates by MAC-address K:macAddress v:list of user's locations
        HashMap<String, ArrayList<Location>> otherLocationMap = new HashMap<>();
        for(Location currLocation : allOtherLocations) {
            //System.out.println("check 2");
            String currMac = currLocation.getMacAddress();
            ArrayList<Location> currLocationList = otherLocationMap.get(currMac);
            if(currLocationList == null) {
                currLocationList = new ArrayList<>();
            } 
            currLocationList.add(currLocation);
            otherLocationMap.put(currMac, currLocationList);
        }
        
        //Get time spent together
        HashMap<String, Integer> result = new HashMap<>(); //k: mac address, v:seconds spent together
        //Loop all other users
        for(Map.Entry<String, ArrayList<Location>> entrySet : otherLocationMap.entrySet()) {
            //System.out.println("check 3");
            String currMacAddress = entrySet.getKey();
            ArrayList<LocationChunk> othersChunks = LocationChunkUtility.getLocationChunks(entrySet.getValue(), startWindow, endWindow);
            
            //get overlapping time
            int overlappingTime = LocationChunkUtility.countTotalOverlappingTime(targetUsersChunks, othersChunks);
            
            if(overlappingTime != 0) {
                result.put(currMacAddress, overlappingTime);
            }
        }
        
        ArrayList<Companion> companions = getTopKInList(result, k);
        return companions;
    }
    
    /**
     * Method finds the top-k companions out of a selection of available companions
     * @param result Map of K: companion MAC-Address, V: time spent with specified user
     * @param k 
     * @return top-k from the map result given
     */
    public static ArrayList<Companion> getTopKInList(HashMap<String, Integer> result, int k) {
//        System.out.println("getTokKINList: " + result.size());
        //sort companions by time spent
        List<Map.Entry<String, Integer>> list = new LinkedList<>(result.entrySet());
        Collections.sort(list, new ValueComparator());
        
        //build JSON
        ArrayList<Companion> companions = new ArrayList<>();
        
        int rank = 0;
        int prevTimeSpent = 0;
        for(Map.Entry<String,Integer> entrySet : list) {
            
            int timeSpent = entrySet.getValue();
            String currMac = entrySet.getKey();
            
            if(timeSpent != prevTimeSpent) {    //list is already sorted by time, if time is not equal, means it went down
                rank++;
            }
            prevTimeSpent = timeSpent;
            
            if(rank == k+1) {
                break;
            }
            
            //make json
            Demographic otherUser = DemographicDAO.getDemographic(currMac);
            String email = "";
            if(otherUser != null) {
                email = otherUser.getEmail();
            }

            Companion companion = new Companion(rank, currMac, timeSpent, email); 
            companions.add(companion);
        }
        return companions;
    }
}
