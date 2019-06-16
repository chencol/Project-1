package is203.se.Bootstrap;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import is203.se.DAO.DemographicDAO;
import is203.se.DAO.LocationDAO;
import is203.se.DAO.LocationLookupDAO;
import is203.se.Entity.Demographic;
import is203.se.Entity.Location;
import is203.se.Validation.DemographicValidator;
import is203.se.Validation.LocationLookupValidator;
import is203.se.Validation.LocationValidator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.Part;

/**
 * Class contains common logic for bootstrap functions
 * @author David Liu, Brittany Goh, Chng Zheng Ren, Tan Ming Sheng, Chen Bi Huang
 */
public class BootstrapManager {

    /**
     * Method takes in a CSV file, reads its rows, treats the values found within as a csv entry of a Demographic, validates the values.
     * <p>
     * If valid, entry is inserted to the database, if false error messages are stored in the AbstractValidator
     * @param csvFile
     * @return AbstractValidator        use the method found in the class to get number of successful inserts and a Map that contains all error messages found in the CSV file
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     * @throws ParseException 
     */
    
public static BootstrapResult importDemographicCSV(File csvFile) throws FileNotFoundException, SQLException, IOException, ParseException {
        
        File tempCSV = new File(csvFile.getParentFile().getAbsolutePath() + File.separator + "tempDemographics.csv");
        
        //Error Msgs key: rowNum, value:errMsgs
        HashMap<Integer, String[]> allErrorMsgs = new HashMap<>();

        //Validator
        DemographicValidator validator = new DemographicValidator();

        //Demographics to be added
        ArrayList<Demographic> tempList = new ArrayList<>();
        
        //csvreader -> skips 1st line which is the header
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withSkipLines(1).build();
            CSVWriter writer = new CSVWriter(new FileWriter(tempCSV));) {
            
            //validate and insert CSV rows
            int currRowNum = 1;
            String[] row = null;
            
            while((row = reader.readNext()) != null) {  //loops all rows in CSV
                currRowNum++;

                //System.out.println("READ: " + Arrays.toString(row));    //DEBUG

                String macStr = row[0].trim();
                String nameStr = row[1].trim();
                String pwdStr = row[2].trim();
                String emailStr = row[3].trim();
                String genderStr = row[4].trim();

                HashMap<String, String> rowMap = new HashMap<>();
                rowMap.put(DemographicDAO.MAC_ADDRESS_COL_NAME, macStr);
                rowMap.put(DemographicDAO.NAME_COL_NAME, nameStr);
                rowMap.put(DemographicDAO.PASSWORD_COL_NAME, pwdStr);
                rowMap.put(DemographicDAO.EMAIL_COL_NAME, emailStr);
                rowMap.put(DemographicDAO.GENDER_COL_NAME, genderStr);

                ArrayList<String> rowErrMsgs = validator.isValid(rowMap);

                if(rowErrMsgs.isEmpty()) { //if no errors, add obj to tempList
                    //write to csv
                    String[] line = {macStr,nameStr,pwdStr,emailStr,genderStr};
                    writer.writeNext(line,false);
                    writer.flush();
                } else {    //errors found, add to HashMap of error messages
                    allErrorMsgs.put(currRowNum, rowErrMsgs.toArray(new String[rowErrMsgs.size()]));
                }
            }
        }
        
        //Number of successfully inserted rows
        int numSuccess = DemographicDAO.loadDataInfile(tempCSV);
        return new BootstrapResult(numSuccess, allErrorMsgs);
    }
    
    /**
     * Method takes in a CSV file, reads its rows, treats the values found within as a csv entry of a LocationLookup, validates the values.
     * <p>
     * If valid, entry is inserted to the database, if false error messages are stored in the AbstractValidator
     * @param csvFile
     * @return AbstractValidator    use the method found in the class to get number of successful inserts and a Map that contains all error messages found in the CSV file
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public static BootstrapResult importLocationLookupCSV(File csvFile) throws FileNotFoundException, SQLException, IOException{
        
        File tempCSV = new File(csvFile.getParentFile().getAbsolutePath() + File.separator + "tempLocationLookup.csv");

        //Error Msgs key: rowNum, value:errMsgs
        HashMap<Integer, String[]> allErrorMsgs = new HashMap<>();

        //Validator
        LocationLookupValidator validator = new LocationLookupValidator();
        
        //csvreader -> skips 1st line which is the header
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withSkipLines(1).build();
            CSVWriter writer = new CSVWriter(new FileWriter(tempCSV));) {
            //validate and insert CSV rows
            int currRowNum = 1;
            String[] row = null;
            while((row = reader.readNext()) != null) {  //loops all rows in CSV

                currRowNum++;

                //System.out.println("READ: " + Arrays.toString(row));

                String locationIDStr = row[0].trim();
                String semanticPlaceStr = row[1].trim();

                HashMap<String, String> rowMap = new HashMap<>();
                rowMap.put(LocationLookupDAO.LOCATION_ID_COL_NAME, locationIDStr);
                rowMap.put(LocationLookupDAO.SEMANTIC_PLACE_COL_NAME, semanticPlaceStr);

                ArrayList<String> rowErrMsgs = validator.isValid(rowMap);

                if(rowErrMsgs.isEmpty()) { //if no errors, INSERT
                    //write to csv
                    String[] line = {locationIDStr,semanticPlaceStr};
                    writer.writeNext(line, false);
                    writer.flush();
                } else {    //errors found
                    allErrorMsgs.put(currRowNum, rowErrMsgs.toArray(new String[rowErrMsgs.size()]));
                }
            }
        }
        
        //Number of successfully inserted rows
        int numSuccess = LocationLookupDAO.loadDataInfile(tempCSV);
        return new BootstrapResult(numSuccess, allErrorMsgs);
    }
    
    /**
     * Method takes in a CSV file, reads its rows, treats the values found within as a csv entry of a Location, validates the values.
     * <p>
     * If valid, entry is inserted to the database, if false error messages are stored in the AbstractValidator
     * @param csvFile
     * @return AbstractValidator        use the method found in the class to get number of successful inserts and a Map that contains all error messages found in the CSV file
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     * @throws ParseException 
     */
    public static BootstrapResult importLocationCSV(File csvFile) throws FileNotFoundException, SQLException, IOException, ParseException{
        //tempCSV
        File tempCSV = new File(csvFile.getParentFile().getAbsolutePath() + File.separator + "tempLocation.csv");
        
        //Error Msgs
        HashMap<Integer, String[]> allErrorMsgs = new HashMap<>();
        
        //Validator
        LocationValidator validator = new LocationValidator();
        
        //Dateformat
        DateFormat df = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        
        //Holds all Locatin objects, K:Location, V:csvRow       used to find duplicates
        HashMap<Location, Integer> allLocationsToBeAdded = new HashMap<>();
        
        //csvreader -> skips 1st line which is the header
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withSkipLines(1).build()) { 
            //validate and insert CSV rows
            int currRowNum = 1;
            String[] row = null;
            while((row = reader.readNext()) != null) {  //loops all rows in CSV

                currRowNum++;

                String timestampStr = row[0].trim();
                String macStr = row[1].trim();
                String locationIDStr = row[2].trim();

                HashMap<String, String> rowMap = new HashMap<>();
                rowMap.put(LocationDAO.TIMESTAMP_COL_NAME, timestampStr);
                rowMap.put(LocationDAO.MACADDRESS_COL_NAME, macStr);
                rowMap.put(LocationDAO.LOCATION_ID_COL_NAME, locationIDStr);

                ArrayList<String> rowErrMsgs = validator.isValid(rowMap);
                
                //check for infile duplicate
                Location currLocation = null;
                if(rowErrMsgs.isEmpty()) {
                    //Since no fields are blank or invalid, create the Location object
                    int locationID = Integer.parseInt(locationIDStr);
                    currLocation = new Location(timestampStr,macStr,locationID);

                    if(allLocationsToBeAdded.containsKey(currLocation)) {   //duplicate exists
                        
                        int errorRowNum = allLocationsToBeAdded.get(currLocation);
                        //the key isn't replaced if we dont remove it althought the value is overwritten-currRowNum
                        allLocationsToBeAdded.remove(currLocation);
                        allLocationsToBeAdded.put(currLocation, currRowNum);
                        rowErrMsgs.add("duplicate row");
                        allErrorMsgs.put(errorRowNum, rowErrMsgs.toArray(new String[rowErrMsgs.size()]));
                        continue;
                    }
                    
                } else {    //errors in cols found
                    allErrorMsgs.put(currRowNum, rowErrMsgs.toArray(new String[rowErrMsgs.size()]));
                    continue;
                }                       

                //add valid Location to allLocationsToBeAdded
                allLocationsToBeAdded.put(currLocation, currRowNum);
            }
        }
        
        //write locations to tempCSV
        try (CSVWriter writer = new CSVWriter(new FileWriter(tempCSV));) {
            
            for(Entry<Location, Integer> entry : allLocationsToBeAdded.entrySet()) {
                
                
                Location location = entry.getKey();
                
                String timestamp = location.getTimestamp();
                String mac = location.getMacAddress();
                String locationID = ""+location.getLocationID();
                
                //write to csv
                String[] line = {timestamp,mac, locationID};
                writer.writeNext(line, false);  //apply quotes to all false
                writer.flush();
            }
                
            //load data infile and return BootstrapResult
            LocationDAO.loadDataInfile(tempCSV);
            int numSuccess = allLocationsToBeAdded.size();
            return new BootstrapResult(numSuccess, allErrorMsgs);
            
        }
    }
    
    /**
     * Method bootstraps the database demographics table with additional values found in the CSV
     * @param csvFile
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     * @throws ParseException 
     * @return BootstrapResult
     */
    public static BootstrapResult importAdditionalDemographicCSV(File csvFile) throws FileNotFoundException, SQLException, IOException, ParseException {
        return importDemographicCSV(csvFile);
    }
    
    /**
     * Method bootstraps the database location table with additional values found in the CSV
     * @param csvFile
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException 
     * @return BootstrapResult
     */
    public static BootstrapResult importAdditionalLocationCSV(File csvFile) throws FileNotFoundException, SQLException, IOException {
        //tempCSV
        File tempCSV = new File(csvFile.getParentFile().getAbsolutePath() + File.separator + "tempLocation.csv");
        
        //Error Msgs
        HashMap<Integer, String[]> allErrorMsgs = new HashMap<>();
        
        //Validator
        LocationValidator validator = new LocationValidator();
        
        //Dateformat
        DateFormat df = new SimpleDateFormat(Location.DATE_FORMAT_PATTERN);
        
        //Holds all Locatin objects, K:Location, V:csvRow       used to find duplicates
        HashMap<Location, Integer> allLocationsToBeAdded = new HashMap<>();
        
        //csvreader -> skips 1st line which is the header
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(csvFile)).withSkipLines(1).build()) { 
            //validate and insert CSV rows
            int currRowNum = 1;
            String[] row = null;
            while((row = reader.readNext()) != null) {  //loops all rows in CSV

                currRowNum++;

                String timestampStr = row[0].trim();
                String macStr = row[1].trim();
                String locationIDStr = row[2].trim();

                HashMap<String, String> rowMap = new HashMap<>();
                rowMap.put(LocationDAO.TIMESTAMP_COL_NAME, timestampStr);
                rowMap.put(LocationDAO.MACADDRESS_COL_NAME, macStr);
                rowMap.put(LocationDAO.LOCATION_ID_COL_NAME, locationIDStr);

                ArrayList<String> rowErrMsgs = validator.isValid(rowMap);
                
                //check for datebase duplicate and infile duplicate
                Location currLocation = null;
                if(rowErrMsgs.isEmpty()) {
                    //Since no fields are blank or invalid, create the Location object
                    int locationID = Integer.parseInt(locationIDStr);
                    currLocation = new Location(timestampStr,macStr,locationID);
					//database duplicate
                    //if duplicate is found in the database, the method will return false
                    if(!validator.checkDuplicate(timestampStr, macStr)) {
                        rowErrMsgs.add("duplicate row");
                        allErrorMsgs.put(currRowNum, rowErrMsgs.toArray(new String[rowErrMsgs.size()]));
                        continue;
                        
                    //infile duplicate
                    } else if(allLocationsToBeAdded.containsKey(currLocation)) {
                        
                        int errorRowNum = allLocationsToBeAdded.get(currLocation);
                        //the key isn't replaced if we dont remove it althought the value is overwritten-currRowNum
                        allLocationsToBeAdded.remove(currLocation);
                        allLocationsToBeAdded.put(currLocation, currRowNum);
                        rowErrMsgs.add("duplicate row");
                        allErrorMsgs.put(errorRowNum, rowErrMsgs.toArray(new String[rowErrMsgs.size()]));
                        continue;
                    }
                    
                } else {    //errors in cols found
                    allErrorMsgs.put(currRowNum, rowErrMsgs.toArray(new String[rowErrMsgs.size()]));
                    continue;
                }                       

                //add valid Location to allLocationsToBeAdded
                allLocationsToBeAdded.put(currLocation, currRowNum);
            }
        }
        
        //write locations to tempCSV
        try (CSVWriter writer = new CSVWriter(new FileWriter(tempCSV));) {
            
            for(Entry<Location, Integer> entry : allLocationsToBeAdded.entrySet()) {
                
                Location location = entry.getKey();
                
                String timestamp = location.getTimestamp();
                String mac = location.getMacAddress();
                String locationID = ""+location.getLocationID();
                
                //write to csv
                String[] line = {timestamp,mac, locationID};
                writer.writeNext(line, false);  //apply quotes to all false
                writer.flush();
            }
                
            //load data infile and return BootstrapResult
            LocationDAO.loadDataInfile(tempCSV);
            int numSuccess = allLocationsToBeAdded.size();
            return new BootstrapResult(numSuccess, allErrorMsgs);
        }
    }
    
    /**
     * This method drops and recreates all tables within the database.
     */
    public static void emptyAllTables() {
        //Tables cannot be deleted when another table has a Foreign Key that references it
        // Therefore to empty the database, it MUST be in this order.
        
        LocationDAO.dropTable();
        LocationLookupDAO.dropTable();
        DemographicDAO.dropTable();
        
        DemographicDAO.createTable();
        LocationLookupDAO.createTable();
        LocationDAO.createTable();
    }
    
    /**
     * Unzip file to folder zipFile exists in
     * @param zipFile
     * @return ArrayList of unziped files that were in the zip file
     */
    public static ArrayList<File> unzipFile(File zipFile) {
        //list of unzipped files to return
        ArrayList<File> unzippedFiles = new ArrayList<>(); 
        
        String tempFolderPath = zipFile.getParentFile().getAbsolutePath();
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));) {
            
            ZipEntry zipEntry = zis.getNextEntry();     //Entries being the individual files in the zip

            while(zipEntry != null) {
                
                String fileName = zipEntry.getName();
                
                //Content in ZIP may be either a directory or a file
                File entryFile = new File(tempFolderPath + File.separator + fileName);
                
                if(zipEntry.isDirectory()) {    //if the entryFile is a directory
                    
                    entryFile.mkdirs();         //create the directory and any missing parent direcotires
                    
                } else {    //entryFile is an actual file
                    entryFile.getParentFile().mkdirs();     //just in case
                    
                    //write the files
                    byte[] buffer = new byte[1024];   //the ZipInputStream zis.read(buffer) will write the bytes into this array
                    try (FileOutputStream fos = new FileOutputStream(entryFile);){

                        int numBytesRead;
                        while ((numBytesRead = zis.read(buffer)) > 0) {       //zis.read(buffer) reads up to the number of bytes in the buffer from the zip
                            fos.write(buffer, 0, numBytesRead);     //Write the buffer, skip 0 bytes in buffer, number of bytes to write -> given by zis.read()
                        }
                        fos.flush();    //flushes any bytes in the outputStream buffer
                    }
                    //Add unzipped file (that is not a folder/directory) to the arrayList of files to return
                    unzippedFiles.add(entryFile);
                }
                //Close current entry and open next one
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return unzippedFiles;
    }
    
    /**
     * This method attempts to delete a file, along with all its contents and subdirectories
     * @param file to be deleted
     */
    public static void deleteFile(File file) {
        if(file.isDirectory())  { //file given is a folder/directory
            for(File subFile : file.listFiles()) {  //loop through everything in the directory
                deleteFile(subFile);                //send contents back into the method, where if it is a directory same thing happens, if its a file its deleted
            }
        }
        //At this point, any directories found should be totally empty and thus can be successfully deleted
        file.delete();
    }
    
    /**
     * Method gets a file name from the HTTP header content-disposition
     * @param part
     * @return 
     */
    public static String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
}
