package pickify.pickifybackend.category;

import java.io.*;
import java.util.*;

public class CategoryMatcher {

    // 트리 형태로 데이터를 저장할 Map
    private static final Map<String, List<String>> CATEGORY_TREE = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // 텍스트 파일 경로
        String filePath = "taxonomy.en-US.txt";

        // 파일을 읽어 CATEGORY_TREE에 저장
        loadCategories(filePath);

        // 테스트 키워드
        List<String> keywords = Arrays.asList("cat", "cat food", "bird cage", "bird bath");

        // 키워드로부터 매칭된 카테고리 반환
        for (String keyword : keywords) {
            String matchedCategory = findCategory(keyword);
            System.out.println("Keyword: " + keyword + " -> Category: " + matchedCategory);
        }
    }

    // 파일 읽어서 카테고리 트리 생성
    private static void loadCategories(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // '>' 기준으로 나누고 계층 구조 저장
                String[] parts = line.split(" > ");
                String parent = parts[0];
                for (int i = 1; i < parts.length; i++) {
                    CATEGORY_TREE.computeIfAbsent(parent, k -> new ArrayList<>()).add(parts[i]);
                    parent = parts[i];
                }
            }
        }
    }

    // 키워드와 카테고리를 매칭
    private static String findCategory(String keyword) {
        keyword = keyword.toLowerCase();

        // 트리를 순회하며 키워드와 가장 유사한 카테고리를 탐색
        for (String category : CATEGORY_TREE.keySet()) {
            if (keyword.contains(category.toLowerCase())) {
                return category;
            }
        }
        return "Uncategorized"; // 매칭 실패 시
    }
}

