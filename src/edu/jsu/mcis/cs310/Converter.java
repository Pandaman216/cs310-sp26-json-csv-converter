package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.util.List;
import java.io.StringWriter;
public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            JsonObject root = new JsonObject();
            JsonArray proNums = new JsonArray(); // Production Numbers
            JsonArray columnHeaders = new JsonArray();
            JsonArray data = new JsonArray();
            
            
            CSVReader csvR = new CSVReader(new java.io.StringReader(csvString));//CSVReader converts csvString into list of String arrays. Each array is one row while columns are attributes
            List <String[]> csvRows = csvR.readAll(); //csvRows array contains all arrays from CSVReader
            
            String[] headers = csvRows.get(0);
            for(String header : headers) { // adds all headers to row 0
                columnHeaders.add(header);
            }
            
            for(int i = 1; i < csvRows.size(); i++) { //ProdNums go first, fallowed by the data
                String[] row = csvRows.get(i);
                proNums.add(row[0]);
                
                JsonArray rowData = new JsonArray(); // Array for row data
                
                for (int j = 1; j < row.length; j++) { // j0-prodNums, j1-Title...
                    
                    String value = row[j];
                    
                    try {
                        
                        int intValue = Integer.parseInt(value); // if String can be converted, convert and add
                        rowData.add(intValue);
                        
                    } catch (NumberFormatException e) {
                        
                        rowData.add(value); // String can not be converted to integer, so keep as string
                        
                    }
                }
                
                data.add(rowData);
           
            }
            
            //Put three arrays into root JSON object
            root.put("ProdNums", proNums);
            root.put("ColHeadings", columnHeaders);
            root.put("Data", data);
            
            result = root.toJson();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            JsonObject root = (JsonObject) Jsoner.deserialize(jsonString); // takes JSON string and converts into java objects
            
            // Get the arrays
            JsonArray proNums = (JsonArray) root.get("ProdNums"); 
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray data = (JsonArray) root.get("Data");
            
            //Make the CSV Writter
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);
            
            
            String[] headers = new String[colHeadings.size()];
            
            for (int i = 0; i < colHeadings.size(); i++) {
                headers[i] = (String) colHeadings.get(i); //copy the headers into colHeadings. Casting because JSON arrays are java objects
                
            }
            
            //FIRST CSV LINE WOOHOO
            csvWriter.writeNext(headers); // writes the array as a CSV row with quotes and commas baked in
            
            for (int i = 0; i < proNums.size(); i++) { // loop through the episodes
                
                String[] row = new String[colHeadings.size()]; // Array to hold new CSV row
                
                row[0] = (String) proNums.get(i); // prodNums row is iterated and filled
                
                JsonArray rowData = (JsonArray) data.get(i); // array for data
                
                for (int j = 0; j < rowData.size(); j++) {
                    
                    row[j+1] = rowData.get(j).toString(); // fills leftover columns after prodnums and converts all to stirng
                    
                }
                
                csvWriter.writeNext(row); // writes the row in CSV
                
            }
            
            csvWriter.close();
            result = writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
