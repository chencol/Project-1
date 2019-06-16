/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.topKPlaces;

import is203.se.Connection.ConnectionFactory;
import is203.se.Connection.JDBCCloser;
import is203.se.JsonAuthentication.TokenValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Top K Popular Places Servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */


public class TopKPopularPlaceController extends HttpServlet {
    
    /**
     * The url requested from the JSON service.
     */
    private String url1 = "/json/top-k-popular-places";
    /**
     * The url requested for webApplication.
     */
    private String url2 = "/TopKPopularPlaceController";

    /**
     * Method to handle http request
     * @param request http request
     * @param response http response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            boolean isDateValid = true;
            boolean isKValid = true;
            boolean isTokenValid = true;
            JSONObject result = new JSONObject();
            String url = request.getServletPath();
            
            //get k and date from user.
            String rank = request.getParameter("k");
            String dateString = request.getParameter("date");
            
            
            //sometime if the second is 00, the parameter passed from form will not show the :00 for seconds.
            if(dateString!=null&&dateString.length()==16){
                dateString = dateString + ":00";
            }
            //Take different action depends on the url.
           //For the request from form. We dont not need to check para since all para are given in correct format and in reasonable range.
           //So the parameter checking only apply to the Json Service.
            if(url.equals(url1)){
                ArrayList<String> errorMsgs = new ArrayList<>();
                String token = request.getParameter("token");
                
                
                
                //check token
                if(checkToken(token)!=null){
                    errorMsgs.add(checkToken(token));
                    isTokenValid = false; //default is true
                }else{
                    //If k is null, we will give it a default value 3;
                    if(rank==null){
                        rank="3";
                    } else if(!ParameterChecker.checkK(rank)) {
                        isKValid = false;
                        errorMsgs.add("invalid k");
                    } 
                
                //check date field
                    if(checkDate(dateString)!=null) {
                        errorMsgs.add(checkDate(dateString));
                        isDateValid = false;  //default is true
                    }
                }
                
                //if all fields valid
                if(isKValid&&isDateValid&&isTokenValid) {
                    result.put("status","success");
                    result.put("results", getResult(rank,dateString,url));
                } else {
                    //return error json
                    JSONArray JSONError = new JSONArray();
                    Collections.sort(errorMsgs);
                    for(String error: errorMsgs){
                        JSONError.add(error);
                    }
                    result.put("status", "error");
                    result.put("messages", JSONError);
                }
                out.println(result.toJSONString());
                return;
            }
            
            if(url.equals(url2)){
                result.put("status","success");
                result.put("result", getResult(rank,dateString,url));
                request.setAttribute("result", result);
                RequestDispatcher view = request.getRequestDispatcher("TopKPopularPlaceView.jsp");
                view.forward(request,response);
            }
        }     
    }
    
    /**
     *
     * Method to get the requested result as a JSONArray
     * @param k K enter by the user
     * @param dateString
     * @param url The requested url 
     * @return JSONArray object which contains detail about the topK popular places.
     */
    public JSONArray getResult(String k, String dateString, String url){
        try{
            int rank = Integer.parseInt(k);
            
            Date endTime = ParameterChecker.getDateByString(dateString);
            
            Date startTime = DateOperator.getDateBeforeMinutes(endTime,15);
            String startTimeString = DateOperator.getFormattedTimeString(startTime);
            String endTimeString = DateOperator.getFormattedTimeString(endTime);
            
            return retrieveFromDB(startTimeString, endTimeString, rank, url);
            
        }catch(Exception e){
            
        }
        return null;
    }
    

    /**
     * Method that will go into database and get the requested service result them store it in a JSONArray
     * @param startTimeString the startTimeString of the window
     * @param endTimeString the endTimeString of the window
     * @param k the k value entered by the user
     * @param url the requested url
     * @return JSONArray object which contains detail about the topK popular places.
     */


    public JSONArray retrieveFromDB(String startTimeString, String endTimeString, int k, String url){
        String query =      "SELECT COUNT(*), ref2.sm "
                        +   "FROM   (SELECT lk.semantic_place sm, lk.location_id, l.mac_address "
                        +           "FROM location_lookup lk, location l,"
                        +               "(SELECT  mac_address mac, max(timestamp) t "
                        +               "FROM location l1, location_lookup lk1 "
                        +               "WHERE timestamp >= ? "
                        +               "AND timestamp < ? "
                        +               "AND l1.location_id = lk1.location_id "
                        +               "GROUP BY mac_address) AS ref "
                        +           "WHERE l.mac_address = ref.mac "
                        +           "AND l.location_id = lk.location_id "
                        +           "AND ref.t = l.timestamp) AS ref2 "
                        +   "GROUP BY ref2.sm "
                        +   "ORDER BY COUNT(*) DESC";
        JSONArray results = new JSONArray();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{         
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            stmt.setString(1,startTimeString);
            stmt.setString(2, endTimeString);
            rs = stmt.executeQuery();
            
            if(url.equals(url2)){   //WEBAPP
                
                //filter results inito countPopularPlaceMap
                Map<Integer, PopularPlace> countPopularPlaceMap = new LinkedHashMap<>();
                while(rs.next()){
                    int count = rs.getInt(1);
                    String semantic_place = rs.getString(2);
                    
                    PopularPlace currPopularPlaces = countPopularPlaceMap.get(count);
                    if(currPopularPlaces == null) {
                        currPopularPlaces = new PopularPlace(0, new ArrayList<>(), count);
                    }
                    currPopularPlaces.getSemantic_place().add(semantic_place);
                    countPopularPlaceMap.put(count, currPopularPlaces);
                }
                
                //get only K ranks to return
                int rank = 1;
                for(Integer count: countPopularPlaceMap.keySet()){
                    if(rank<=k){
                        PopularPlace popularPlace = countPopularPlaceMap.get(count);
                        popularPlace.setRank(rank);
                        results.add(popularPlace);
                    }
                    rank++;
                }
                
            } else {    //JSON SERVICE
                ArrayList<JsonPopularPlace> allPlaces = new ArrayList<>();
                
                //filter all results into list allPlaces
                while(rs.next()){
                    int count = rs.getInt(1);
                    String semantic_place = rs.getString(2);
                    allPlaces.add(new JsonPopularPlace(0,semantic_place,count));
                }
                
                //loop allPlaces and get topK ranks
                ArrayList<JsonPopularPlace> topKPlaces = new ArrayList<>();
                int rank = 0;
                int previousCount = 0;
                for(JsonPopularPlace place : allPlaces){
                    
                    //first in list
                    if(rank == 0){
                        rank++;
                        previousCount = place.getCount();
                        place.setRank(rank);
                        topKPlaces.add(place);
                        continue;
                    } 
                    
                    //change in rank
                    if(place.getCount() != previousCount) {
                        rank++;
                        if(rank > k) {
                            break;
                        }
                    }
                    
                    previousCount = place.getCount();
                    place.setRank(rank);
                    topKPlaces.add(place);
                }
                
                //Sort the place which have the same rank in alphabetic order.
                Collections.sort(topKPlaces);
                for(JsonPopularPlace place: topKPlaces){
                    JSONObject obj = new JSONObject();
                    obj.put("rank",place.getRank());
                    obj.put("semantic-place", place.getSemantic_place());
                    obj.put("count", place.getCount());
                    results.add(obj);
                }
            }
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }finally{
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
              
        return results;
    }
    
    /**
     * Method checks the date parameter for missing, blank, validity
     * @param date the dateString
     * @return String error message, or null if valid
     */
    public String checkDate(String date){
//        System.out.println("i am date " + date);
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
     * Method checks the token parameter for missing, blank, validity
     * @param token token provided by user
     * @return String error message, or null if valid
     */
    public String checkToken(String token){
        if(token==null){
            return "missing token";
        } else if(token.isEmpty()) {
            return "blank token";
        } else if (!TokenValidator.checkToken(token)) {
            return "invalid token";
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
