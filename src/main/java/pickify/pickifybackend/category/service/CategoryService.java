package pickify.pickifybackend.category.service;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.category.CategoryLoader;
import pickify.pickifybackend.category.controller.CategoryRequestDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class CategoryService {

    private static final String CATEGORY_FILE_PATH = "src/main/resources/static/taxonomy.en-US.txt";

    private final Map<String, Set<String>> categoryTree = new HashMap<>();
    private final Map<String, String> parentMap = new HashMap<>();
    private final Map<String, Integer> keywordWeights = new HashMap<>();

    // 카테고리 파일 로드
    @PostConstruct
    public void loadCategories() {
        // CategoryLoader로 파일 로딩
        CategoryLoader.loadCategories(CATEGORY_FILE_PATH, categoryTree, parentMap);
    }

    // 키워드 기반 카테고리 매칭
    public String matchCategory(CategoryRequestDto requestDto) {
        Map<String, Integer> categoryScores = new HashMap<>();

        for (String keyword : requestDto.getKeywords()) {
            int weight = keywordWeights.getOrDefault(keyword.toLowerCase(), 1); // 기본 가중치 1
            for (String category : categoryTree.keySet()) {
                if (category.toLowerCase().contains(keyword.toLowerCase())) {
                    categoryScores.put(category, categoryScores.getOrDefault(category, 0) + weight);
                }
            }
        }
        // 최상위 카테고리 계산
        return calculateBestCategory(categoryScores);
    }

    // 최종적으로 가장 점수가 높은 카테고리 반환
    private String calculateBestCategory(Map<String, Integer> categoryScores) {
        String bestCategory = null;
        int maxScore = 0;
        Map<String, Integer> updatedScores = new HashMap<>();

        for (Map.Entry<String, Integer> entry : categoryScores.entrySet()) {
            String category = entry.getKey();
            int score = entry.getValue();
            while (parentMap.containsKey(category)) {
                category = parentMap.get(category); // 상위 카테고리로 점수 합산
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

    // 키워드 중요도 초기화
    public void loadKeywordWeights(Map<String, Integer> weights) {
        keywordWeights.putAll(weights);
    }
}
