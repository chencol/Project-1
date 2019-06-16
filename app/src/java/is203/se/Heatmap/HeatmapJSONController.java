package is203.se.Heatmap;

import is203.se.Entity.Location;
import is203.se.JsonAuthentication.TokenValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Heatmap JSON servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "HeatmapJSONController", urlPatterns = {"/json/heatmap"})
public class HeatmapJSONController extends HttpServlet{
    
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
        
        String token = request.getParameter("token");
        String floor = request.getParameter("floor");
        String date = request.getParameter("date");
        
        //validate parameters
        ArrayList<String> errorMessages = validateParameters(token, floor, date);
        
        //return JSON response if errors are found
        if(errorMessages.size() > 0) { //errors found
            JSONArray errorMessagesJSON = new JSONArray();
            for(String err : errorMessages) {
                errorMessagesJSON.add(err);
            }
            
            JSONObject respJSON = new JSONObject();
            respJSON.put("status", "error");
            respJSON.put("messages", errorMessagesJSON);
            
            out.print(respJSON.toJSONString());
            return; //stops subsequence code from running
            
        }
        
        //create heatmap
        Map<String, LocationDensity> heatmap = HeatmapFactory.createHeatmap(floor, date);
        
        //sort semantic place keys alphabetically
        ArrayList<String> sortedHeatmapKeys = new ArrayList<>(heatmap.keySet());
        Collections.sort(sortedHeatmapKeys);
        
        //return successful JSON response
        JSONArray heatmapJSONArray = new JSONArray();
        for(String currSemanticPlace : sortedHeatmapKeys) {
            LocationDensity currLocationDensity = heatmap.get(currSemanticPlace);
            
            JSONObject obj = new JSONObject();
            obj.put("semantic-place", currSemanticPlace);
            obj.put("num-people", currLocationDensity.getNumberOfPeople());
            obj.put("crowd-density", currLocationDensity.getDensity());
            heatmapJSONArray.add(obj);
        }
        JSONObject respJSON = new JSONObject();
        respJSON.put("status", "success");
        respJSON.put("heatmap", heatmapJSONArray);
        out.print(respJSON.toJSONString());
        return;
    }
    
    /**
     * Method validates parameters token, floor and date
     * @param token
     * @param floor
     * @param date
     * @return list of error messages, empty if no errors found
     */
    protected ArrayList<String> validateParameters(String token, String floor, String date) {
        
        ArrayList<String> errorMessages = new ArrayList<>();
        
        //validate token
        if(token == null) {
            errorMessages.add("token is missing");
        } else if (token.isEmpty()){
            errorMessages.add("blank token");
        } else if (!TokenValidator.checkToken(token)) {
            errorMessages.add("invalid token");
        }
        
        //validate floor
        if(floor == null) {
            errorMessages.add("floor is missing");
        } else if (floor.isEmpty()) {
            errorMessages.add("blank floor");
        } else if (Integer.parseInt(floor) > 5 || Integer.parseInt(floor) < 0) {
            errorMessages.add("invalid floor");
        }
        
        //validate date
        if(date == null) {
            errorMessages.add("date is missing");
        } else if (date.isEmpty()) {
            errorMessages.add("blank date");
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
                sdf.parse(date.replace('T', ' '));
            } catch (ParseException ex) {
                errorMessages.add("invalid date");
            }
        }
        
        //sort error messages alphabetically
        Collections.sort(errorMessages);
        return errorMessages;
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