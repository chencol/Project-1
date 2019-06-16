/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.JsonAuthentication;

import is203.JWTException;
import is203.JWTUtility;

/**
 * Class contains methods that validate tokens
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class TokenValidator {
    /**
     * Method checks a token if it is valid or invalid
     * @param token to be validated
     * @return boolean true if valid, false if invalid
     */
    public static boolean checkToken(String token){
        try {
            String username = JWTUtility.verify(token, TokenServlet.SHARED_SECRET);
            System.out.println("username " + username);
            if(username!=null){
                return true;
            }      
        } catch (JWTException | NullPointerException ex) {
            return false;
        }       
        return false;
    }
}
