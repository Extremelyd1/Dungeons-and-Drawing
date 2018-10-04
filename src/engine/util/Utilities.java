package engine.util;

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
     * @return The resource as a string
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

    /**
     * Reads a whole file from a file and returns a list of lines
     *
     * @param fileName Name of the file to be loaded
     * @return List of lines
     * @throws Exception Exception
     */
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

    /**
     * Utility method that takes in a float list and converts it to an array.
     * @param list The list to convert to an array
     * @return float array with the elements from the list
     */
    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }
}
