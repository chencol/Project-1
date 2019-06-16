/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.topKPlaces;

import java.util.ArrayList;

/**
 * Class represents a PopularPlace
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class PopularPlace {
    private int rank;
    private ArrayList<String> semantic_place;
    private int count=0;
    
    /**
     *
     * @param rank
     * @param semantic_place
     * @param count
     */
    public PopularPlace(int rank, ArrayList<String> semantic_place, int count){
        this.rank = rank;
        this.semantic_place = semantic_place;
        this.count = count;
    }
    
    /**
     *
     * @param place
     * @param number
     * @param rank
     */
    public PopularPlace(String place, int number, int rank){
        this.semantic_place = new ArrayList<String>();
        this.semantic_place.add(place);
        this.count = number;
        this.rank = rank;
    }
    
    /**
     *
     * @return Rank of the the object
     */
    public int getRank(){
        return rank;
    }
    
    /**
     *
     * @return ArrayList of sementic_place String
     */
    public ArrayList<String> getSemantic_place(){
        return semantic_place;
    }
    
    /**
     * Method sets the rank of the object
     * @param k rank
     */
    public void setRank(int k){
        this.rank = k;
    }
    
    /**
     *
     * @return count of the object
     */
    public int getCount(){
        return count;
    }
    
    public String toString(){
        return semantic_place.toString();
    }
    
    /**
     *
     * @param place semantic place which has the same count of existing semantic place in this object
     */
    public void addPlace(String place){
        semantic_place.add(place);
    }
}
