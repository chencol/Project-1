/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.JsonAuthentication;

import is203.JWTUtility;
import is203.se.DAO.DemographicDAO;
import is203.se.Entity.Demographic;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * TokenServlet that handles JSON authentication requests
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
@WebServlet(name = "TokenServlet", urlPatterns = {"/json/authenticate"})
public class TokenServlet extends HttpServlet {
    /**
     * Shared secret that is used to generate tokens
     */
    public static final String SHARED_SECRET = "ThisStringIs16ch";
    
    /**
     * Method handles get and post requests to the servlet
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()){
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
//            System.out.println(username);
//            System.out.println(password);
            
            //check if params are missing or blank
            String usernameErrorMsg = checkUsername(username);
            String passwordErrorMsg = checkPassword(password);
            
            ArrayList<String> errorMesssages = new ArrayList<>();
            if(usernameErrorMsg != null) {
                errorMesssages.add(usernameErrorMsg);
            }
            if(passwordErrorMsg != null) {
                errorMesssages.add(passwordErrorMsg);
            }
            JSONArray errorJSON = new JSONArray();
            //check if username and password exists in DB
            if(errorMesssages.isEmpty()) {
                
                if(!checkValidUser(username, password)) {   //invalid login
                    JSONObject respJSON = new JSONObject();
                    //errorMesssages.add("invalid username/password");
                    System.out.println("invalid username/password");
                    respJSON.put("status", "error");
                    errorJSON.add("invalid username/password");
                    respJSON.put("messages",errorJSON );
                    out.println(respJSON);
                } else {    //valid login
                    
                    String token = JWTUtility.sign(SHARED_SECRET, username);
            
                    JSONObject respJSON = new JSONObject();
                    respJSON.put("status", "success");
                    respJSON.put("token", token);
                    
                    out.print(respJSON.toJSONString());
                    return; //stop the rest of te code from running
                }
            } else { //errors found
                //JSONArray errorJSON = new JSONArray();
                
                //sort arrayList by alphabatical order
                Collections.sort(errorMesssages);
                for(String err : errorMesssages) {
                    errorJSON.add(err);
                }
                
                JSONObject respJSON = new JSONObject();
                respJSON.put("status", "error");
                respJSON.put("messages", errorJSON);
                
                out.print(respJSON.toJSONString());
                return;
            }
            
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /**
     * Method checks if a username is missing or blank.
     * @param username
     * @return error message, or null if valid String
     */
    public String checkUsername(String username){
        if(username==null){
            return "username is missing";
        } else if(username.length()==0){
            return "username is blank";
        }
        return null;
    }
    
    /**
     * Method checks if a password is missing or blank.
     * @param password
     * @return error message, or null if valid String
     */
    public String checkPassword(String password){
        if(password==null) {
            return "password is missing";
        } else if(password.length() == 0) {
            return "password is blank";
        }
        return null;
    }
    /**
     * Method checks if a username and password combination is a valid combination that can be found in the DB
     * @param username
     * @param password
     * @return boolean, true if valid, false if invalid
     */
    public Boolean checkValidUser(String username, String password) {
        
        if(username.equals("admin") && password.equals("admin")) {
            return true;
        } else {
            Demographic user = DemographicDAO.getDemographicByUsername(username, password);
            if(user == null) {
                return false;
            }
        } 
        return true;
    }

}
