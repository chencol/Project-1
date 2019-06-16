/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.topKCompanions;
/**
 * Class represents a companion of a specified user
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class Companion {
    private int rank;
    private String macAddress;
    private int timeTogether;
    private String email;
    /**
     * Constructor method
     * @param rank rankth companion amongst potential companions to the specified user
     * @param macAddress of the companion
     * @param timeTogether with the specified user
     * @param email of the companion
     */
    public Companion(int rank, String macAddress, int timeTogether, String email) {
        this.rank = rank;
        this.macAddress = macAddress;
        this.timeTogether = timeTogether;
        this.email = email;
    }
    /**
     * Getter method for rank
     * @return int 
     */
    public int getRank() {
        return rank;
    }
    /**
     * Getter method for MAC-Address
     * @return String 
     */
    public String getMacAddress() {
        return macAddress;
    }
    /**
     * Getter method for timeTogether
     * @return int in seconds, time spent together
     */
    public int getTimeTogether() {
        return timeTogether;
    }
    /**
     * Getter method for email
     * @return String companion's email. Empty String if MAC-Address is not a valid user in the DB Demographic table
     */
    public String getEmail() {
        return email;
    }
    
}
