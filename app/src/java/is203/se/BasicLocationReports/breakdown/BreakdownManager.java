/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.breakdown;

import is203.se.Entity.Demographic;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Class handles the logic behind breaking down a list of Demographic objects by specific categories
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class BreakdownManager {
    
    /**
     * Processes a list of Demographic objects and generates a Breakdown by year
     * @param users
     * @return Breakdown
     */
    public static Breakdown processYear(ArrayList<Demographic> users) {
        //this map holds k:year v:all users in that year
        TreeMap<Integer, ArrayList<Demographic>> yearUserMap = new TreeMap<>();
                
        //seperate users into their years
        for(Demographic user : users) {
            int currYear = user.getYear();
            ArrayList<Demographic> usersInCurrYear = yearUserMap.get(currYear);
            if(usersInCurrYear == null) {
                usersInCurrYear = new ArrayList<>();
            }
            usersInCurrYear.add(user);
            yearUserMap.put(currYear, usersInCurrYear);
        }
        
        // In order to show the number of different year even though the number of user is zero
        for(int i=2013;i<=2017;i++){
            if(!yearUserMap.containsKey(i)){
                yearUserMap.put(i,new ArrayList<Demographic>());
            }
        }
        
        
        //breakdown object contains all the contents created below
        Breakdown breakdown = new Breakdown("year");
        
        //loop through the map and create Content objects
        for(int key : yearUserMap.navigableKeySet()) {
            ArrayList<Demographic> usersInvolved = yearUserMap.get(key);
            String header = "" + key;
            Content content = new Content(header, usersInvolved);
            breakdown.addContent(content);
        }
        
        return breakdown;
    }
    
    /**
     * Processes a list of Demographic objects and generates a Breakdown by gender
     * @param users
     * @return Breakdown
     */
    public static Breakdown processGender(ArrayList<Demographic> users) {
        //this map holds k:gender v:all users in that year
        TreeMap<String, ArrayList<Demographic>> genderUserMap = new TreeMap<>();
        
        //seperate users into their genders
        for(Demographic user : users) {
            String currGender = "" + user.getGender();
            ArrayList<Demographic> usersInCurrGender = genderUserMap.get(currGender);
            if(usersInCurrGender == null) {
                usersInCurrGender = new ArrayList<>();
            }
            usersInCurrGender.add(user);
            genderUserMap.put(currGender, usersInCurrGender);
        }
        
        //breakdown object contains all the contents created below
        Breakdown breakdown = new Breakdown("gender");
        
        //Create both male and female contents IM ASSUMING THAT WE NEED TO ALWAYS SHOW BOTH
        ArrayList<Demographic> maleUsersInvolved = genderUserMap.get("M");      //male
        if(maleUsersInvolved == null) {
            maleUsersInvolved = new ArrayList<>();
        }
        String mHeader = "M";
        Content mContent = new Content(mHeader, maleUsersInvolved);
        breakdown.addContent(mContent);
        
        ArrayList<Demographic> femaleUsersInvolved = genderUserMap.get("F");    //female
        if(femaleUsersInvolved == null) {
            femaleUsersInvolved = new ArrayList<>();
        }
        String fHeader = "F";
        Content fContent = new Content(fHeader, femaleUsersInvolved);
        breakdown.addContent(fContent);

        return breakdown;
    }
    
    /**
     * Processes a list of Demographic objects and generates a Breakdown by school
     * @param users
     * @return Breakdown 
     */
    public static Breakdown processSchool(ArrayList<Demographic> users) {
        //this map holds k:gender v:all users in that school
        TreeMap<String, ArrayList<Demographic>> schoolUserMap = new TreeMap<>();
        
        //seperate users into their school
        for(Demographic user : users) {
            String currSchool = user.getSchool();
            ArrayList<Demographic> usersInCurrSchool = schoolUserMap.get(currSchool);
            if(usersInCurrSchool == null) {
                usersInCurrSchool = new ArrayList<>();
            }
            usersInCurrSchool.add(user);
            schoolUserMap.put(currSchool, usersInCurrSchool);
        }
        
        //In order to make sure that all school shown even the user is zero.
        ArrayList<String> schools = new ArrayList<>();
        schools.add("business");
        schools.add("accountancy");
        schools.add("sis");
        schools.add("economics");
        schools.add("law");
        schools.add("socsc");
        
        for(String school: schools){
            if(!schoolUserMap.containsKey(school)){
                schoolUserMap.put(school, new ArrayList<>());
            }
        }
        
        //breakdown object contains all the contents created below
        Breakdown breakdown = new Breakdown("school");
        
        //loop through the map and create Content objects
        //sort by school name
        for(String key : schoolUserMap.navigableKeySet()) {
            ArrayList<Demographic> usersInvolved = schoolUserMap.get(key);
            String header = key;
            Content content = new Content(header, usersInvolved);
            breakdown.addContent(content);
        }
        
        return breakdown;
    }
}
