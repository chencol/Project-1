/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is203.se.BasicLocationReports.topKPlaces;

import is203.se.Entity.Location;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Class checks the Date object
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class DateOperator {
    
    /**
     * Method check a Date object which is n minutes before the provided Date Object
     * @param date date object
     * @param minutes 
     * @return Date object which is n minutes before the provided Date Object
     */
    public static Date getDateBeforeMinutes(Date date, int minutes){
        return new Date(date.getTime() - minutes*60*1000);
    }
    
    /**
     * Method check a formatted Date String
     * @param date Date Object
     * @return date string which is formated as  yyyy-MM-dd HH:mm:ss (i.e 2013-07-02 23:12:12)
     */
    public static String getFormattedTimeString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(date);
        return dateString;
    }
    
    /**
     * Method check the time difference between two different  Location objects
     * @param lk1 Location object
     * @param lk2 Location object
     * @return the timeDifference in second of two different Location objects
     */
    public static int checkTimeDifference(Location lk1, Location lk2){
        Date d1 = lk1.getDate();
        Date d2 = lk2.getDate();
        
        return checkTimeDifference(d1,d2);
    }
    
    /**
     * Methods check the time difference between two Date object
     * @param d1 
     * @param d2
     * @return time difference in second between two different date object
     */
    public static int checkTimeDifference(Date d1, Date d2){
        Date startDate = d1;
        Date endDate = d2;
        long duration = endDate.getTime()-startDate.getTime();
        int diffInSeconds = (int)(TimeUnit.MILLISECONDS.toSeconds(duration));
        return diffInSeconds;
    }
}
