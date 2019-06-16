package is203.se.BasicLocationReports.breakdown;

import is203.se.DAO.DemographicDAO;
import is203.se.DAO.LocationDAO;
import is203.se.Entity.Demographic;
import is203.se.Entity.Location;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Breakdown servlet
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "BreakdownController", urlPatterns = {"/basic-loc-report"})
public class BreakdownController extends HttpServlet {
    
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String reportType = request.getParameter("type");
//        System.out.println("type: " + reportType);
        
        if(reportType.equals("Basic")) {
            simpleOutput(request, response);
        } else {
            advancedOutput(request, response);
        }
        
    }
    
    /**
     * Method processes the form for Breakdown Simple and forwards to BreakdownSimpleView.jsp
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    public void simpleOutput(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get parameters passed from the GET request
        String date = request.getParameter("date");
        String option1 = request.getParameter("option1");
        String option2 = request.getParameter("option2");
        String option3 = request.getParameter("option3");
        
        //Deal with weird html form thing where datetime can have missing ss part
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}");
        if(datePattern.matcher(date).matches()) {
            date += ":00";
        }
        
        //get all demographic users in the time frame
        ArrayList<Demographic> users = getDemographicsInWindow(date);
        int totalUsers = users.size();
        
        //iterate the breakdown levels based on how many options/orders user selected "school,year,gender" or other combos
        
        Breakdown breakdown1 = null;
        //level1
        if(option1 != null && !option1.equals("none")) {    //just in case, this basically can never be null if validation works properly
            breakdown1 = processBreakdown(option1, users);
        }
        
        //level2
        Breakdown breakdown2 = null;
        if(option2 != null && !option2.equals("none")) {
            breakdown2 = processBreakdown(option2, users); 
        }
        
        //level3
        Breakdown breakdown3 = null;
        if(option3 != null && !option3.equals("none")) {
            breakdown3 = processBreakdown(option3, users);
        }
        
        //set request attributes, forward to BreakdownView.jsp
        request.setAttribute("totalNumUsers", totalUsers);
        request.setAttribute("breakdown1", breakdown1);
        request.setAttribute("breakdown2", breakdown2);
        request.setAttribute("breakdown3", breakdown3);
        RequestDispatcher view = request.getRequestDispatcher("breakdownSimpleView.jsp");
        view.forward(request, response);
    }
    
    /**
     * Method processes the form for Breakdown Advanced and forwards to BreakdownAdvancedView.jsp
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    public void advancedOutput(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //get parameters passed from the GET request
        String date = request.getParameter("date");
        String option1 = request.getParameter("option1");
        String option2 = request.getParameter("option2");
        String option3 = request.getParameter("option3");
        
        //Deal with weird html form thing where datetime can have missing ss part
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}");
        if(datePattern.matcher(date).matches()) {
            date += ":00";
        }
        
        //get all demographic users in the time frame
        ArrayList<Demographic> users = getDemographicsInWindow(date);
        int totalUsers = users.size();
        
        //iterate the breakdown levels based on how many options/orders user selected "school,year,gender" or other combos
        ArrayList<String> types = new ArrayList<>();    //for view to use, remember type = school, gender, year
        Breakdown level1Breakdown = null;
        
        //level1
        if(option1 != null && !option1.equals("none")) {    //just in case, this basically can never be null if validation works properly
            level1Breakdown = processBreakdown(option1, users);
            types.add(option1);
//            System.out.println("Setting type1: " + option1);
        }
        
        //level2
        if(option2 != null && !option2.equals("none")) {
            for(Content level1Content : level1Breakdown.getContents()) {    //get all contents of lvl1 breakdown
                Breakdown level2Breakdown = processBreakdown(option2, level1Content.getUsersInvolved()); 
                level1Content.setBreakdown(level2Breakdown);    //for each Content in lvl1 Breakdown, add another breakdown lvl3
            }
            types.add(option2);
//            System.out.println("Setting type2: " + option2);
        }
        
        //level3
        if(option3 != null && !option3.equals("none")) {
            for(Content level1Content : level1Breakdown.getContents()) {    //get all contents of lvl1 breakdown
                Breakdown level2Breakdown = level1Content.getBreakdown();

                for(Content level2Content : level2Breakdown.getContents()) {    //get all contents of lvl2 breakdown
                    Breakdown level3Breakdown = processBreakdown(option3, level2Content.getUsersInvolved());
                    level2Content.setBreakdown(level3Breakdown);    //for each Content of lvl2 Breakdown, add in breakdown lvl3
                }
            }
            types.add(option3);
//            System.out.println("Setting type3: " + option3);
        }
        
        //set request attributes, forward to BreakdownView.jsp
        request.setAttribute("totalNumUsers", totalUsers);
        request.setAttribute("breakdown", level1Breakdown);
        request.setAttribute("types", types);
        RequestDispatcher view = request.getRequestDispatcher("breakdownAdvancedView.jsp");
        view.forward(request, response);
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
        for(Demographic demo : uniqueUserList) {
//            System.out.println(demo.getEmail() + demo.getYear() + demo.getSchool());
        }
        return uniqueUserList;
    }
    
    /**
     * method processes a list of users appropriately based on options
     * @param option year, gender, school
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
}