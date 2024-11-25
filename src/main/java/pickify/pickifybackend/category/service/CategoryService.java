package pickify.pickifybackend.category.service;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.category.CategoryLoader;
import pickify.pickifybackend.category.controller.CategoryRequestDto;
import pickify.pickifybackend.category.controller.CategoryResponseDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class CategoryService {

    private static final String CATEGORY_FILE_PATH = "src/main/resources/static/taxonomy.en-US.txt";

    private final Map<String, Set<String>> categoryTree = new HashMap<>(); //각 카테고리와 해당 카테고리에 속하는 키워드 집합을 저장합니다.
    private final Map<String, String> parentMap = new HashMap<>(); //parentMap 참조하여 상위 카테고리를 찾음
    private final Map<String, Integer> keywordWeights = new HashMap<>();

    @PostConstruct //서버가 띄워질때 한번만 실행
    public void loadCategories() {
        CategoryLoader.loadCategories(CATEGORY_FILE_PATH, categoryTree, parentMap);
    }

    public CategoryResponseDto matchCategory(CategoryRequestDto requestDto) {
        Map<String, Integer> categoryScores = new HashMap<>();

        for (String keyword : requestDto.getKeywords()) {
            int weight = keywordWeights.getOrDefault(keyword.toLowerCase(), 1);
            for (String category : categoryTree.keySet()) {
                if (category.toLowerCase().contains(keyword.toLowerCase())) { // 하나의 키워드가 여러개의 카테고리에 매칭될수 있음
                    categoryScores.put(category, categoryScores.getOrDefault(category, 0) + weight);
                }
            }
        }

        String bestCategory = calculateBestCategory(categoryScores);
        return new CategoryResponseDto(bestCategory, categoryScores);
    }

    private String calculateBestCategory(Map<String, Integer> categoryScores) {
        String bestCategory = null;
        int maxScore = 0;
        Map<String, Integer> updatedScores = new HashMap<>();

        for (Map.Entry<String, Integer> entry : categoryScores.entrySet()) {
            String category = entry.getKey();
            int score = entry.getValue();
            while (parentMap.containsKey(category)) {
                category = parentMap.get(category);
            }

            updatedScores.put(category, updatedScores.getOrDefault(category, 0) + score);
            if (updatedScores.get(category) > maxScore) {
                maxScore = updatedScores.get(category);
                bestCategory = category;
            }
        }

        categoryScores.putAll(updatedScores);
        return bestCategory;
    }

    public void loadKeywordWeights(Map<String, Integer> weights) {
        keywordWeights.putAll(weights);
    }
}
