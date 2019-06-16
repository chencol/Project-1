//TO-DO: LOGGER IMPLEMENTATION

package is203.se.Connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;


/**
 * This class initializes the JDBC driver staticly, and contains static method to create connections to the database.
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 * <b>Note: </b> Remember to close Connection, Statement, and ResultSet objects after using the Connection
 */
public class ConnectionFactory {
    
    private static final String PROPERTY_FILENAME = "connection.properties";
    private static String DBUsername;
    private static String DBPassword;
    private static String DBUrl;
    private static final int MAX_CONNECTIONS = 50;
    private static BasicDataSource conPool;
    
    static {
        //create connection pool
        conPool = new BasicDataSource();
                
        //load driver
        loadDriver();
        readProperties();
    }
    
    /**
     * This method sets the driver for the basicDataSource connection pool
     */
    public static void loadDriver() {
        
        try {
            //load mysql JDBC connector/driver for use
            conPool.setDriverClassName("com.mysql.jdbc.Driver");    //Connection Pool is not initialized here, but only when these methods are called: getConnection, setLogwriter, setLoginTimeout, getLoginTimeout, getLogWriter.
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * This method reads values from the connection.properties file and 
     * assigns values to the DBUsername, DBPassword, and DBUrl fields
     * and sets the connection pool properties
     */
    public static void readProperties() {
        
        try (InputStream propInput = ConnectionFactory.class.getResourceAsStream(PROPERTY_FILENAME);) {

            Properties properties = new Properties();
            properties.load(propInput);

            //get property values
            String host = properties.getProperty("mySQL.host");
            String dbName = properties.getProperty("mySQL.DBname");
            String port = properties.getProperty("mySQL.port");
            DBUsername = properties.getProperty("mySQL.username");
            String osName = System.getProperty("os.name");
            if (osName.equals("Linux")) {
                // in production environment, use aws.db.password
                DBPassword = properties.getProperty("aws.db.password");
            } else {
                // in local environment, use db.password
                DBPassword = properties.getProperty("db.password");
            }

            DBUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
            
            //Set connection pool properties
            conPool.setUrl(DBUrl);
            conPool.setUsername(DBUsername);
            conPool.setPassword(DBPassword);
            conPool.setMaxTotal(MAX_CONNECTIONS);   //max number of connections that can be made
            
//            System.out.println("url: " + DBUrl);
//            System.out.println("username: " + DBUsername);
//            System.out.println("pwd: " + DBPassword);
            
        } catch (FileNotFoundException ex) {
            System.out.println("readProperties() method failed...");
            ex.printStackTrace();
            
        } catch (IOException ex) {
            System.out.println("readProperties() method failed...");
            ex.printStackTrace();
        }
    }
    
    
    /**
     * This method returns a Connection to the mySQL database as specified in the connection.properties file
     * @return Connection 
     * @throws SQLException 
     */
    public static Connection getConnection() throws SQLException {
        
        return conPool.getConnection();

    }
    
}
