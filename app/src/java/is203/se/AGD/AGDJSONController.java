/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.AGD;

import is203.se.BasicLocationReports.locationChunk.LocationGroup;
import is203.se.Entity.Demographic;
import is203.se.JsonAuthentication.TokenValidator;
import is203.se.Validation.LocationValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * AGD JSON Servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "AGDJSONController", urlPatterns = {"/json/group_detect"})
public class AGDJSONController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        
        //appeears in the response header, so browsers or whatever knows to process response body as JSON
        response.setContentType("application/json");

        //get parameters passed from the GET request
        String token = request.getParameter("token");
        String date = request.getParameter("date").replace("T", " ");
        
        //validate parameters
        ArrayList<String> errMsgs = validate(token, date);
        
        //if errors found, send err msgs
        if(!errMsgs.isEmpty()) { //request came from JSON api
            
            //put errors into JSONArray
            JSONArray errArray = new JSONArray();
            for(String errMsg : errMsgs) {
                errArray.add(errMsg);
            }
            
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("status", "error");
            resultJSON.put("messages", errArray);
            out.print(resultJSON.toJSONString());
            return;
        }
        
        //no errors
        try {
            AGDManager manager = new AGDManager(date);
        
            int totalUsers = manager.getTotalUsers();
            ArrayList<LocationGroup> groupsFound = manager.getGroups();
            int totalGroups = groupsFound.size();
            
            //create JSON
            JSONObject respJSON = new JSONObject();
            respJSON.put("status", "success");
            respJSON.put("total-users", totalUsers);
            respJSON.put("total-groups", totalGroups);
            
            //loop all groups
            JSONArray groups = new JSONArray();
            for(LocationGroup currGroup : groupsFound) {
                int size = currGroup.getSize();
                int totalTimeSpent = currGroup.getTotalTimeSpent();
                
                //loop all members in the currGroup
                JSONArray members = new JSONArray();
                for(Demographic currDemo : currGroup.getMembers()) {
                    String email = currDemo.getEmail();
                    String mac = currDemo.getMacAddress();
                    
                    JSONObject member = new JSONObject();
                    member.put("email", email);
                    member.put("mac-address", mac);
                    
                    members.add(member);
                }
                
                //loop all locations in the currGroup
                JSONArray locations = new JSONArray();
                for(Entry<Integer, Long> entrySet : currGroup.getLocations().entrySet()) {
                    String locationID = ""+entrySet.getKey();
                    long timeSpent = entrySet.getValue();
                    
                    JSONObject location = new JSONObject();
                    location.put("location", locationID);
                    location.put("time-spent", timeSpent);
                    
                    locations.add(location);
                }
                
                JSONObject group = new JSONObject();
                group.put("size", size);
                group.put("total-time-spent", totalTimeSpent);
                group.put("members", members);
                group.put("locations", locations);
                groups.add(group);
            }
            
            respJSON.put("groups", groups);
            
            //send json response
            out.print(respJSON.toJSONString());
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Method validates token and date parameters
     * @param token
     * @param date
     * @return list of error messages, empty list if all valid
     */
    protected ArrayList<String> validate(String token, String date) {
        ArrayList<String> errMsgs = new ArrayList<>();
        
        //validate token
        if(token == null) {
            errMsgs.add("missing token");
        } else if (token.isEmpty()) {
            errMsgs.add("empty token");
        } else if (!tokenIsValid(token)) {
            errMsgs.add("invalid token");
        }
        
        //validate date
        if(date == null) {
            errMsgs.add("missing date");
        } else if (date.isEmpty()) {
            errMsgs.add("empty date");
        } else if (!dateIsValid(date)) {
            errMsgs.add("invalid date");
        }
        
        return errMsgs;
    }
    
    /**
     * Method validates token
     * @param token
     * @return true if valid, false if not
     */
    protected boolean tokenIsValid(String token) {
        return TokenValidator.checkToken(token);
    }
    
    /**
     * Method validates date parameter
     * @param date
     * @return true if valid, false if not
     */
    protected boolean dateIsValid(String date) {
        LocationValidator validator = new LocationValidator();
        if(date == null || (!validator.checkTimestamp(date))) {
            return false;
        }
        return true;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
    }// </editor-fold>

}
