package pickify.pickifybackend.category;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CategoryLoader {

    public static void loadCategories(String filePath, Map<String, Set<String>> categoryTree, Map<String, String> parentMap) {

        ClassPathResource resource = new ClassPathResource(filePath);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] categories = line.split(" > ");
                for (int i = 1; i < categories.length; i++) {
                    String parent = categories[i - 1];
                    String child = categories[i];
                    categoryTree.computeIfAbsent(parent, k -> new HashSet<>()).add(child);
                    parentMap.put(child, parent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
