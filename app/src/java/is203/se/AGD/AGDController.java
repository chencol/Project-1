/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.AGD;

import is203.se.BasicLocationReports.locationChunk.LocationGroup;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for Automatic Group detection
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "AGDController", urlPatterns = {"/group_detect"})
public class AGDController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //Writer
        PrintWriter out = response.getWriter();
        
        //get parameters
        String dateString = request.getParameter("date");
        
        //Deal with weird html form thing where datetime can have missing ss part
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}");
        if(datePattern.matcher(dateString).matches()) {
            dateString += ":00";
        }
        
        //remove T in the dateString
        dateString = dateString.replace("T", " ");
        
        try {
            AGDManager manager = new AGDManager(dateString);
            
            int totalUsers = manager.getTotalUsers();
            ArrayList<LocationGroup> groupsFound = manager.getGroups();
            int totalGroups = groupsFound.size();
            
            //set attributes and forward to view
            request.setAttribute("total-users", totalUsers);
            request.setAttribute("total-groups", totalGroups);
            request.setAttribute("groups", groupsFound);
            
            RequestDispatcher view = request.getRequestDispatcher("AGDView.jsp");
            view.forward(request, response);
            
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        
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
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
