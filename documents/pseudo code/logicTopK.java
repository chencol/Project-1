TopK companions
1. get all location entries in the previous 15min window in a specific Semantic Place

2. Ceate a HashMap<MAC, Location>, include only the most recent Location updates -> boolean isAfter(Loction other) in Location Class

3. Get all Location entries in the next 15min

5. Loop all MACs in previous 15mins

    7. Loop all Location in next 15min
    8. If time stayed > 5mins, store next 15min Locations in a List<Location>
        - query 
    9. Get latest Location
    10. Add to hashMap<SemanticPlace, count>

    
Companion   -> Based on Location ID 
            -> Ranked by time spent
            -> Return Rank, MAC, co-located time, email(if in demographics)
                -> Sort by Rank and MAC Address

                
                
* User stays at a location until the next update time
* If there are no subsequent updates in the processing window, User stays for 5 mins


Make Location Comparable by TIMESTAMP

Make Location isBefore(Location) isAfter(Location) methods that return second difference 

Validate parameters

Create 2 variables, startWindow & endWindow DATE objects
    - https://docs.oracle.com/cd/E19776-01/820-4867/ghrst/index.html
    
Create SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    - format()
    -Parse()
    
Get target User locations in 15min window
    - Select * from Location where MAC = MAC AND 
        - https://stackoverflow.com/questions/14069256/mysql-timestamp-comparison
    - sort list by date
    - get list of LocationWindowChunk -> getLocationWindowChunk();
    
Get list of user LocationWindow that covers the ENTIRE 15min window
    - LocationWindow holds location ID (or -1 if outside SIS), start time(inclusive), end time(exclusive) 
    - Put everyone into HashMap<MAC, ArrayList<Location>> 
    - loop HashMap
        - getLocationWIndowChunkArray for each mac
        - getOverlappingTime(targetChunks, currChunks)
    
Get all Users within the 15min window sort by MAC


//ArrayList <macAddress, List(Location)> -> Person A
//ArrayList <macAddress, List(Location)> -> Person B


//ArrayList of location object which user spend tgt, compute the time spent tgt 
//use comparator to check if (A,B) & (B,A) is the same 


getTotalOverlappingTime() {
    
    int overlap = 0;
    
    for(Chunk targetChunk : targetChunks) {
        for(Chunk currChunk : currChunks) {
            
            overlap += targetChunk.getOverlappingTime(currChunk);
            
        }
    }
    
}

//validate if a location is more than 5min?
//return a vaild arrayList of location

getLocationWIndowChunkArray(ArrayList<location> locationList, startWindow, endWindow) {
    
    //placeholder objects
    Location startWindowRep = new Location("", startWindow, "");
    Location endWindowRep = new 
    
    //add endWindowRep to locationList
    locationList.add(endWindowRep);
    
    //Create list of LocationWindowChunks to return
    ArrayList<LocationWindowChunk> retList
    
    //sort locationList by TIME
    
    //loop locationList
    Location prevLocation = startWindowRep;
    for(Location currLocation : locationList) {
        if(prevLocatin == null && currLocation.isAfter(startWindowRep)) {       //first currLocation
            LocationWindowChunk chunk = new LocationWindowChunk(currLocation.getMAC(), locationID, startWindow, currLocation.getTime());
            retList.add(chunk);
            prevLocation = currLocation;
            continue;
        } else if (prevLocation == null){    //right on the startWindow
            prevLocation = currLocation;
            continue;
        } 
        
        if (currLocation.isAfter(prevLocation) <= 60 * 5) {    //time diff less than 5 mins
            LocationWindowChunk chunk = new LocationWindowChunk(currLocation.getMAC(), locationID, prevLocation.getTime(), currLocation.getTime());
            retList.add(chunk);
            prevLocation = currLocation;
            continue;
        }
        
        if(currLocation.isAfter(prevLocation) > 60*5) {  // time diff more than 5 mins
            LocationWindowChunk chunk = new LocationWindowChunk(currLocation.getMAC(), locationID, prevLocation.getTime(), prevLocation.getTime() + 5mins);
            retList.add(chunk);
            
            chunk = new LocationWindowChunk(currLocation.getMAC(), -1, prevLocation.getTime() + 5mins, currLocation.getTime());
            retList.add(chunk);
            
            prevLocation = currLocation;
            continue;
        }   
        return retList;
    }
}

