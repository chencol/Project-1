
package is203.se.BasicLocationReports.breakdown;

import java.util.ArrayList;

/**
 * This class represents the contents of a breakdown report, of which the individual sets of header:count (i.e. year:2013 count:50) are stored as Content objects
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class Breakdown {
    
    private String type;
    private ArrayList<Content> contents = new ArrayList<>();
    /**
     * Constructor method
     * @param type i.e. year, gender, school 
     */   
    public Breakdown(String type) {
        this.type = type;
    }
    
    /**
     * Getter method for Type
     * @return String, i.e. year, gender or school
     */
    public String getType() {
        return type;
    }
    /**
     * Getter method for contents
     * @return list of Content objects
     */       
    public ArrayList<Content> getContents() {
        return contents;
    }
    /**
     * Setter method for type
     * @param type String, i.e. year, gender, school
     */  
    public void setType(String type) {
        this.type = type;
    }
    /**
     * setter method for contents
     * @param contents List of Content objects representing the JSON object {"year": 2013, "count":40} for example
     */    
    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }
    /**
     * Method adds a content object to the list of content objects
     * @param content Content object representing the JSON object {"year": 2013, "count":40} for example
     */     
    public void addContent(Content content) {
        contents.add(content);
    }
    
}
