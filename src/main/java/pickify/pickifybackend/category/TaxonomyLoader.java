package pickify.pickifybackend.category;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
                line = line.trim();  // 공백 제거
                if (line.isEmpty()) continue; // 빈 줄은 무시

                // 카테고리를 '>'로 구분하여 계층 구조로 처리
                String[] parts = line.split(" > ");
                String parent = parts[0]; // 첫 번째 요소는 부모 카테고리
                for (int i = 1; i < parts.length; i++) {
                    // 부모 카테고리에 자식 카테고리 추가
                    categoryTree.computeIfAbsent(parent, k -> new ArrayList<>()).add(parts[i]);
                    parent = parts[i]; // 현재 카테고리를 부모로 설정
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return categoryTree;
    }
}
