//TO-DO: LOGGER IMPLEMENTATION

package is203.se.DAO;

import is203.se.Connection.ConnectionFactory;
import is203.se.Connection.JDBCCloser;
import is203.se.Entity.Demographic;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class provides access to the data in the 'demographic' table in the database
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class DemographicDAO {
    
    /**
     * Name of the demographic DB table the DAO accesses
     */
    public static final String TABLE_NAME = "demographic";
    /**
     * Name of the column in the DB table that stores the MAC-Address
     */
    public static final String MAC_ADDRESS_COL_NAME = "mac_address";
    /**
     * Name of the column in the DB table that stores the user's name
     */
    public static final String NAME_COL_NAME = "name";
    /**
     * Name of the column in the DB table that stores the user's password
     */
    public static final String PASSWORD_COL_NAME = "password";
    /**
     * Name of the column in the DB table that stores the user's email
     */
    public static final String EMAIL_COL_NAME = "email";
    /**
     * Name of the column in the DB table that stores the user's gender
     */
    public static final String GENDER_COL_NAME = "gender";
    /**
     * Array of column names in the order as found in the demographic DB table
     */
    public static final String[] CSV_COL_ORDER = {MAC_ADDRESS_COL_NAME, NAME_COL_NAME, PASSWORD_COL_NAME, EMAIL_COL_NAME, GENDER_COL_NAME};
    
    
    /**
     * This method returns a Demographic object based on a specific username and password combination
     * @param username e.g john.2016
     * @param password
     * @return Demographic or null if nothing is found
     */
    
    public static Demographic getDemographicByUsername(String username, String password) {
        System.out.println("I am inside1");
        String query = "SELECT * FROM " + TABLE_NAME 
                        + " WHERE " + EMAIL_COL_NAME + " like ? " 
                        + " AND " + PASSWORD_COL_NAME + "= BINARY ? ";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        Demographic user = null;
        System.out.println("username " + username);
        System.out.println("password " + password);
        try {
            
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            //In case user key in the first few character of the username;
            username = username + "@"; 
            stmt.setString(1, username + "%");
            stmt.setString(2, password);
            
            rs = stmt.executeQuery();
            System.out.println("I am inside2");
            if(rs.next()) {
                System.out.println("I find it");
                String macAddress = rs.getString(MAC_ADDRESS_COL_NAME);
                String name = rs.getString(NAME_COL_NAME);
                String pwd = rs.getString(PASSWORD_COL_NAME);
                String email = rs.getString(EMAIL_COL_NAME);
                char gender = rs.getString(GENDER_COL_NAME).charAt(0);
                int adminStatus = Demographic.ADMIN_FALSE;

                user = new Demographic(macAddress, name, password, email, gender, adminStatus);
            }   
            
        } catch (SQLException ex) {
            //TO-DO: LOGGER IMPLEMENTATION
            System.out.println(ex);
            System.out.println("I got caught here");
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return user;
    }
    
    
    /**
     * This method returns a Demographic object based on a specific username and password combination
     * @param username
     * @param password
     * @return Demographic or null if nothing is found
     */
    public static Demographic getDemographic(String username, String password) {
        String query = "SELECT * FROM " + TABLE_NAME 
                        + " WHERE " + EMAIL_COL_NAME + "= ? " 
                        + " AND " + PASSWORD_COL_NAME + "= BINARY ? ";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        Demographic user = null;
        
        try {
            
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                String macAddress = rs.getString(MAC_ADDRESS_COL_NAME);
                String name = rs.getString(NAME_COL_NAME);
                String pwd = rs.getString(PASSWORD_COL_NAME);
                String email = rs.getString(EMAIL_COL_NAME);
                char gender = rs.getString(GENDER_COL_NAME).charAt(0);
                int adminStatus = Demographic.ADMIN_FALSE;

                user = new Demographic(macAddress, name, password, email, gender, adminStatus);
            }   
            
        } catch (SQLException ex) {
            //TO-DO: LOGGER IMPLEMENTATION
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return user;
    }
    
    //assumes mac address is a unique identifier pk
    /**
     * This method returns a Demographic object based on a specific MACAddress
     * @param macAddress
     * @return Demographic or null if nothing is found
     */
    public static Demographic getDemographic(String macAddress) {
        String query = "SELECT * FROM " + TABLE_NAME 
                        + " WHERE " + MAC_ADDRESS_COL_NAME + "=?";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        Demographic user = null;
        
        try {
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            stmt.setString(1, macAddress);
            
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                String MACAddress = rs.getString(MAC_ADDRESS_COL_NAME);
                String name = rs.getString(NAME_COL_NAME);
                String pwd = rs.getString(PASSWORD_COL_NAME);
                String email = rs.getString(EMAIL_COL_NAME);
                char gender = rs.getString(GENDER_COL_NAME).charAt(0);
                int adminStatus = Demographic.ADMIN_FALSE;

                user = new Demographic(MACAddress, name, pwd, email, gender, adminStatus);
            }   
            
        } catch (SQLException ex) {
            //TO-DO: LOGGER IMPLEMENTATION
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return user;    
    }
    
    
    /**
     * This method drops 'userinfo' table in the database
     */
    public static void dropTable() {
        Connection con = null;
        Statement stmt = null;
        
        //Drop table
        String query = "DROP TABLE " + TABLE_NAME;
        
        try {
            con = ConnectionFactory.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            
        } catch(SQLException ex) { 
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
    }
    
    /**
     * This method recreates demographic table in the DB
     */
    public static void createTable() {
        Connection con = null;
        Statement stmt = null;
        
        //recreate table
        String query = "create table " + TABLE_NAME + " ("
                    + " mac_address varchar(40) not null,"
                    + " name    varchar(50) not null,"
                    + " password varchar(15),"
                    + "  email	varchar(60) not null,"
                    + " gender	char(1) not null,"
                    + " constraint pk PRIMARY KEY(mac_address)"
                + ")" + " ENGINE=MYISAM";;
        
        try {
            con = ConnectionFactory.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(query);
            
        } catch(SQLException ex) { 
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
    }
    
    /**
     * This method inserts a Demographic object into the database
     * @param demographic
     * @return rowsAffted. -1 if error
     */
    public static int insert(Demographic demographic) {
        String query = "INSERT INTO " + TABLE_NAME + " VALUES(?, ?, ?, ?, ?)";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int rowsAffected = -1;
        
        try {
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            stmt.setString(1, demographic.getMacAddress());
            stmt.setString(2, demographic.getName());
            stmt.setString(3, demographic.getPassword());
            stmt.setString(4, demographic.getEmail());
            stmt.setString(5, demographic.getGender() + "");
            
            rowsAffected = stmt.executeUpdate();
            
        } catch (SQLException ex) {
            //TO-DO: LOGGER IMPLEMENTATION
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        return rowsAffected;
    }
    
    /**
     * Method returns all unique MAC-Addresses found in the demographic DB table
     * @return list of MAC-Addresses
     */
    
    public static ArrayList<String> getAllUsersMacAddress(){
        String query = "select mac_address from " + TABLE_NAME;
        ArrayList<String> macAddresses = new ArrayList<String>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            
            rs = stmt.executeQuery();
            
            while(rs.next()){
                String macAddress = rs.getString(1);
                macAddresses.add(macAddress);
            }
            
        } catch (SQLException ex) {
            //TO-DO: LOGGER IMPLEMENTATION
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        return macAddresses;
    }
    
    /**
     * This method attempts to import a csv file into the demographic table 
     * @param csv
     * @return int number of rows affected
     */
    public static int loadDataInfile(File csv) {
        System.out.println("LOADING DEMOGRAPHIC FILE");
        String query = "LOAD DATA LOCAL INFILE '" + csv.getAbsolutePath().replace("\\", "\\\\") + "'"
                        + " INTO TABLE " + TABLE_NAME
                        + " FIELDS TERMINATED BY ',' "
                        + " LINES TERMINATED BY '\\n'"
                        + "(mac_address,name,password,email,gender)";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int rowsAffected = -1;
        
        try {
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            
            rowsAffected = stmt.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        if(rowsAffected == -1){
            rowsAffected = 0;
        }
        
        return rowsAffected;
    }
}
