
package is203.se.Bootstrap;

import java.util.HashMap;
import java.util.TreeSet;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * This class represents a result of attempting to import a csv to the database.
 * <p>
 * Holds the number of Successful rows inserted and the error messages from validation
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class BootstrapResult {
    
    private int numSuccesses;
    private HashMap<Integer, String[]> allErrMsgs;
    
    /**
     * Constructor method for a BootstrapResult
     * @param numSuccesses number of successes
     * @param allErrMsgs map of K:row, V: array of error messages
     */
    public BootstrapResult(int numSuccesses, HashMap<Integer, String[]> allErrMsgs) {
        this.numSuccesses = numSuccesses;
        this.allErrMsgs = allErrMsgs;
    }
    
    /**
     * This method returns the number of successful inserts to the database that was stored in this BootstrapResult
     * @return the number of successes
     */
    public int getNumSuccesses() {
        return numSuccesses;
    }
    
    /**
     * This method returns all errors found while attempting to import a csv, that was stored in this BootstrapResult
     * @return HashMap of Integer, String[] key: rowNumber, Value: array of error messages
     */
    public HashMap<Integer, String[]> getAllErrMsgs() {
        return allErrMsgs;
    }
    
    /**
     * Method aggregates the error messages of the BootstrapResult parameters into a single JSONArray
     * @param demographicResult
     * @param locationLookupResult
     * @param locationResult
     * @return JSONArray of error messages
     */
    public static JSONArray totalErrors(BootstrapResult demographicResult, BootstrapResult locationLookupResult, BootstrapResult locationResult) {
        //error messages
        JSONArray error = new JSONArray();
        
        if(demographicResult != null) {
            //loop demographics error messages
            HashMap<Integer, String[]> errMsgs = demographicResult.getAllErrMsgs();
            //sort keys by lines
            TreeSet<Integer> sortedkeys = new TreeSet<>(errMsgs.keySet());
            
            for(Integer line : sortedkeys) {
                JSONObject temp = new JSONObject();
                temp.put("file", "demographics.csv");
                temp.put("line", line);
                JSONArray errMessages = new JSONArray();
                for(String errMsg : errMsgs.get(line)) {
                    errMessages.add(errMsg);
                }
                temp.put("message", errMessages);
                error.add(temp);
            }
        }
            
        if(locationLookupResult != null) {
            //loop location lookup error messages
            HashMap<Integer, String[]> errMsgs = locationLookupResult.getAllErrMsgs();
            //sort keys by lines
            TreeSet<Integer> sortedkeys = new TreeSet<>(errMsgs.keySet());
            
            for(Integer line : sortedkeys) {
                JSONObject temp = new JSONObject();
                temp.put("file", "location-lookup.csv");
                temp.put("line", line);
                JSONArray errMessages = new JSONArray();
                for(String errMsg : errMsgs.get(line)) {
                    errMessages.add(errMsg);
                }
                temp.put("message", errMessages);
                error.add(temp);
            }
        }
            
        if(locationResult != null) {
            //loop location error messages
            //loop location lookup error messages
            HashMap<Integer, String[]> errMsgs = locationResult.getAllErrMsgs();
            //sort keys by lines
            TreeSet<Integer> sortedkeys = new TreeSet<>(errMsgs.keySet());
            
            for(Integer line : sortedkeys) {    //may need to rewrite this to look less retarded
                JSONObject temp = new JSONObject();
                temp.put("file", "location.csv");
                temp.put("line", line);
                JSONArray errMessages = new JSONArray();
                for(String errMsg : errMsgs.get(line)) {
                    errMessages.add(errMsg);
                }
                temp.put("message", errMessages);
                error.add(temp);
            }
        }
            
        return error;
    }    

    /**
     * Method aggregates the number of successful records loaded per csv, from the BootstrapResult parameters into a single JSONArray
     * @param demographicResult
     * @param locationLookupResult
     * @param locationResult
     * @return the JSONArray of number of records loaded
     */
    public static JSONArray totalNumRecordLoaded(BootstrapResult demographicResult, BootstrapResult locationLookupResult, BootstrapResult locationResult) {
        JSONArray numRecordLoaded = new JSONArray();
            
        if(demographicResult != null) {
            JSONObject demographicsAdded = new JSONObject();
            demographicsAdded.put("demographics.csv", demographicResult.getNumSuccesses());
            numRecordLoaded.add(demographicsAdded);
        }
            
        if(locationLookupResult != null) {
            JSONObject locationLookupAdded = new JSONObject();
            locationLookupAdded.put("location-lookup.csv", locationLookupResult.getNumSuccesses());
            numRecordLoaded.add(locationLookupAdded);
        }
        
        if(locationResult != null) {
            JSONObject locationAdded = new JSONObject();
            locationAdded.put("location.csv", locationResult.getNumSuccesses());
            numRecordLoaded.add(locationAdded);
        }
        
        return numRecordLoaded;
    }
}
