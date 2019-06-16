
package is203.se.BasicLocationReports.topKCompanions;

import is203.se.DAO.LocationDAO;
import is203.se.Validation.LocationValidator;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;

/**
 * Top-K Companions servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "TopKCompanionsServlet", urlPatterns = {"/top-k-companions"})
public class TopKCompanionsController extends HttpServlet {

    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String kString = request.getParameter("k");
        int k = 3;
        String dateString = request.getParameter("date");
        String macAddress = request.getParameter("macAddress");
       
        //System.out.println("K: " + k + " date: " + dateString + " mac: " + macAddress);
        
        //Response PrintWriter out
        PrintWriter out = response.getWriter();;     //Servlet should automatically close this stream
        
        //Deal with weird html form thing where datetime can have missing ss part
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}");
        if(datePattern.matcher(dateString).matches()) {
            dateString += ":00";
        }
        
        //remove T in the dateString
        dateString = dateString.replace("T", " ");
        
        //validate
        ArrayList<String> errorMessages = validateParams(macAddress);
        
        //if errors found, return error to view
        if (errorMessages.isEmpty()) {
            ArrayList<Companion> companions = TopKCompanionsManager.getTopKCompanions(dateString, macAddress, kString);
            request.setAttribute("companions", companions);
            RequestDispatcher view = request.getRequestDispatcher("TopKCompanionView.jsp");
            view.forward(request, response);
        } else {
            RequestDispatcher view = request.getRequestDispatcher("TopKCompanionView.jsp");
            request.setAttribute("errMsgs", errorMessages);
            view.forward(request, response);
        }
    }
    
    /**
     * This method validates request parameters
     * @param macAddress
     * @return List of error messages
     */
    public ArrayList<String> validateParams(String macAddress) {
        LocationValidator validator = new LocationValidator();

        ArrayList<String> errorMessages = new ArrayList<>();
        
        if(macAddress == null) {
            errorMessages.add("missing mac address");
        } else if (macAddress.trim().isEmpty()) {
            errorMessages.add("empty mac address");
        } else if (!LocationDAO.macAddressExists(macAddress)) {
            errorMessages.add("invalid mac address");
        }
        
        return errorMessages;
    }

}