/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.topKPlaces;

/**
 * Class represents JsonPopularPlace
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class JsonPopularPlace implements Comparable<JsonPopularPlace>{

    private int rank;
    private String semantic_place;
    private int count=0;
    
    /**
     * Constructor
     * @param rank
     * @param semantic_place
     * @param count
     */
    public JsonPopularPlace(int rank, String semantic_place, int count){
        this.rank = rank;
        this.semantic_place = semantic_place;
        this.count = count;
    }
    
    /**
     * 
     * @return rank
     */
    public int getRank(){
        return rank;
    }
    
    /**
     *
     * @return semantic_place
     */
    public String getSemantic_place(){
        return semantic_place;
    }
    
    /**
     *
     * @return count
     */
    public int getCount(){
        return count;
    }
    
    /**
     * Method set the rank of the object
     * @param rank
     */
    public void setRank(int rank){
        this.rank = rank;
    }
    
    /*
    Method that will help to arrange the place with same rank in alphabetic order
    */
    @Override
    public int compareTo(JsonPopularPlace o) {
        if(o.getRank()==this.rank){  
            
            return this.semantic_place.compareTo(o.getSemantic_place());
        
        }
        return 0;
    }
}
