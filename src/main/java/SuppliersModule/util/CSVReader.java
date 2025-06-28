package SuppliersModule.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<String[]> loadCSV(String resourcePath) {
        List<String[]> rows = new ArrayList<>();

        try (InputStream is = CSVReader.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
        reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line.split(","));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
    }
}
