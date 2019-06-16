/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.Bootstrap;

import is203.se.JsonAuthentication.TokenValidator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Servlet for Bootstrap Update
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "BootstrapUpdateServlet", urlPatterns = {"/json/update"})
@MultipartConfig
public class BootstrapUpdateServlet extends HttpServlet {

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
        //absolute path of application
        String applicationPath = request.getServletContext().getRealPath("");

        //upload file path
        String uploadFolderPath = applicationPath + "uploads";
        
        //Setting response content type to JSON
        response.setContentType("application/json");
        
        //Response PrintWriter out
        PrintWriter out = response.getWriter(); 
        
        //verify token
        String token = request.getParameter("token");
        String tokenError = "";
        if(token == null) {
            tokenError = "missing token";
        } else if (token.isEmpty()){
            tokenError = "blank token";
        } else if (!TokenValidator.checkToken(token)) {
            tokenError = "invalid token";
        }
        
        if(!tokenError.isEmpty()) {
            JSONArray messages = new JSONArray();
            messages.add(tokenError);
            JSONObject respJSON = new JSONObject();
            respJSON.put("status", "error");
            respJSON.put("messages", messages);
            out.print(respJSON.toJSONString());
        }
        
        //ensure /temp folder exists
        File tempFolder = new File(uploadFolderPath);
        if(!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        
        //Retrieve Part, aka the zip file. 
        Part zipPart = request.getPart("bootstrap-file");     //name="bootstrap-file" in <form> back in view page
        String zipFileName = BootstrapManager.getFileName(zipPart);

        zipPart.write(uploadFolderPath + File.separator + zipFileName);         //Relative to location attribute in @MultipartConfig, which matches /temp
        
        //Pointing to actual zip.zip
        File zipFile = new File(uploadFolderPath + File.separator + zipFileName);
        
        //unzip file
        ArrayList<File> unzippedFiles = BootstrapManager.unzipFile(zipFile);
        
        //Assign unzipped files to thier variables
        File demographicCSV = null;
        File locationCSV = null;
        for(File csv : unzippedFiles) {
            
            String fileName = csv.getName();
            if(fileName.equals("demographics.csv")) {
                demographicCSV = csv;
            }
            if (fileName.equals("location.csv")) {
                locationCSV = csv;
            }
        }
        
        //System.out.println("DEMO PATH: " + demographicCSV.getAbsolutePath());
        //System.out.println("LOC PATH: " + locationCSV.getAbsolutePath());
        
        //Import CSVs
        BootstrapResult demographicResult = null;
        BootstrapResult locationResult = null;
        try {
            
            if(demographicCSV != null) {
                demographicResult = BootstrapManager.importAdditionalDemographicCSV(demographicCSV);
            }
            
            if(locationCSV != null) {
                locationResult = BootstrapManager.importAdditionalLocationCSV(locationCSV);
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException | IOException | ParseException ex) {
            ex.printStackTrace();
        }
        
        //Generate JSONObject response
        JSONObject JSONret = new JSONObject();
        
        //Putting number of successfully loaded records into final JSONObject
        JSONArray numRecordLoaded = BootstrapResult.totalNumRecordLoaded(demographicResult, null, locationResult);

        //Put error messages into final JSONObject
        JSONArray error = BootstrapResult.totalErrors(demographicResult, null, locationResult);

        if(error.isEmpty()) {   //all rows in csv were found to be valid
            JSONret.put("status", "success");
            JSONret.put("num-record-loaded", numRecordLoaded);
        } else {    //invalid rows were found in csv
            JSONret.put("status", "error"); 
            JSONret.put("num-record-loaded", numRecordLoaded);
            JSONret.put("error", error);
        }
        
        //delete zipfile and tempFolder
        //zipFile.delete();
        //ssBootstrapManager.deleteFile(tempFolder);
        
        //return response depending on where request came from, if bootstrapView the button value will be set
        if(request.getParameter("button") != null) {    //request came from bootstrapView.jsp
            request.setAttribute("result", JSONret);
            RequestDispatcher view = request.getRequestDispatcher("/bootstrapView.jsp");
            view.forward(request, response);
        } else {    //request came from someone trying to use REST API
            out.print(JSONret.toJSONString());
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
    }// </editor-fold>

}
