/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.breakdown;

import is203.se.DAO.DemographicDAO;
import is203.se.DAO.LocationDAO;
import is203.se.Entity.Demographic;
import is203.se.Entity.Location;
import is203.se.JsonAuthentication.TokenValidator;
import is203.se.Validation.LocationValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
/**
 * Breakdown JSON servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "BreakdownJSONController", urlPatterns = {"/json/basic-loc-report"})
public class BreakdownJSONController extends HttpServlet {
    
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
        String date = request.getParameter("date");
        String order = request.getParameter("order");
        
        String[] options = new String[3];   //holds order selections in order, default null values if nothing is assigned. to be filled after validation of parameters
        
        //validate parameters
        ArrayList<String> errMsgs = validate(token, order, date, request, response);
        
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
        
        //Fill options[3] with values given by order parameter
        //options[] can only be filled after we are sure order parameter isnt null. just avoiding nullException
        String[] tempOrderArray = order.split(",");
        for(int i = 0; i < tempOrderArray.length; i++) {    //split will create exact array length. order=school,gender will be split into String[2].
            options[i] = tempOrderArray[i];                 //String[2] is harder to use, as trying to get [3] will throw an exception
        }                                                   //but with String[3], [3] is just null, which is just easier to use
        
        //get all demographic users in the time frame
        ArrayList<Demographic> users = getDemographicsInWindow(date);
        
        
        //iterate the breakdown levels based on how many options/orders user selected "school,year,gender" or other combos
        Breakdown level1Breakdown = null;
        
        //level1
        if(options[0] != null) {    //just in case, this basically can never be null if validation works properly
            level1Breakdown = processBreakdown(options[0], users);
        }
        
        //level2
        if(options[1] != null) {
            for(Content level1Content : level1Breakdown.getContents()) {    //get all contents of lvl1 breakdown
                Breakdown level2Breakdown = processBreakdown(options[1], level1Content.getUsersInvolved()); 
                level1Content.setBreakdown(level2Breakdown);    //for each Content in lvl1 Breakdown, add another breakdown lvl3
            }
        }
        
        //level3
        if(options[2] != null) {
            for(Content level1Content : level1Breakdown.getContents()) {    //get all contents of lvl1 breakdown
                Breakdown level2Breakdown = level1Content.getBreakdown();

                for(Content level2Content : level2Breakdown.getContents()) {    //get all contents of lvl2 breakdown
                    Breakdown level3Breakdown = processBreakdown(options[2], level2Content.getUsersInvolved());
                    level2Content.setBreakdown(level3Breakdown);    //for each Content of lvl2 Breakdown, add in breakdown lvl3
                }
            }
        }
        
        //unpack breakdown into JSON and print
        String jsonString = breakdownToJSON(level1Breakdown);
        out.print(jsonString);
    }
    
    /**
     * Method validates parameters and returns a list of errors found
     * @param token
     * @param order
     * @param date
     * @param request
     * @param response
     * @return list of errors, can be empty
     * @throws ServletException
     * @throws IOException 
     */
    public ArrayList<String> validate(String token, String order, String date, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        ArrayList<String> errMsgs = new ArrayList<>();
        
        //validate token
        if(token == null) {
            errMsgs.add("missing token");
        } else if (token.isEmpty()) {
            errMsgs.add("empty token");
        } else if (!tokenIsValid(token)) {
            errMsgs.add("invalid token");
        }
        
        //validate options/order
        if(order == null) {
            errMsgs.add("missing order");
        } else if (order.isEmpty()) {
            errMsgs.add("empty order");
        } else if (!orderIsValid(order)) {
            errMsgs.add("invalid order");
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
    public boolean tokenIsValid(String token) {
        return TokenValidator.checkToken(token);
    }
    
    /**
     * Method validates order
     * @param order
     * @return true if valid, false if not
     */
    public boolean orderIsValid(String order) {
        
        if(order == null) {   //order parameter wasnt written by user at all
            return false;
        }
        
        String[] optionsArray = new String[3];
        
        String[] tempOrderArray = order.split(",");
        for(int i = 0; i < tempOrderArray.length; i++) {
            optionsArray[i] = tempOrderArray[i];
        }
        
        String option1 = optionsArray[0];
        String option2 = optionsArray[1];
        String option3 = optionsArray[2];
        
        if(option1 == null) {   //there has gotta be at least 1 order selected
            return false;
        } else if (!(option1.equals("year") || option1.equals("gender") || option1.equals("school"))) {
            return false;
        }

        if(option2 != null) {   //user can select only one report
            if(!(option2.equals("year") || option2.equals("gender") || option2.equals("school"))) {
                return false;
            } else if(option2.equals(option1)) {
                return false;
            }
        }
        
        if(option3 != null) { //user can select only one report
            if(!(option3.equals("year") || option3.equals("gender") || option3.equals("school"))) {
                return false;
            } else if (option3.equals(option1) || option3.equals(option2)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Method validates date parameter
     * @param date
     * @return true if valid, false if not
     */
    public boolean dateIsValid(String date) {
        LocationValidator validator = new LocationValidator();
        if (date == null || (!validator.checkTimestamp(date.replace("T", " ")))) {
            return false;
        }
        return true;
    }
    
    /**
     * Method retrieves all Location updates within a timeframe starting from 15minutes before the given datetime inclusive to datetime exclusive
     * @param dateTime
     * @return list of unique Demographic users with valid MACaddresses
     */
    public ArrayList<Demographic> getDemographicsInWindow(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        
        //Processing window 
        Date endWindow = null;
        Date startWindow = new Date();
        try {
            endWindow = sdf.parse(dateTime.replace("T", " "));
            //Set startWindow to 15mins before given time
            startWindow.setTime(endWindow.getTime() - (1000 * 60 * 15));     //startTime is the endTime - 15mins, units in milliseconds
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        //get all location updates within a time window
        ArrayList<Location> allLocations = LocationDAO.getAllLocationsInWindow(sdf.format(startWindow), sdf.format(endWindow));
        
        //filter all location updates
        //Keep all unique Demographic users based on MACaddress in the Location object. keep only those with valid MAC addresses found in Demographic table
        HashMap<String, Demographic> uniqueUsers = new HashMap<>(); //K: MAC, V:Demographic object
        for(Location currLocation : allLocations) {
            String currMAC = currLocation.getMacAddress();
            Demographic currDemographic = DemographicDAO.getDemographic(currMAC);
            if(currDemographic != null) {
                uniqueUsers.put(currMAC, currDemographic);
            }
        }
        
        //put all Demographic user objects found into an arrayList
        ArrayList<Demographic> uniqueUserList = new ArrayList<>(uniqueUsers.values());
        return uniqueUserList;
    }
    
    /**
     *  method processes a list of users appropriately based on options
     * @param option
     * @param users
     * @return Breakdown
     */
    public Breakdown processBreakdown(String option, ArrayList<Demographic> users) {
        Breakdown breakdown = null;
        if(option.equals("year")) {
            breakdown = BreakdownManager.processYear(users);
        } else if (option.equals("gender")) {
            breakdown = BreakdownManager.processGender(users);
        } else if (option.equals("school")) {
            breakdown = BreakdownManager.processSchool(users);
        }
        
        return breakdown;
    }
    
    /**
     * method converts a Breakdown object to a JSON String
     * @param breakdown1
     * @return JSON String
     */
    public String breakdownToJSON(Breakdown breakdown1) {
        
        JSONObject result = new JSONObject();
        
        //set first level JSON objects
        result.put("status", "success");
        
        JSONArray array1 = new JSONArray();
        
        //loop all in 1st level 
        for(Content content1 : breakdown1.getContents()) {
            JSONObject json1 = new JSONObject();
            String header1 = content1.getHeader();
            //if header1 matches a number, set as integer, else set as String
            //this affect the value being surrounded by "" in the JSON
            json1.put(breakdown1.getType(), (header1.matches("^\\d+$") ? Integer.parseInt(header1) : header1));
            json1.put("count", content1.getCount());
            
            Breakdown breakdown2 = content1.getBreakdown();
            
            if(breakdown2 != null) {
                JSONArray array2 = new JSONArray();
                
                for(Content content2 : breakdown2.getContents()) {
                    JSONObject json2 = new JSONObject();
                    String header2 = content2.getHeader();
                    json2.put(breakdown2.getType(), (header2.matches("^\\d+$") ? Integer.parseInt(header2) : header2));
                    json2.put("count", content2.getCount());

                    Breakdown breakdown3 = content2.getBreakdown();
                    
                    if(breakdown3 != null) {
                        JSONArray array3 = new JSONArray();
                        
                        for(Content content3 : breakdown3.getContents()) {
                            JSONObject json3 = new JSONObject();
                            String header3 = content3.getHeader();
                            json3.put(breakdown3.getType(), (header3.matches("^\\d+$") ? Integer.parseInt(header3) : header3));
                            json3.put("count", content3.getCount());
                            array3.add(json3);
                        }
                        json2.put("breakdown", array3);
                    }
                    
                    array2.add(json2);
                }
                
                json1.put("breakdown", array2);
            }
            
            array1.add(json1);
        }
        
        result.put("breakdown", array1);
        return result.toJSONString();
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
