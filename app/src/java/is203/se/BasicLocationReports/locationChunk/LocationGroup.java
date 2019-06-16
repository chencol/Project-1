
package is203.se.BasicLocationReports.locationChunk;

import is203.se.DAO.DemographicDAO;
import is203.se.Entity.Demographic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *This class represents a group of MAC Addresses that share common time
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationGroup {
    
    private ArrayList<String> unattemptedMACs;
    private ArrayList<String> memberMACs;
    private ArrayList<LocationChunk> commonChunks;
    private TreeMap<Integer, Long> locationTimeMap;
    /**
     * Constructor method
     */
    public LocationGroup() {
        //create lists
        unattemptedMACs = new ArrayList<>();
        memberMACs = new ArrayList<>();
        commonChunks = new ArrayList<>();
        locationTimeMap = new TreeMap<>();
    }
    
    /**
     * method attempts to add a member to the group. Affects objects lists of attemptedMACs and memberMACs
     * @param macAddress
     * @param chunksToAdd
     * @return boolean true if member was successfully added to group, false if not
     */
    public boolean addMember(String macAddress, ArrayList<LocationChunk> chunksToAdd) {
        int timeTogether = LocationChunkUtility.countTotalOverlappingTime(commonChunks, chunksToAdd);
        
        //DEBUG
        //System.out.println("Overlapping time: " + timeTogether);
        
        //handle first add to the group
        if(memberMACs.isEmpty()) {
            unattemptedMACs.remove(macAddress);
            memberMACs.add(macAddress);
            commonChunks = chunksToAdd;
            return true;
        }
        
        //attempt add to group
        if(timeTogether >= 60 * 12) {   //chunksToAdd overlaps with commonChunks by at least 12mins
            
            //recalculate commonChunks
            recalculateCommonChunks(chunksToAdd);
            recalculateLocationTimeMap();
            unattemptedMACs.remove(macAddress);
            memberMACs.add(macAddress);
            return true;
            
        } else {    //overlaping time < 12mins
            
            unattemptedMACs.remove(macAddress);
            return false;
        }
    }
    
    /**
     * Method recalculates common chunks based on an additional list of LocationChunks.
     * @param otherChunks list of chunks to merge with current common chunks
     */
    public void recalculateCommonChunks(ArrayList<LocationChunk> otherChunks) {
        
        ArrayList<LocationChunk> newCommonChunks = new ArrayList<>();
        
        //loop commonChunks
        for(LocationChunk commonChunk : commonChunks) {
            int commonChunkLocationID = commonChunk.getLocationID();
            
            //loop otherChunks
            for(LocationChunk otherChunk : otherChunks) {
                int otherChunkLocationID = otherChunk.getLocationID();
                
                //same location ID
                if(commonChunkLocationID == otherChunkLocationID) {
                    
                    //try getting common LocationChunk
                    LocationChunk newCommonChunk = LocationChunkUtility.getCommonLocationChunk(commonChunk, otherChunk);
                    if(newCommonChunk != null) {    //common time found, getCommonLocationChunk() returns null if no common time
                        newCommonChunks.add(newCommonChunk);
                    }
                }
            }
        }
        
        //smooth by locationID
        newCommonChunks = LocationChunkUtility.smoothChunksByLocationId(newCommonChunks);
        
        //replace common chunks with new common chunks
        commonChunks = newCommonChunks;
    }
    
    /**
     * Method recalculated the LocationTimeMap based on current common chunks
     */
    public void recalculateLocationTimeMap() {
        //reCalculate locationTimeMap
        locationTimeMap = new TreeMap<>();
        for(LocationChunk chunk : commonChunks) {
            int locationID = chunk.getLocationID();
            long timeSpent = chunk.getDuration();
            
            Long timeSpentInMap = locationTimeMap.get(locationID);
            if(timeSpentInMap == null) {
                timeSpentInMap = timeSpent;
            } else {
                timeSpentInMap += timeSpent;
            }
            locationTimeMap.put(locationID, timeSpentInMap);
        }
    }
    
    /**
     * Method checks if this group is a subset of an other group, based on member MACs
     * @param other
     * @return true if this group is a subset of other group. False if it is not.
     */
    public boolean isSubsetOf(LocationGroup other) {
        
        boolean uniqueFound = false;
        
        ArrayList<String> otherMACs = other.getMemberMACs();
        
//        System.out.println("\nComparing groups...");
//        System.out.print("Group_this: ");
//        memberMACs.forEach(mac->System.out.print(mac + ", "));
//        System.out.print("\nGroup_other: ");
//        otherMACs.forEach(mac->System.out.print(mac + ", "));
        
        for(String thisMAC : memberMACs) {
            
            if(!otherMACs.contains(thisMAC)) {
                
                uniqueFound = true;
                break;
                
            }            
        }
        
//        System.out.println("Result: " + !uniqueFound);
        
        return !uniqueFound;
    }
    
    /**
     * Getter method for MACs that have not been attempted to be added into the group
     * @return List of attempted MAC Addresses
     */
    public ArrayList<String> getUnattemptedMACs() {
        return unattemptedMACs;
    }
    
    /**
     * Setter method for MACs that have not been attempted to be added into the group
     * @param unattemptedMACs List of attempted MAC addresses
     */
    public void setUnattemptedMACs(ArrayList<String> unattemptedMACs) {
        this.unattemptedMACs = unattemptedMACs;
    }
    
    /**
     * Method removes a macAddress from the list of unattempted macAddresses
     * @param macAddress to remove
     */
    public void removeUnattemptedMAC(String macAddress) {
        unattemptedMACs.remove(macAddress);
    }
    
    /**
     * Getter method for MACs that are part of the group
     * @return List of Member MAC Addresses
     */
    public ArrayList<String> getMemberMACs() {
        return memberMACs;
    }
    
    /**
     * Setter method for MACs that are part of the group
     * @param memberMACs List of Member MAC Addresses
     */
    public void setMemberMACs(ArrayList<String> memberMACs) {
        this.memberMACs = memberMACs;
    }
    
    /**
     * Getter method for common LocationChunks of all members
     * @return List of LocationChunks
     */
    public ArrayList<LocationChunk> getCommonChunks() {
        return commonChunks;
    }
    
    /**
     * Setter method for common LocationChunks of all members
     * @param commonChunks List of LocationChunks
     */
    public void setCommonChunks(ArrayList<LocationChunk> commonChunks) {
        this.commonChunks = commonChunks;
    }
    
    /**
     * Method checks for equality between LocationGroups, based off members MAC addresses
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        
        if(o instanceof LocationGroup) {
            
            LocationGroup other = (LocationGroup)o;
            ArrayList<String> otherMemberMACs = other.getMemberMACs();
            
            //check same number of MACs
            if(memberMACs.size() != otherMemberMACs.size()) {
                return false;
            }
            
            //check members are the same
            for(String thisMember : memberMACs) {
                if(!otherMemberMACs.contains(thisMember)) {
                    return false;
                }
            }
            
        } else {
            return false;
        }
        
        return true;
    }

    /**
     * returns a hash code based off member MAC addresses sorted by natural order
     * @return int hash code
     */
    @Override
    public int hashCode() {
        //sort memberMACs so method builds hash in alphabetical order
        Collections.sort(memberMACs);
        
        int hash = 5;
        int prime = 31;
        for(String currMac : memberMACs) {
            hash = hash * prime + currMac.hashCode();
        }
        
        return hash;
    }
    
    /**
     * Method returns the number of members in this group
     * @return int number of members
     */
    public int getSize() {
        return memberMACs.size();
    }
    
    /**
     * Method returns total time spent at any location in this locationGroup
     * @return int seconds
     */
    //in seconds
    public int getTotalTimeSpent() {
        
        int totalTimeSpent = 0;
        
        for(LocationChunk currChunk : commonChunks) {
            
            totalTimeSpent += currChunk.getDuration();
            
        }
        
        return totalTimeSpent;
    }
    
    /**
     * Method returns Demographic members of the group sorted by Email and MAC Address
     * @return List of Demographic members
     */
    public ArrayList<Demographic> getMembers() {
        
        ArrayList<Demographic> members = new ArrayList<>();
        
        //fill List of demographics based on list of MACs
        for(String memberMAC : memberMACs) {
            Demographic demographic = DemographicDAO.getDemographic(memberMAC);
            
            if(demographic == null) {   //mac not in demographics table.
                demographic = new Demographic(memberMAC,"name","password","", 'M', 0);
            }
            members.add(demographic);
        }
        
        return members;
    }
    
    /**
     * Method returns locationIDs and the total time spent there of the group sorted by LocationID
     * @return Map of K: locationID, V: time spent
     */
    public TreeMap<Integer, Long> getLocations() {
           return locationTimeMap;
    }
    
        /**
     * Method compared this groups aggregated time spent at locations against another HashMap of locations and time spent
     * @param otherLocationTimeMap
     * @return true if there is potentially at least 12 mins of shared time between both, false if not.
     */
    public boolean roughlySharesCommonTime(Map<Integer, Long> otherLocationTimeMap) {
        
        //DEBUG
//        System.out.println("Current general time spent: ");
//        locationTimeMap.entrySet().forEach(action->System.out.println("\t" + action.getKey() + "\t" + action.getValue()));
//        System.out.println("Candidate general time spent: ");
//        otherLocationTimeMap.entrySet().forEach(action->System.out.println("\t" + action.getKey() + "\t" + action.getValue()));

        int sharedTime = 0;
        for(Map.Entry<Integer, Long> entry : locationTimeMap.entrySet()) {
            
            int currLocationID = entry.getKey();
            long currTimeSpent = entry.getValue();
            
            if(otherLocationTimeMap.containsKey(currLocationID)) {
                Long otherTimeSpent = otherLocationTimeMap.get(currLocationID);
                if(otherTimeSpent != null) {
                    //shared time is the lower of the 2
                    if(currTimeSpent < otherTimeSpent) {
                        sharedTime += currTimeSpent;
                    } else {
                        sharedTime += otherTimeSpent;
                    } 
                }
            }
        }
        
        //check if sharedTime is roughly 12mins
        if(sharedTime >= 60 * 12) {
            return true;
        } else {
            return false;
        }
    }
}

