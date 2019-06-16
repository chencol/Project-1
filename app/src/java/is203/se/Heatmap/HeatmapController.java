
package is203.se.Heatmap;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Heatmap servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */

@WebServlet(name = "HeatmapController", urlPatterns = {"/heatmap"})
public class HeatmapController extends HttpServlet {
    
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String floor = request.getParameter("floor");
        String date = request.getParameter("date");
        
        //deal with html form where datetime can have missing ss part
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}");
        if(datePattern.matcher(date).matches()) {
            date += ":00";
        }
        
        //filter out only location updates that refer to the floor given
        Map<String, LocationDensity> heatmap = HeatmapFactory.createHeatmap(floor, date);
        
        //return and forward to view
        request.setAttribute("heatmap", heatmap);
        RequestDispatcher view = request.getRequestDispatcher("heatmapView.jsp");
        view.forward(request, response);
    }
}
