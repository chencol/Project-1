
package is203.se.BasicLocationReports.breakdown;

import is203.se.Entity.Demographic;
import java.util.ArrayList;

/**
 * This class represents the contents of a Breakdown report, (i.e. year:2013 count:50). It also additionally stores the Demographic objects involved
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class Content {
    
    private String header;
    private ArrayList<Demographic> usersInvolved;
    private Breakdown breakdown = null;
    
    /**
     * Constructor method
     * @param header i.e. 2013, male, sis...
     * @param usersInvolved
     */
    public Content(String header, ArrayList<Demographic> usersInvolved) {
        this.header = header;
        this.usersInvolved = usersInvolved;
    }
    /**
     * Getter method for header
     * @return String that is the value of the Breakdown type i.e. 2013, M, sis
     */     
    public String getHeader() {
        return header;
    }
    /**
     * Getter method for usersInvolved
     * @return list of Demographic users that fall into the category of the header, i.e. 40 Demographic users are in sis
     */  
    public ArrayList<Demographic> getUsersInvolved() {
        return usersInvolved;
    }
    /**
     * Getter method for the number of users involved in the breakdown
     * @return int number of Demographic users that fall into the category of the header, i.e. 40 Demographic users are in sis
     */     
    public int getCount() {
        return usersInvolved.size();
    }
    /**
/**
     * Getter method of the sub-breakdown
     * @return contents can contain Breakdowns as well. as per advanced report requirement. see project wiki
     */    
    public Breakdown getBreakdown() {
        return breakdown;
    }
    /**
     * Setter method for header
     * @param header set String header that is the value of the Breakdown type i.e. 2013, M, sis
     */    
    public void setHeader(String header) {
        this.header = header;
    }
    /**
     * Setter method for usersInvolved
     * @param usersInvolved Demographic users that fall into the category of the header, i.e. 40 Demographic users are in sis
     */    
    public void setUsersInvolved(ArrayList<Demographic> usersInvolved) {
        this.usersInvolved = usersInvolved;
    }
    /**
     * Setter method for the sub-breakdown object
     * @param breakdown set Breakdown of the content
     */    
    public void setBreakdown(Breakdown breakdown) {
        this.breakdown = breakdown;
    }
    
}
