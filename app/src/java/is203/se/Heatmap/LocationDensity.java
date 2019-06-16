package is203.se.Heatmap;

/**
 * This class represents the number of people in each semantic place
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class LocationDensity {
    private String semanticPlace;
    private int numberOfPeople;
    
    /**
     * Constructor
     * @param semanticPlace
     * @param numberOfPeople
     */
    public LocationDensity(String semanticPlace, int numberOfPeople) {
        this.semanticPlace = semanticPlace;
        this.numberOfPeople = numberOfPeople;
    }
    
    /**
     * Method returns the semantic place it represents
     * @return String semantic place
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }
    
    /**
     * Method returns the number of people in the semantic place
     * @return int number of people in the location
     */
    public int getNumberOfPeople() {
        return numberOfPeople;
    }
    
    /**
     * Method adds 1 to the total numberOfPeople found
     */
    public void incrementNumberOfPeople() {
        numberOfPeople++;
    }
    
    /**
     * Method converts a number to a crowd density number
     * @return int crowd density number, or -1 if number if less than 0
     */
    public int getDensity() {
        if(numberOfPeople == 0) {
            return 0;
        } else if (numberOfPeople >= 1 && numberOfPeople <= 2) {
            return 1;
        } else if (numberOfPeople >= 3 && numberOfPeople <= 5) {
            return 2;
        } else if (numberOfPeople >=6 && numberOfPeople <= 10) {
            return 3;
        } else if (numberOfPeople >= 11 && numberOfPeople <= 20) {
            return 4;
        } else if (numberOfPeople >= 21 && numberOfPeople <= 30) {
            return 5;
        } else if (numberOfPeople >= 31) {
            return 6;
        } else {    //negative number
            return -1;
        }
        
    }
    
}
