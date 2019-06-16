
package is203.se.DAO;

import is203.se.Connection.ConnectionFactory;
import is203.se.Connection.JDBCCloser;
import is203.se.Entity.LocationLookup;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class provides access to the data in the 'locationlookup' table in the database
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationLookupDAO {
    
    /**
     * Name of the Location Lookup DB table the DAO accesses
     */
    public static final String TABLE_NAME = "location_lookup";
    /**
     * Name of the column in the DB table that stores the locationID
     */
    public static final String LOCATION_ID_COL_NAME = "location_id";
    /**
     * Name of the column in the DB table that stores the semantic place
     */
    public static final String SEMANTIC_PLACE_COL_NAME = "semantic_place";
    /**
     * Array of column names in the order as found in the location lookup DB table
     */
    public static final String[] CSV_COL_ORDER = {LOCATION_ID_COL_NAME, SEMANTIC_PLACE_COL_NAME}; 
            
    /**
     * This method returns a LocationLookup object based on a specific location ID
     * 
     * @param locationID locationID to look for
     * @return LocationLookup or null if nothing is found
     */
    public static LocationLookup getLocationLookup(String locationID) {
        
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + LOCATION_ID_COL_NAME + "=?";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        LocationLookup locationLookup = null;
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            stmt.setString(1, locationID);
            
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                int locID = rs.getInt(LOCATION_ID_COL_NAME);
                String semanticPlace = rs.getString(SEMANTIC_PLACE_COL_NAME);
                locationLookup = new LocationLookup(locID, semanticPlace);
            }
        } catch (SQLException ex) {
            System.out.println("DAO ERROR: ");
            ex.printStackTrace();
            //TO-DO: LOGGER IMPLEMENTATION
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return locationLookup;
    }

    /**
     * This method drops recreates the 'LocationLookup' table in the database.
     */
    public static void dropTable() {
        
        Connection con = null;
        Statement stmt = null;
        
        //drop table
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
     * This method recreates the 'LocationLookup' table in the database.
     */
    public static void createTable() {
        
        Connection con = null;
        Statement stmt = null;
        
        //recreate the table
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                        + " location_id int not null,"
                        + " semantic_place varchar(40) not null,"
                        + " constraint pk PRIMARY KEY(location_id)"
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
     * Method inserts an Entity.LocationLookup Java object in the database
     * @param locUp 
     * @return rowsAffted. -1 if error
     */
    public static int insert(LocationLookup locUp) {
        String query = "INSERT INTO " + TABLE_NAME + " VALUES(?, ?)";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int rowsAffected = -1;
        
        try {
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            stmt.setInt(1, locUp.getLocationID());
            stmt.setString(2, locUp.getSemanticPlace());
            
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
     * Method returns all semantic places found in the DB
     * @return list of semantic places
     */
    public static ArrayList<String> getAllSemanticPlaces(){
        String query = "select distinct semantic_place from " + TABLE_NAME ;
        ArrayList<String> semanticPlaces = new ArrayList<String>(); 
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        
        try {
            con = ConnectionFactory.getConnection();            
            stmt = con.prepareStatement(query);
            
            rs = stmt.executeQuery();
            while(rs.next()){
                String semanticPlace = rs.getString(1);
                semanticPlaces.add(semanticPlace);
            }
            
        } catch (SQLException ex) {
            //TO-DO: LOGGER IMPLEMENTATION
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        return semanticPlaces;
    }
    
    /**
     * This method attempts to import a csv file into the location_lookup table 
     * @param csv
     * @return int number of rows affected
     */
    public static int loadDataInfile(File csv) {
        System.out.println("LOADING LOOKUP FILE");
        String query = "LOAD DATA LOCAL INFILE '" + csv.getAbsolutePath().replace("\\", "\\\\") + "'"
                        + " INTO TABLE " + TABLE_NAME
                        + " FIELDS TERMINATED BY ',' "
                        + " LINES TERMINATED BY '\\n'"
                        + "(location_id,semantic_place)";
        
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
    
    /**
     * Method returns all unique locationIDs
     * @return set of location IDs
     */
    public static HashSet<String> getAllLocationIDs() {
        String query = "select distinct " + LOCATION_ID_COL_NAME + " from " + TABLE_NAME ;
        HashSet<String> allLocationIDs = new HashSet<>(); 
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = ConnectionFactory.getConnection();            
            stmt = con.prepareStatement(query);
            
            rs = stmt.executeQuery();
            while(rs.next()){
                String locationID = rs.getString(1);
                allLocationIDs.add(locationID);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        return allLocationIDs;
    }
}
