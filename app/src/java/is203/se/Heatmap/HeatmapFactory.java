package is203.se.Heatmap;

import is203.se.DAO.LocationDAO;
import is203.se.DAO.LocationLookupDAO;
import is203.se.Entity.Location;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Class manages the creation of heatmap, contains the logic of the process
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class HeatmapFactory {

    /**
     * Method creates a heatmap of all semantic places location in the floor. The HashMap also contains all semantic places with 0 density and number of people
     * @param floor
     * @param date the timestring field
     * @return heatmap HashMap with K:Semantic Place, V: LocationDensity
     */
    protected static Map<String, LocationDensity> createHeatmap(String floor, String date) {

        //get start and end window
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        String startWindow = "";
        String endWindow = "";
        try {
            Date endWindowDate = sdf.parse(date.replace('T', ' '));
            Date startWindowDate = new Date();
            startWindowDate.setTime(endWindowDate.getTime() - 1000 * 60 * 15); //15mins = 1000 miliseconds * 60 * 15

            startWindow = sdf.format(startWindowDate);
            endWindow = sdf.format(endWindowDate);

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        //get all location updates in a window
        ArrayList<Location> allLocationUpdates = LocationDAO.getAllLocationsInWindow(startWindow, endWindow);

        //get only the latest updates for each MACAddress
        ArrayList<Location> latestLocationUpdates = extractLatestUpdates(allLocationUpdates);
        
        String floorPattern = getFloorPattern(floor);
        //assume floorPattern is not null, as request parameter validations have already been done
        
        //Count the number of Location updates in each semantic place
        HashMap<String, LocationDensity> heatmap = new HashMap<>(); // K: semanticPlace, V: LocationDensity
        for(Location currLocation : latestLocationUpdates) {
            String currSemanticPlace = currLocation.getSemanticPlace();
            
            //only count Locations that are in the floor we are looking at
            if(currSemanticPlace.contains(floorPattern)) {
                LocationDensity mapLocationDensity = heatmap.get(currSemanticPlace);
                
                if(mapLocationDensity == null) {    //first time finding this currSemanticPlace
                    heatmap.put(currSemanticPlace, new LocationDensity(currSemanticPlace, 1));
                } else {
                    mapLocationDensity.incrementNumberOfPeople();
                }
            }
        }
        
        //insert semantic places with LocationDensity that has 0 numberOfPeople
        
        //get all semanticPlaces
        ArrayList<String> allSemanticPlaces = LocationLookupDAO.getAllSemanticPlaces();
        for(String currSemanticPlace : allSemanticPlaces) {
            
            if(currSemanticPlace.contains(floorPattern)) {
                LocationDensity mapLocationDensity = heatmap.get(currSemanticPlace);
                if(mapLocationDensity == null) {    //not found
                    heatmap.put(currSemanticPlace, new LocationDensity(currSemanticPlace, 0));
                }
            }
        }
        
        //sort by semanticPlace
        LinkedHashMap<String, LocationDensity> sortedHeatmap = new LinkedHashMap<>();
        
        //sort by semantic place ascending
        ArrayList<String> semanticPlaceKeys = new ArrayList<>(heatmap.keySet());
        Collections.sort(semanticPlaceKeys);
        
        //put into linked hashmap, same as a normal hashmap but with a proper ordering
        for(String semanticPlaceKey : semanticPlaceKeys) {
            LocationDensity currKeyDensity = heatmap.get(semanticPlaceKey);
            sortedHeatmap.put(semanticPlaceKey, currKeyDensity);
        }
        return sortedHeatmap;

    }

    /**
     * Method gets a list that contains the only latest Location update for each macAddress
     * @param allLocationUpdates
     * @return list of each macAddress's latest Location update
     */
    protected static ArrayList<Location> extractLatestUpdates(ArrayList<Location> allLocationUpdates) {
        // get only latest update of each MacAddress K:macaddress V: LATEST update
        HashMap<String, Location> latestUserLocation = new HashMap<>();
        for(Location currLocation: allLocationUpdates) {
            
            String currMacAddress = currLocation.getMacAddress();
            Date currDate = currLocation.getDate();
            
            Location mapLocation = latestUserLocation.get(currMacAddress);
            
            if(mapLocation == null) {       //first time macAddress finding currMac
                
                latestUserLocation.put(currMacAddress, currLocation);
                
            } else if (currDate.after(mapLocation.getDate())) {     //curr Location is after Location found in Map
                
                latestUserLocation.put(currMacAddress, currLocation);
                
            }
        }
        return new ArrayList<>(latestUserLocation.values());
    }
    
    /**
     * Method converts a floor number between 0-5 to a floor pattern that matches semantic places by floor
     * @param floor
     * @return String floor pattern, or null if the number is not between 0 - 5
     */
    protected static String getFloorPattern(String floor) {
        int floorNum = Integer.parseInt(floor);
        
        String floorPattern = "SMUSIS";
        
        switch(floorNum) {
            case 0:
                return floorPattern + "B1";
            case 1:
                return floorPattern + "L1";
            case 2:
                return floorPattern + "L2";
            case 3:
                return floorPattern + "L3";
            case 4:
                return floorPattern + "L4";
            case 5:
                return floorPattern + "L5";
            default:
                return null;

        }
    }
}
