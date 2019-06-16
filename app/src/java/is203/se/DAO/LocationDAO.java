
package is203.se.DAO;

import is203.se.Connection.ConnectionFactory;
import is203.se.Connection.JDBCCloser;
import is203.se.Entity.Location;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class provides access to the data in the 'Location' table in the database
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationDAO {
    
    /**
     * Name of the Location DB table the DAO accesses
     */
    public static final String TABLE_NAME = "location";
    /**
     * Name of the column in the DB table that stores the timestamp
     */
    public static final String TIMESTAMP_COL_NAME = "timestamp";
    /**
     * Name of the column in the DB table that stores the MAC-Address
     */
    public static final String MACADDRESS_COL_NAME = "mac_address";
    /**
     * Name of the column in the DB table that stores the locationID
     */
    public static final String LOCATION_ID_COL_NAME = "location_id";
    /**
     * Array of column names in the order as found in the location DB table
     */
    public static final String[] CSV_COL_ORDER = {TIMESTAMP_COL_NAME, MACADDRESS_COL_NAME, LOCATION_ID_COL_NAME};
    private static final SimpleDateFormat sdf = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
    
    /**
     * This method finds a specific location based on a specific timestamp and macAddress
     * @param timestamp
     * @param macAddress
     * @return Location or null if nothing is found
     */
    public static Location getLocation(String timestamp, String macAddress) {
        
        String query = "SELECT * FROM " + TABLE_NAME 
                        + " WHERE " + TIMESTAMP_COL_NAME + "= ?" 
                        + " AND " + MACADDRESS_COL_NAME + "= ?";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        Location location = null;
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            
            stmt.setString(1, timestamp);
            stmt.setString(2, macAddress);
            
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                int locationID = rs.getInt(LOCATION_ID_COL_NAME);
                location = new Location(timestamp, macAddress, locationID);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return location;    //null if nothing is found
        
    }
    
    /**
     * Method checks if a MAC-Address exists in the Location table.
     * @param macAddress
     * @return true if found, false if not
     */
    public static boolean macAddressExists(String macAddress) {
                
        String query = "SELECT * FROM " + TABLE_NAME 
                        + " WHERE " + MACADDRESS_COL_NAME + "= ?";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean userFound = false;
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            
            stmt.setString(1, macAddress);
            
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                System.out.println("I am found");
                userFound = true;
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        System.out.println("ln " + userFound);
        return userFound;   
    }
    
    /**
     * Method retrieves all Location updates within a time window
     * @param startWindow   timestamp format yyyy-MM-dd HH:mm:ss
     * @param endWindow     timestamp format yyyy-MM-dd HH:mm:ss
     * @return ArrayList of Location list of Location updates found
     */
    public static ArrayList<Location> getAllLocationsInWindow(String startWindow, String endWindow) {
        
        String query = "SELECT * FROM se.location "
                        + "WHERE " + TIMESTAMP_COL_NAME + " >= ? "
                        + "AND " + TIMESTAMP_COL_NAME + " < ?";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<Location> retList = new ArrayList<>();
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            
            stmt.setString(1, startWindow);
            stmt.setString(2, endWindow);
            
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Timestamp timestamp = rs.getTimestamp(TIMESTAMP_COL_NAME);
                String macAddress = rs.getString(MACADDRESS_COL_NAME);
                int locationID = rs.getInt(LOCATION_ID_COL_NAME);
                String timestampString = sdf.format(new Date(timestamp.getTime()));
                retList.add(new Location(timestampString, macAddress, locationID));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return retList;    //empty if nothing is found
        
    }
    
    /**
     * Method returns all location updates of a specific user within a time window
     * @param macAddress    
     * @param startWindow Start of the time window (Inclusive), format: yyyy-MM-dd HH:mm:ss
     * @param endWindow End of the time window (Exclusive), format: yyyy-MM-dd HH:mm:ss
     * @return  ArrayList of Location list of Location updates found
     */
    public static ArrayList<Location> getAllLocationsInWindow(String macAddress, String startWindow, String endWindow) {
        
        String query = "SELECT * FROM se.location "
                        + " WHERE " + MACADDRESS_COL_NAME + " = ? "
                        + " AND " + TIMESTAMP_COL_NAME + " >= ? "
                        + " AND " + TIMESTAMP_COL_NAME + " < ?";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<Location> retList = new ArrayList<>();
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            
            stmt.setString(1, macAddress);
            stmt.setString(2, startWindow);
            stmt.setString(3, endWindow);
            
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Timestamp timestamp = rs.getTimestamp(TIMESTAMP_COL_NAME);
                int locationID = rs.getInt(LOCATION_ID_COL_NAME);
                String timestampString = sdf.format(new Date(timestamp.getTime()));
                retList.add(new Location(timestampString, macAddress, locationID));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return retList;    //empty if nothing is found
        
    }
    
    /**
     * Method returns all Location updates of all users excluding the given MAC-Address
     * @param macAddress MAC-Address to exclude from search
     * @param startWindow Start of the time window (Inclusive), format: yyyy-MM-dd HH:mm:ss
     * @param endWindow End of the time window (Exclusive), format: yyyy-MM-dd HH:mm:ss
     * @return list of Location objects found in DB
     */
    public static ArrayList<Location> getAllLocationsExcludingMAC(String macAddress, String startWindow, String endWindow) {
        String query = "SELECT * FROM se.location "
                        + " WHERE " + MACADDRESS_COL_NAME + " <> ? "
                        + " AND " + TIMESTAMP_COL_NAME + " >= ? "
                        + " AND " + TIMESTAMP_COL_NAME + " < ?";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<Location> retList = new ArrayList<>();
        
        try {
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            
            stmt.setString(1, macAddress);
            stmt.setString(2, startWindow);
            stmt.setString(3, endWindow);
            
            rs = stmt.executeQuery();
            
            while(rs.next()) {
                Timestamp timestamp = rs.getTimestamp(TIMESTAMP_COL_NAME);
                int locationID = rs.getInt(LOCATION_ID_COL_NAME);
                String currMac= rs.getString(MACADDRESS_COL_NAME);
                String timestampString = sdf.format(new Date(timestamp.getTime()));
                retList.add(new Location(timestampString, currMac, locationID));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(rs);
            JDBCCloser.close(stmt);
            JDBCCloser.close(con);
        }
        
        return retList;    //empty if nothing is found
        
    }
    
    /**
     * This method drops the 'location' table in the database.
     */
    public static void dropTable() {
        
        Connection con = null;
        Statement stmt = null;
        
        String query = "DROP TABLE " + TABLE_NAME;
        
        try {
            con = ConnectionFactory.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        } finally {
            JDBCCloser.close(con);
            JDBCCloser.close(stmt);
        }
    }
    
    /**
     * This method recreates the 'location' table in the database.
     */
    public static void createTable() {
        
        
        Connection con = null;
        Statement stmt = null;
        
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                            + " timestamp datetime not null," 
                            + " mac_address varchar(40) not null,"
                            + " location_id int not null,"
                            + " constraint composite_pk PRIMARY KEY(timestamp, mac_address),"
                            + " constraint Qualification_fk1  FOREIGN KEY(location_id) REFERENCES location_lookup(location_id)"
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
     * Method inserts a Entity.Location Java object into the database
     * @param location 
     * @return rowsAffted. -1 if error
     */
    public static int insert(Location location) {
        String query = "INSERT INTO " + TABLE_NAME + " VALUES(?, ?, ?)";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int rowsAffected = -1;
        
        System.out.println("INSERTING: " + location.getTimestamp() + " " + location.getMacAddress() + " " + location.getLocationID());
        
        try {
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(query);
            stmt.setString(1, location.getTimestamp());
            stmt.setString(2, location.getMacAddress());
            stmt.setInt(3, location.getLocationID());
            
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
     * This method attempts to import a csv file into the location table 
     * @param csv
     * @return int number of rows affected
     */
    public static int loadDataInfile(File csv) {
        System.out.println("LOADING LOCATION FILE");
        long start = System.currentTimeMillis();
        
        String query = "LOAD DATA LOCAL INFILE '" + csv.getAbsolutePath().replace("\\", "\\\\") + "'"
                        + " REPLACE INTO TABLE " + TABLE_NAME
                        + " FIELDS TERMINATED BY ',' "
                        + " LINES TERMINATED BY '\\n'"
                        + "(timestamp,mac_address,location_id)";
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int rowsAffected = -1;
        
        try {
            con = ConnectionFactory.getConnection();
            
            stmt = con.prepareStatement(query);
            
            rowsAffected = stmt.executeUpdate();
            
            long end = System.currentTimeMillis();
            System.out.println("Time spent for locationcsv " + (end - start));
            
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
