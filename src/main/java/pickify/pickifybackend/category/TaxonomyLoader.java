package pickify.pickifybackend.category;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaxonomyLoader {
    public static Map<String, List<String>> loadCategories(String filePath) {
        Map<String, List<String>> categoryTree = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(" > ");
                String parent = parts[0];
                for (int i = 1; i < parts.length; i++) {
                    categoryTree.computeIfAbsent(parent, k -> new ArrayList<>()).add(parts[i]);
                    parent = parts[i];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return categoryTree;
    }
}
