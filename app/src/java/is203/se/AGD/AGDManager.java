
package is203.se.AGD;

import is203.se.BasicLocationReports.locationChunk.LocationChunk;
import is203.se.BasicLocationReports.locationChunk.LocationChunkUtility;
import is203.se.BasicLocationReports.locationChunk.LocationGroup;
import is203.se.DAO.LocationDAO;
import is203.se.Entity.Demographic;
import is203.se.Entity.Location;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Class handles the logic behind detecting groups
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class AGDManager {
    
    private HashMap<String, ArrayList<LocationChunk>> macChunksMap;
    HashMap<String, TreeMap<Integer, Long>> macLocationTreeMap;
    private Date startWindow;
    private Date endWindow;
    private int totalUsers;
    private HashSet<LocationGroup> groupsFound;
    
    /**
     * Constructor method
     * @param endWindowStr time user enters, is the endWindow for the query window because we look 15mins back from this time
     * @throws ParseException when endWindowStr is invalid date format
     */
    public AGDManager(String endWindowStr) throws ParseException {
        
        //set start and end query window 
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        endWindow = sdf.parse(endWindowStr.replaceAll("T", " "));
        startWindow = new Date(endWindow.getTime() - (1000 * 60 * 15));   //endWindow - 15mins);
        String startWindowStr = sdf.format(startWindow);
        
        //DEBUG 
        /*
        System.out.println("Start Window: " + startWindowStr);
        System.out.println("End Window: " + endWindowStr);
        System.out.println("\n\n");
        */
        
        //get all location updates in the window
        ArrayList<Location> allLocations = LocationDAO.getAllLocationsInWindow(startWindowStr, endWindowStr);
        
        //DEBUG
        /*
        System.out.println("Getting all locationIDs....");
        for(Location location: allLocations) {
            System.out.println("\tlocationID: " + location.getLocationID() + ", mac: " + location.getMacAddress() + ", time: " + location.getTimestamp());
        }
        */
        
        //seperate location updates by MAC Address
        HashMap<String, ArrayList<Location>> macLocationsMap = new HashMap<>();
        for(Location currLocation : allLocations) {
            
            String currMac = currLocation.getMacAddress();
            ArrayList<Location> currMacLocations =  macLocationsMap.get(currMac);
            
            if(currMacLocations == null) {  //first time comming accross a location from this MAC Address
                currMacLocations = new ArrayList<>();
            }
                
            currMacLocations.add(currLocation);
            
            //put mac locations into hashmap.
            macLocationsMap.put(currMac, currMacLocations);
        }
        
        //set totalUsers
        totalUsers = macLocationsMap.keySet().size();
        
        //Use macLocationsMap to create macChunksMap
        macChunksMap = new HashMap<>();
        for(Entry<String, ArrayList<Location>> entrySet : macLocationsMap.entrySet()) {
            
            String currMac = entrySet.getKey();
            ArrayList<Location> currLocations = entrySet.getValue();
            
            //create chunks
            ArrayList<LocationChunk> currChunks = LocationChunkUtility.getLocationChunks(currLocations, startWindow, endWindow);
            
            //smooth by location id
            currChunks = LocationChunkUtility.smoothChunksByLocationId(currChunks);
            
            //remove chunks outside sis. the -1s
            Iterator<LocationChunk> iter = currChunks.iterator();
            while(iter.hasNext()) {
                LocationChunk currChunk = iter.next();
                int locationID = currChunk.getLocationID();
                if(locationID == -1) {
                    iter.remove();
                }
            }
            
            //add chunks to map
            macChunksMap.put(currMac, currChunks);
        }
        
//        //DEBUG 
//        System.out.println("\n\nTurning locations into chunks map...");
//        for(Entry<String, ArrayList<LocationChunk>> entrySet : macChunksMap.entrySet()) {
//            
//            System.out.println("\tmac: " + entrySet.getKey());
//            for(LocationChunk chunk : entrySet.getValue()) {
//                System.out.print("\tChunk: " + chunk.getMacAddress() + ", " + chunk.getLocationID());
//                System.out.println("\t " + sdf.format(chunk.getStartWindow()) + "\t" + sdf.format(chunk.getEndWindow()));
//            }
//            System.out.println("=============");
//        }
        
        //Remove mac Addresses with time < 12
        //Generate retarded generalized location stay time
        macLocationTreeMap = new HashMap<>();
        Iterator<Entry<String, ArrayList<LocationChunk>>> iter = macChunksMap.entrySet().iterator();
        while(iter.hasNext()) {
            
            Entry<String, ArrayList<LocationChunk>> entry = iter.next();
            
            String currMAC = entry.getKey();
            
            int totalTime = 0;
            for(LocationChunk chunk : entry.getValue()) {
                int locationID = chunk.getLocationID();
                Long timeSpent = chunk.getDuration();
                
                //add time to total time spent
                totalTime += timeSpent;
                
                //add time to macLocationTreeMap
                TreeMap<Integer, Long> locationTimeMap = macLocationTreeMap.get(currMAC);
                if(locationTimeMap == null) {
                    locationTimeMap = new TreeMap<>();
                }
                
                Long timeInMap = locationTimeMap.get(locationID);
                if(timeInMap == null) {
                    timeInMap = timeSpent;
                } else {
                    timeInMap += timeSpent;
                }
                locationTimeMap.put(locationID, timeInMap);
                macLocationTreeMap.put(currMAC, locationTimeMap);
            }
            
            //DEBUG
//            System.out.println("MAC: " + currMAC);
//            System.out.println("totalTimeSpent: " + totalTime);
//            System.out.println("location, timeSpent: ");
//            macLocationTimeMap.get(currMAC).entrySet().forEach(thing->System.out.println("\t" + thing.getKey() + "\t" + thing.getValue()));
            
            //if time of a person is < 12min, remove this person 
            if(totalTime < (60 * 12)) {
                iter.remove();
                macLocationTreeMap.remove(currMAC);
            }
        }
        
        //init hashset for groupsfound
        groupsFound = new HashSet<>();
    }
    
    /**
     * Method that finds all groups within the query window specified in the constructor.
     * @return List of LocationGroups.
     */
    /**
     * Method that finds all groups within the query window specified in the constructor.
     * @return List of LocationGroups.
     */
    public ArrayList<LocationGroup> getGroups() {
        
        //Populate groupsFound list. permutate and add all combinations. 
        LocationGroup baseGroup = new LocationGroup();
        baseGroup.setUnattemptedMACs(new ArrayList<>(macChunksMap.keySet()));
        baseGroup.recalculateLocationTimeMap();
        findNextMember(baseGroup);
        
        //remove subsets
        //This is about to get ghetto
        ArrayList<LocationGroup> groupsFoundList = new ArrayList<>(groupsFound);
        ArrayList<LocationGroup> tempList = new ArrayList<>();
        int numGroups = groupsFoundList.size();
        
        for(int i = numGroups - 1; i >= 0; i--) {
            LocationGroup firstGroup = groupsFoundList.get(i);
            
            boolean isSubsetOfSomething = false;
            
            for(int k = numGroups - 1; k >= 0; k--) {
                LocationGroup secondGroup = groupsFoundList.get(k);
                
                //same element in list
                if(i == k) {
                    continue;
                }
                
                //check if second is subset of first
                if(firstGroup.isSubsetOf(secondGroup)) {
                    isSubsetOfSomething = true;
                }
            }
            
            if(!isSubsetOfSomething) {
                tempList.add(firstGroup);
            }
        }
        
        //locations already sorted by TreeMap
        //sort members by email and macAddress
        Comparator<Demographic> demographicComparator = Comparator.comparing(Demographic::getEmail)
                                                                  .thenComparing(Demographic::getMacAddress);
        for(LocationGroup group : tempList) {
            
            ArrayList<Demographic> members = group.getMembers();
            Collections.sort(members, demographicComparator);
        }
        
        //sort groups by size, total-time-spent, members
        Comparator<LocationGroup> groupComparator = Comparator.comparing(LocationGroup::getSize)
                                                              .thenComparing(LocationGroup::getTotalTimeSpent).reversed()
                                                              .thenComparing(LocationGroup::getMemberMACs,
                                                                             (list1, list2)-> {return String.join("", list1).compareTo(String.join("", list2));});
        Collections.sort(tempList, groupComparator);
        
        //Explanation for the weird shit above
        //Comparator.comparing(Demographic::getEmail); This method returns a comparator
        //  parameter is a method to extract the field/value you want to compare between both objects
        //      so for Demographic::getEmail, I want to compare email
        //Comparator.comparing(valueExtractor, method to compare values)
        //      This is what is happening in the groupComparator. Since Java cannot naturally compare Strings, I have to convert each list to a String and compare it
        
        return tempList;
    }
    
    /**
     * Recursive method, explores all paths of combination of users found in query window. Adds groups found to groupsFound field
     * @param baseGroup 
     */
    protected void findNextMember(LocationGroup baseGroup) {    
        
        ArrayList<String>baseUnattemptedMACs = baseGroup.getUnattemptedMACs();
        ArrayList<String> baseMemberMACs = baseGroup.getMemberMACs();
        ArrayList<LocationChunk> baseCommonChunks = baseGroup.getCommonChunks();
        
        //Loop unattempted MACs
        for(String candidateMAC : baseUnattemptedMACs) {
            
            ArrayList<LocationChunk> candidateChunks = macChunksMap.get(candidateMAC);
                        
            //Make new Locationgroup object based on baseGroup parameter >> Java passes by reference, google it.
            // Basically, you dont want to touch baseGroup caus thats the base lol
            // The fineNextMethod finds different combinations of users/macAddresses so....
            // PAIR_1: [base, mac1]         PAIR_2: [base, mac2]    >>If you simply base.add(mac,chunks) then itll screw up, cause base will change like...
            // IF YOU DONT REBASE...  PAIR_1: [base, mac1]      PAIR_2: [base+mac1, mac2]
            LocationGroup rebasedGroup = new LocationGroup();
            rebasedGroup.setUnattemptedMACs(new ArrayList<>(baseUnattemptedMACs));
            rebasedGroup.setMemberMACs(new ArrayList<>(baseMemberMACs));
            rebasedGroup.setCommonChunks(new ArrayList<>(baseCommonChunks));
            
//            //DEBUG
//            System.out.println("\n=================\nAttempting grouping...");
//            System.out.print("Base members: ");
//            baseMemberMACs.forEach(mac->System.out.print(mac + ", "));
//            System.out.println("\nBase Common Chunks: ");
//            baseCommonChunks.forEach(chunk->System.out.println("\t" + chunk.getLocationID() + "\t" + chunk.getStartWindow() + "\t" + chunk.getEndWindow()));
//            System.out.println("new Member: " + candidateMAC);
//            System.out.println("New member chunks: ");
//            candidateChunks.forEach(chunk->System.out.println("\t" + chunk.getLocationID() + "\t" + chunk.getStartWindow() + "\t" + chunk.getEndWindow()));
            
            
            //First member of the group
            if(rebasedGroup.getMemberMACs().isEmpty()) {
                rebasedGroup.getMemberMACs().add(candidateMAC);
                rebasedGroup.getUnattemptedMACs().remove(candidateMAC);
                rebasedGroup.setCommonChunks(candidateChunks);
                rebasedGroup.recalculateLocationTimeMap();
//                System.out.println("FIRST COMBINE");
                //continue on path
                findNextMember(rebasedGroup);
                continue;
            }
            
            //Lookahead: Check if new group of current + candidate member already exists in found groups
            ArrayList<String> potentialGroup = new ArrayList<>(baseMemberMACs);
            potentialGroup.add(candidateMAC);
            if(groupAlreadyTried(potentialGroup)) {
                //remove mac from unattemptedMACs
                rebasedGroup.removeUnattemptedMAC(candidateMAC);
//                System.out.println("GROUP ALREADY TRIED");
                continue;
            }
            
            //Generalized checking of time.
            TreeMap<Integer, Long> candidateLocationTimeMap = macLocationTreeMap.get(candidateMAC);
            if(!baseGroup.roughlySharesCommonTime(candidateLocationTimeMap)) {
//                System.out.println("FAILED ROUGH LOCATION TIME CHECK");
                continue;
            }
            
            //potential group has not been tried before
            boolean addWasSuccessful = rebasedGroup.addMember(candidateMAC, candidateChunks);
            if(addWasSuccessful) {
//                System.out.println("SUCCESS!");
                //continue on path
                findNextMember(rebasedGroup);
                continue;
                
            } else {    //skip
//                System.out.println("INVALID COMBINATION!");
                continue;
            }
        }
        
        //Break point 1: end of the road, tried everything
        if(baseMemberMACs.size() > 1) {
            groupsFound.add(baseGroup);
        }
    }

    /**
     *Method returns total number of unique mac addresses found in the query time window
     * @return int total number of users
     */
    public int getTotalUsers() {
        return totalUsers;
    }


    /**
     * Method checks if a combination of mac addresses has already been attempted based on groups already found
     * @param potentialGroup
     * @return true if group has already been tried, false if it has not
     */
    public boolean groupAlreadyTried(ArrayList<String> potentialGroup) {
        
        //loop all found groups
        for(LocationGroup groupFound : groupsFound) {
            
            ArrayList<String> memberMACs = groupFound.getMemberMACs();
            
            boolean currGroupMatch = true;  //true if curGroup matches potentialGroup
            
            //loop potential group
            for(String potentialMAC : potentialGroup) {
                
                //check if all MACs in potentialGroup is in groupFound
                if(!memberMACs.contains(potentialMAC)) {
                    currGroupMatch = false;
                    break;
                }
            }
            
            //if currGroup matches, means potentialGroup is subset or duplicate, therefore return true
            if(currGroupMatch) {  
                    return true;
            }
        }

        //Code reaches this point only when not even 1 foundGroup can match potentialGroup: subgroup or duplicate
        return false;
    }
    
}


