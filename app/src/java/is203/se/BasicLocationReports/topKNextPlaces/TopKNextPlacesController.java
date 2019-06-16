/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.topKNextPlaces;


import is203.se.BasicLocationReports.locationChunk.LocationChunk;
import is203.se.BasicLocationReports.locationChunk.LocationChunkUtility;
import is203.se.BasicLocationReports.topKPlaces.DateOperator;
import is203.se.BasicLocationReports.topKPlaces.JsonPopularPlace;
import is203.se.BasicLocationReports.topKPlaces.ParameterChecker;
import is203.se.BasicLocationReports.topKPlaces.PopularPlace;
import is203.se.Connection.ConnectionFactory;
import is203.se.Connection.JDBCCloser;
import is203.se.DAO.LocationDAO;
import is203.se.Entity.Location;
import is203.se.JsonAuthentication.TokenValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Topk next places servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class TopKNextPlacesController extends HttpServlet {
    
    /**
     * The url requested from the webApplication.
     */
    private String url1 ="/TopKNextPlacesController";
    /**
     * The url requested for JSON service
     */
    private String url2 = "/json/top-k-next-places";
    /**
     * ArrayList of macAddress string
     */
    private ArrayList<String> macAddresses = null;

    /**
     * Method to handle http post and get request
     * @param request http request
     * @param response http response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try (PrintWriter out = response.getWriter()) {
           String url = request.getServletPath();
           String dateString = request.getParameter("date");
           String semanticPlace = request.getParameter("origin");
           String k =request.getParameter("k");
           String token = null;
           if(dateString!=null&&dateString.length()==16){
               dateString = dateString + ":00";
           }
           
           //isSemanticPlaceValid = ParameterChecker.checkValidSemanticPlace(semanticPlace);
           /*Only the one with all correct parameters will proceed"*/
           //Take different action depends on the url.
           //For the request from form. We dont not need to check para since all para are given in correct format and in reasonable range.
           //So the parameter checking only apply to the Json Service.
           if(url.equals(url2)){
                token = request.getParameter("token");
                ArrayList<String> errorMsgs = new ArrayList<String>();
               
                //validate fields
                
                
                //validate token field
                if(checkToken(token)!=null){
                    errorMsgs.add(checkToken(token));
                }
                else{
                     //validate k field
                    if(k==null){
                        k="3";  //If k is null, we will give it a default value 3; 
                    } else if(!ParameterChecker.checkK(k)) {
                        errorMsgs.add("invalid k");
                    }
                                    
                
                    //validate date field
                    if(checkDate(dateString)!=null){
                        errorMsgs.add(checkDate(dateString));
                    }
                     //validate origin field
                    if(checkSemanticPlace(semanticPlace)!=null){
                        errorMsgs.add(checkSemanticPlace(semanticPlace));
                    }
                }
                
                
                //if all fields are valid
                JSONObject result;
                if(errorMsgs.isEmpty()){    //no error messages
                    
                    result = getResult(url,semanticPlace,dateString,k);
                    result.put("status", "success");
                    //out.println(result);
                    
                } else {    //error messages found
                    
                    JSONArray JSONError = new JSONArray();
                    Collections.sort(errorMsgs);
                    for(String error: errorMsgs){
                        JSONError.add(error);
                    }                
                    
                    result = new JSONObject();
                    result.put("status","error");
                    result.put("messages",JSONError);
                    
                    //return; //stops subsequent code from running
                } 
                out.println(result);
            }
           
           
           //Proceed the request from form.
            if (url.equals(url1)){
                
                JSONObject result = getResult(url,semanticPlace,dateString,k);
                result.put("status", "success");
                request.setAttribute("result", result);
                request.setAttribute("k",k);
                RequestDispatcher view = request.getRequestDispatcher("TopKNextPlacesView.jsp");
                view.forward(request,response);
            }

        } catch (ParseException ex) {
            Logger.getLogger(TopKNextPlacesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Method get the requested service result and store them in a JSONObject
     * @param url The requested url 
     * @param semanticPlace The semantic place enter by user
     * @param dateString Date enter by the user
     * @param k K enter by the user
     * @return JSONObject which contains the requested service result
     * @throws ParseException
     */
    public JSONObject getResult(String url, String semanticPlace, String dateString, String k) throws ParseException{
        JSONObject jsonResult = new JSONObject();
        
        HashMap<String, Integer> recordsOfPlaceWithCount = new HashMap<>();
        
        int rank = Integer.parseInt(k);
        
        //
        Date midTimePoint = ParameterChecker.getDateByString(dateString);       
        Date firstTimePoint = DateOperator.getDateBeforeMinutes(midTimePoint, 15);
        Date lastTimePoint = DateOperator.getDateBeforeMinutes(midTimePoint, -15);
        
        String firstTimePointString = DateOperator.getFormattedTimeString(firstTimePoint);
        String lastTimePointString = DateOperator.getFormattedTimeString(lastTimePoint);
        String midTimePointString = DateOperator.getFormattedTimeString(midTimePoint);
        
        //these are users who have their last Location update in the time window at the specific semantic place
        macAddresses = getCorrespondingUser(semanticPlace,firstTimePointString,midTimePointString);

        //Loop through each user/macAddress and get their locations in the next 15min window
        HashMap<String, ArrayList<Location>> userLocationsMap = new HashMap<>();
        for(String macAddress: macAddresses){
            ArrayList<Location> locations = LocationDAO.getAllLocationsInWindow(macAddress, midTimePointString, lastTimePointString);      
            if(!locations.isEmpty()){
                userLocationsMap.put(macAddress,locations);
            }
        }
        
        //Loop each user 
        int numberOfNextPlaceUsers = 0;
        for(String mac: userLocationsMap.keySet()){
            String place = retrieveThePlaceStayed(userLocationsMap.get(mac), midTimePoint, lastTimePoint);
            if(place!=null){
                
                numberOfNextPlaceUsers++;
                if(recordsOfPlaceWithCount.containsKey(place)){ //if semantic place has already been found, increment count
                    
                    int count = recordsOfPlaceWithCount.get(place);
                    recordsOfPlaceWithCount.put(place,count+1);
                    
                } else {    //first time semantic place has been found
                    recordsOfPlaceWithCount.put(place,1);
                }
                
            }
        }
        
        
        LinkedHashMap<String,Integer> sortedRecords = (LinkedHashMap<String,Integer>)sortByValue(recordsOfPlaceWithCount);          
        JSONArray finalResult = new JSONArray();
        //If it is a normal request
        if(url.equals(url1)){
            int previousCount = 0;
            int round = 1;
            int ranking = 1;
            PopularPlace popularPlace = null;
            PopularPlace previousPlace = null;
            //Loop through the place in the linkedhashmap to assign the rank and number of people to them.
            for(String place: sortedRecords.keySet()){
                if(round==1){
                    popularPlace = new PopularPlace(place, sortedRecords.get(place),1);
                    previousPlace = popularPlace;
                }else{
                    int count = sortedRecords.get(place);
                    if(count!=previousPlace.getCount()){
                        if(previousPlace.getRank()+1<=rank){
                            popularPlace = new PopularPlace(place, sortedRecords.get(place),previousPlace.getRank()+1);
                            finalResult.add(previousPlace);
                            previousPlace = popularPlace;
                        }else{
                            break;
                        }
                    }else{
                        previousPlace.addPlace(place);
                    }
                }
                round++;
            }
            finalResult.add(previousPlace);
        }else{
            //If the request is JSON request
            int previousCount = 0;
            int round = 1;
            int ranking = 1;
            ArrayList<JsonPopularPlace> places = new ArrayList<JsonPopularPlace>();
            ArrayList<JsonPopularPlace> placesList = new ArrayList<JsonPopularPlace>();
            for(String place: sortedRecords.keySet()){
                places.add(new JsonPopularPlace(0, place, sortedRecords.get(place)));
            }
            //Loop through the place in the places ArrayList to assign the rank and number of people to them.
            for(JsonPopularPlace place: places){
                if(ranking==1&&round==1){
                    previousCount=place.getCount();
                    place.setRank(1);
                    placesList.add(place);
                }else{
                    if(place.getCount()!=previousCount){
                        ranking++;
                        if(ranking>rank){
                            break;
                        }else{
                            place.setRank(ranking);
                            previousCount = place.getCount();
                            placesList.add(place);

                        }
                    }else{
                        place.setRank(ranking);
                        placesList.add(place);
                    }
                }

                round++;
            }
            
            //sort the places which have the same rank in alphabetic order
            Collections.sort(placesList);
            //put the JsonPopularPlace object into JSONObject
            for(JsonPopularPlace place: placesList){
                JSONObject obj = new JSONObject();
                obj.put("rank",place.getRank());
                obj.put("semantic-place",place.getSemantic_place());
                obj.put("count", place.getCount());
                finalResult.add(obj);
            }
        }
        jsonResult.put("total-users", macAddresses.size());
        jsonResult.put("total-next-place-users", numberOfNextPlaceUsers);
        jsonResult.put("results",finalResult);
        return jsonResult;
    }
    

    /**
     * Method taking out those users who fulfilled the requirement. The requirement is the last update should be semenatic place from user in specific time window.
     * @param semanticPlace Semantic place entered by user
     * @param date1 StartTime of the window
     * @param date2 EndTime of the window
     * @return ArrayList of macAddress whose latest update is in the specific semantic place at this specific time window(date1 and date2)
     */

    public ArrayList<String> getCorrespondingUser(String semanticPlace, String date1, String date2){
        ArrayList<String> users = new ArrayList<String>();
        String query = "SELECT l.mac_address "
                    +   "FROM location_lookup lk, location l, (SELECT  mac_address mac, max(timestamp) t "
                    +                                           "FROM location "
                    +                                           "WHERE timestamp>=?" 
                    +                                           " AND timestamp<?" 
                    +                                           " GROUP BY mac_address) AS ref "
                    +   "WHERE l.mac_address = ref.mac "
                    +   "AND l.location_id = lk.location_id "
                    +   "AND ref.t = l.timestamp "
                    +   "AND lk.semantic_place = ? "
                    +   "ORDER BY timestamp asc";

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionFactory.getConnection();            
            stmt = con.prepareStatement(query);
            stmt.setString(1,date1);
            stmt.setString(2,date2);
            stmt.setString(3,semanticPlace);
            rs = stmt.executeQuery();
            while(rs.next()){
                String macAddress = rs.getString(1);
                users.add(macAddress);
            }
            
        } catch (SQLException ex) {
            //TO-DO: LOGGER IMPLEMENTATION
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return users;
    }
    
    
    /**
     * Method will figure out what's the eligible place that the user stayed in the specific time window.
    Or null if he did not stay anywhere for at least 5 mins
     * @param locations ArrayList of Location object
     * @param startDate Start Time
     * @param endDate End Time
     * @return place the user stayed or null if the user did not stay at any place in the specific time window(startDate, endDate).
     */

    public String retrieveThePlaceStayed(ArrayList<Location> locations, Date startDate, Date endDate){
        
        //Turn current user's Location updates into LocationChunks which represent where he/she stayed and for when to when
        ArrayList<LocationChunk> locationChunks = LocationChunkUtility.getLocationChunks(locations, startDate, endDate);
        
        
        LocationChunkUtility.smoothChunksByLocationId(locationChunks);
        locationChunks = LocationChunkUtility.smoothChunksBySemanticPlace(locationChunks);

        //get the last location stayed that is not -1, -1 means outside of sis
        for(int i = locationChunks.size() - 1; i >= 0; i--) {
            LocationChunk currChunk = locationChunks.get(i);
            
            String currSemanticPlace = currChunk.getSemanticPlace();
            
            if(!currSemanticPlace.equals("outsideSIS") && currChunk.getDuration() >= 60 * 5) {  //not outside of sis and stayed at least 5 min
                return currSemanticPlace;
            }
        }
        
        return null;
        
    }
    //This method is used to sort the hashmap according to the value in descending order.

    /**
     * Method is used to sort the hashmap according to the value in descending order.
     * @param <K>
     * @param <V>
     * @param map a map which has semantic place as key and the count of it as value.
     * @return a sorted map according to the value in descending order
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return -((o1.getValue()).compareTo(o2.getValue()));
            }
        });
        
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        
        return result;
        
    }
    
        /**
      * Method checks token field for blank, missing and validity
      * @param date date provided by user
      * @return error message or null if valid
      */
     public String checkDate(String date){
        if(date==null){
            return "missing date";
        } else if (date.isEmpty()) {
            return "blank date";
        } else if (!ParameterChecker.checkDateString(date)) {
            return "invalid date";
        } else {
            return null;
        }
    }
    
     /**
      * Method checks token field for blank, missing and validity
      * @param token provided by user
      * @return error message or null if valid
      */
    public String checkToken(String token){
        if(token==null){
            return "missing token";
        } else if (token.isEmpty()) {
            return "blank token";
        } else if (!TokenValidator.checkToken(token)){
            return "invalid token";
        } else {
            return null;
        }
    }
    
    /**
     * Method checks origin field for blank, missing and validity
     * @param semanticPlace semanticPlace provided by user
     * @return error message or null if valid
     */
    public String checkSemanticPlace(String semanticPlace){
        if(semanticPlace==null){
            return "missing origin";
        } else if (semanticPlace.isEmpty()) {
            return "blank origin";
        } else if (!ParameterChecker.checkValidSemanticPlace(semanticPlace)){
            return "invalid origin";
        } else {
            return null;
        }
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
	/**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
}
