package pickify.pickifybackend.category.service;


import org.springframework.stereotype.Service;
import pickify.pickifybackend.category.TaxonomyLoader;
import pickify.pickifybackend.category.controller.CategoryRequestDto;

import java.util.List;

@Service
public class CategoryService {

    public String findCategory(CategoryRequestDto request) {
        List<String> keywords = request.getKeywords();
        String matchedCategory = "Uncategorized";

        // Load categories from the text file
        var categoryTree = TaxonomyLoader.loadCategories("src/main/resources/taxonomy.en-US.txt");

        // Match keywords to categories
        for (String keyword : keywords) {
            for (String category : categoryTree.keySet()) {
                if (keyword.toLowerCase().contains(category.toLowerCase())) {
                    matchedCategory = category;
                    break;
                }
            }
            if (!matchedCategory.equals("Uncategorized")) break;
        }

        return matchedCategory;
    }
}
