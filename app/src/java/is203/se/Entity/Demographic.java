
package is203.se.Entity;

/**
 * This class/entity represents an entry found in the Demographic table in the database
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class Demographic {
        
    /**
     * char that represents the male gender
     */
    public static final char GENDER_MALE = 'M';
    /**
     * char that represents the female gender
     */
    public static final char GENDER_FEMALE = 'F';
    /**
     * int that represents a true admin status
     */
    public static final int ADMIN_TRUE = 1;
    /**
     * int that represents a false admin status
     */
    public static final int ADMIN_FALSE = 0;
    
    private String macAddress;
    private String name;
    private String password;
    private String email;
    private char gender;
    private int adminStatus;
    
    /**
     * Constructor method
     * @param macAddress
     * @param name
     * @param password
     * @param email
     * @param gender
     * @param adminStatus 
     */
    public Demographic(String macAddress, String name, String password, String email, char gender, int adminStatus) {
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.adminStatus = adminStatus;
    }
    
    /**
     * Method returns MAC-Address of the user the Demographic object represents
     * @return String MAC Address
     */
    public String getMacAddress() {
        return macAddress;
    }
    
    /**
     * Setter method for MAC Address
     * @param macAddress 
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    /**
     * Method returns name of the user the Demographic object represents
     * @return String name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setter method for name
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Method returns the password of the user the Demographic object represents
     * @return String password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Setter Method for password
     * @param password 
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Method returns the email of the user the Demographic object represents
     * @return String email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Method returns the enrollment year of the user the Demographic object represents
     * @return year as stated in email
     */
    public int getYear() {
        String[] atSplit = email.split("@");
        String[] dotSplit = atSplit[0].split("[.]");
        return Integer.parseInt(dotSplit[dotSplit.length - 1]);
    }
    
    /**
     * Method returns the school, i.e. SIS or econs of the user the Demographic object represents
     * @return school as stated in email
     */
    public String getSchool() {
        String[] atSplit = email.split("@");
        String[] dotSplit = atSplit[1].split("[.]");
        return dotSplit[0];
    }
    
    /**
     * Setter method for email
     * @param email 
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Method returns the gender of the user the Demographic object represents
     * @return char gender, M or F
     */
    public char getGender() {
        return gender;
    }
    
    /**
     * Setter method for gender
     * @param gender 
     */
    public void setGender(char gender) {
        this.gender = gender;
    }
    
    /**
     * Method returns the admin status of the user the Demographic object represents
     * @return 1 if is admin, 0 is not admin
     */
    public int getAdminStatus(){
        return adminStatus;
    }
    
}
