package engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class to provide extra utilities
 * 
 * @author Cas Wognum (TU/e, 1012585)
 */
public class Utilities {
    
    /**
     * Loads a resource based on its filename
     * 
     * @param fileName The path to where the resource is located
     * @return  The resource as a string
     * @throws Exception if resource is not found
     */
    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Class.forName(Utilities.class.getName()).getResourceAsStream(fileName);
                Scanner scanner = new Scanner(in, "UTF-8")) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }
    
    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utilities.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }
}
