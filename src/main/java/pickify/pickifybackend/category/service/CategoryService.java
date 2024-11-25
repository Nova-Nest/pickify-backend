package pickify.pickifybackend.category.service;


import org.springframework.stereotype.Service;
import pickify.pickifybackend.category.TaxonomyLoader;
import pickify.pickifybackend.category.controller.CategoryRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryService {



    public String findCategory(CategoryRequestDto request) {
        List<String> keywords = request.getKeywords();
        String matchedCategory = "Uncategorized"; // 초기값 설정: 매칭되지 않은 경우

        // 텍스트 파일에서 카테고리를 읽어 트리 형태로 저장
        var categoryTree = TaxonomyLoader.loadCategories("src/main/resources/taxonomy.en-US.txt");

        // 자식 -> 부모 관계를 저장할 맵 생성
        Map<String, String> parentMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : categoryTree.entrySet()) {
            String parent = entry.getKey(); // 부모 카테고리
            for (String child : entry.getValue()) {
                parentMap.put(child, parent); // 자식 -> 부모 관계 저장
            }
        }

        // 키워드 리스트에서 카테고리를 매칭
        for (String keyword : keywords) {
            for (String category : categoryTree.keySet()) {
                // 키워드가 카테고리명에 포함되면 매칭
                if (keyword.toLowerCase().contains(category.toLowerCase())) {
                    matchedCategory = findTopLevelCategory(parentMap, category); // 최상위 부모 카테고리 반환
                    break;
                }
            }
            if (!matchedCategory.equals("Uncategorized")) break; // 매칭되면 종료
        }


        return matchedCategory;
    }

    // 최상위 부모 카테고리를 찾는 메서드
    private String findTopLevelCategory(Map<String, String> parentMap, String category) {
        while (parentMap.containsKey(category)) {
            category = parentMap.get(category); // 부모 카테고리를 따라 올라감
        }
        return category; // 최상위 부모 반환
    }
}
