
package is203.se.BasicLocationReports.topKCompanions;

import java.util.Comparator;
import java.util.Map.Entry;

/**
 * Comparator that compares two values 
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class ValueComparator implements Comparator{
    
    /**
     *  Comparator that compares two Map.Entry of key: String, Value: Integer objects by their values
     * @return 
     */
    @Override
    public int compare(Object o1,Object o2){  
        
        Entry<String, Integer> set1 = (Entry<String, Integer>)o1;
        Entry<String, Integer> set2 = (Entry<String, Integer>)o2;
        
        return set2.getValue() - set1.getValue();
        
    }
    
}
