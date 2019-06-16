
package is203.se.BasicLocationReports.topKCompanions;

import is203.se.JsonAuthentication.TokenValidator;
import is203.se.Validation.LocationValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
/**
 * Top-K companions JSON servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "TopKCompanionsJSONServlet", urlPatterns = {"/json/top-k-companions"})
public class TopKCompanionsJSONController extends HttpServlet {

    
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
//        System.out.println("getTopkCompanion: =======================");
        String kString = request.getParameter("k");
        int k = 3;
        String dateString = request.getParameter("date");
        String macAddress = request.getParameter("mac-address");
        String token = request.getParameter("token");
        
        //Setting response content type to JSON
        response.setContentType("application/json");
        
        //Response PrintWriter out
        PrintWriter out = response.getWriter();;     //Servlet should automatically close this stream
        
        //JSON to return
        JSONObject respJSON = new JSONObject();
        
        //validate
        //ArrayList<String> errorMessages = validateParams(token, kString, dateString, macAddress);
        ArrayList<String> errorMessages = validateParams(token, kString, dateString, macAddress);
        
        //if errors found, send error json        
        if (!errorMessages.isEmpty()) {
            respJSON.put("status", "error");
            JSONArray errorArray = new JSONArray();
            for(String errMsg : errorMessages) {
                errorArray.add(errMsg);
            }
            respJSON.put("messages", errorArray);
            out.print(respJSON.toJSONString());
            return;
        }
        
        //Params are valid, set K
        //deal with k
        if (kString != null) { //user has defined a value, set K as that
            k = Integer.parseInt(kString);
        }   //else K is default 3
        
        ArrayList<Companion> companions = TopKCompanionsManager.getTopKCompanions(dateString,macAddress, ""+k);
        JSONArray results = new JSONArray();
        for(Companion companion : companions) {
            JSONObject tempCompanion = new JSONObject();
            tempCompanion.put("rank", companion.getRank());
            tempCompanion.put("companion", companion.getEmail());
            tempCompanion.put("mac-address", companion.getMacAddress());
            tempCompanion.put("time-together", companion.getTimeTogether());
            results.add(tempCompanion);
        }
        respJSON.put("status", "success");
        respJSON.put("results", results);
        
        out.print(respJSON.toJSONString());
    }
    
    /**
     * This method validates request parameters
     * @param token
     * @param kString
     * @param dateString
     * @param macAddress
     * @return List of error messages
     */
    //public ArrayList<String> validateParams(String token, String kString, String dateString, String macAddress)
    public ArrayList<String> validateParams(String token, String kString, String dateString, String macAddress) {
        LocationValidator validator = new LocationValidator();
        
        ArrayList<String> errorMessages = new ArrayList<>();
        
        //validate token
        if(token==null){
            errorMessages.add("missing token");
        } else if (token.isEmpty()) {
            errorMessages.add("blank token");
        } else if (!TokenValidator.checkToken(token)) {
            errorMessages.add("invalid token");
        }
        
        //validate k
        try {
            
            if (kString != null) {
                
                int k = Integer.parseInt(kString);
                
                if (k <= 0 || k >= 11) {
                    errorMessages.add("invalid k");
                }
            }
            
        } catch(NumberFormatException ex) {
            errorMessages.add("invalid k");
        }
            
        //validate MAC-Address
        if(macAddress == null) {
            errorMessages.add("missing mac address");
        } else if (macAddress.isEmpty()) {
            errorMessages.add("blank mac address");
        } else if (!validator.checkMacAddress(macAddress)) {
            errorMessages.add("invalid mac address");
        }
        
        if(dateString == null) {
            errorMessages.add("missing date");
        } else if (dateString.isEmpty()) {
            errorMessages.add("blank date");
        } else if (!validator.checkTimestamp(dateString.replace('T', ' '))) {
            errorMessages.add("invalid date");
        }
        
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