/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.topKPlaces;

import is203.se.Connection.ConnectionFactory;
import is203.se.Connection.JDBCCloser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Class checks parameter validity
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class ParameterChecker{
    
    /**
     * Method checks validity of rank field
     * @param rank
     * @return true if valid, false if not
     */
    public static boolean checkK(String rank){
        //Check null value
        if(rank!=null){
            try{
                int k = Integer.parseInt(rank);
                /*
                Check whether value of k is in the required scope
                */
                if(k>=1&&k<=10){
                    return true;
                }
            }
            catch(Exception e){
                return false;
            }
        }
        return false;
    }
    
    /**
     * Method checks validity of date field
     * @param dateString
     * @return true if valid, false if not
     */
    public static boolean checkDateString(String dateString){
        
        //Deal with weird html form thing where datetime can have missing ss part
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}");
        if(datePattern.matcher(dateString).matches()) {
            dateString += ":00";
        }
        
        if(dateString!=null){
            try{
                Date date = getDateByString(dateString);
            }
            catch(Exception e){
                return false;
            }
        }
        
        return true;
    }
    

    /**
     * Method convert the dateString to Date object
     * @param dateString
     * @return Date object
     * @throws ParseException
     */

    public static Date getDateByString(String dateString) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");    
        //Check whether the date contains illgal charcter or weird number.
        format.setLenient(false);
        Date d1 =format.parse(dateString);
        return d1;
    }
    
    /**
     * Method check if the semanticPlace is valid or not
     * @param semanticPlace
     * @return true if valid false if invalid
     */
    public static boolean checkValidSemanticPlace(String semanticPlace){
        if(semanticPlace!=null){
            String query = "SELECT semantic_place from location_lookup where semantic_place = ?";
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {

                con = ConnectionFactory.getConnection();
                stmt = con.prepareStatement(query);

                stmt.setString(1, semanticPlace);

                rs = stmt.executeQuery();

                if(rs.next()) {
                     return true;
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                JDBCCloser.close(rs);
                JDBCCloser.close(stmt);
                JDBCCloser.close(con);
            }

            return false;
            
        }
        
        return false;
        
    }
}
