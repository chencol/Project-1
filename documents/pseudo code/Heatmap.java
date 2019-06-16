Heatmap sudoCode
/**NOTE
Currently designed to display a table.

Actual UI heatmap is alittle difficult and im not sure how to create some kind of SVG for each floor map
Maybe there are tools to do it, but as it is not a requirement. We will leave beauty code to the end

**/


HeatmapController

String date = request.getParameter("date");
String floor = request.getParameter("floor");

ArrayList<String> errMsgs = validateParameters(date, floor);
if(!errMsgs.isEmpty()) {
    //set request attribute for errMsg and forward to heatmap.jsp
}

//get all location IDs found in floor
//K: semanticName V:locationIDs
HashMap<String, ArrayList<Integer>> semanticIDMap = LocationLookup.getLocationIDsInFloor(String floor);

//Get everyone found in the timewindow
ArrayList<Location> allUsers = LocationDAO.getAllLocationsInWindow(String startWindow, Stirng endWindow);

//Make map of users and their last location
//K: macAddress, V: semantic name
HashMap<String, Location> macLocationMap = getLastLocation(allUsers);

HashMap<String, Density> heatMap = new HashMap<>(); //k: semanticPlace, V:Density object
//loop macLocationMap
    Location currLocationLookup = locationLookupDAO.getLocationLookup(locationID);
    //get Density obj from heatMap
        //if null >> insert new Density with count 1
        //if not null >> density.addCount();

//Deal with zero density semanticPlaces
insertZeroDensitySemanticPlaces(heatMap);

//return response

//JSON
    //unpack map into JSON stuff, straight forward

//app UI
    //set request attribute heatMap
    //forward to heatmapView.jsp



public void insertZeroDensitySemanticPlaces(HashMap<String, Density> heatMap) {
    
    ArrayList<String> allSemanticPlaces = LocationLookupDAO.getAllSemanticPlaces();
    
    for(String semPlace : allSemanticPlaces) {
        if(heatMap.get(semPlace) == null) {
            heatMap.put(semPlacec, new Density());
        }
    }
    
}

public HashMap<String, Location> getLastLocation(ArrayList<Location> allUsers) {
    
    //loop allUsers
    //get from HashMap
        //if null >> add Location 
        //if Location found >> use Location.compareTo();
            //if compareTo() != -1 then add Location
    
    //return HashMap
}


public ArrayList<String> validateParameters(String date, String floor) {
    //Validate Date
    Use previously coded data validations
    
    //Validate floor    
    Valid values = [B1, L1, L2, L3, L4, L5];    //STORE THIS IN location_lookup as final field
}


//class holds count and a method to turn count-to-density
public class Density {
    int count = 0;
    
    public int getCount();
    
    public int addCount();
    
    public int getDensity() {
        if(count > 0 && count <= 5) {
            return 1;
        }....
    }
}