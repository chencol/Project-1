package is203.se.Connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class contains methods to close JDBC Connection, Statements, and ResultSets 
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class JDBCCloser {
    
    /**
     * Method closes a JDBC Connection
     * @param con 
     */
    public static void close(Connection con) {
        if(con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                //TO-DO: LOGGER
            }
        }
    }
    
    /**
     * Method closes a JDBC Statement
     * @param stmt 
     */
    public static void close(Statement stmt) {
        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                //TO-DO: LOGGER
            }
        }
    }
    
    /**
     * Method closes a JDBC ResultSet
     * @param rs 
     */
    public static void close(ResultSet rs) {
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                //TO-DO: LOGGER
            }
        }
    }
    
}
