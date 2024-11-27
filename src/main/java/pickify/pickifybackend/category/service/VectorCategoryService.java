package pickify.pickifybackend.category.service;

import ai.djl.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.category.TextToVector;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VectorCategoryService {
    private final TextToVector textEmbedder; // 단어 임베딩 생성기
    private final Map<String, float[]> categoryEmbeddings = new HashMap<>();

    // 카테고리 로드 및 백터화
    @PostConstruct
    public void loadCategoryEmbeddings() {
        List<String> categories = readCategoriesFromFile("src/main/resources/static/taxonomy.en-US.txt");
        for (String category : categories) {
            String[] parts = category.split(" > ");
            for (String part : parts) {
                if (!categoryEmbeddings.containsKey(part)) {
                    categoryEmbeddings.put(part, textEmbedder.embedText(part));
                }
            }
        }
    }

    // 키워드로 가장 유사한 카테고리 검색
    public Map<String, Pair<String, Double>> findMostSimilarCategory(List<String> keywords) {
        Map<String, Pair<String, Double>> bestMatches = new HashMap<>();
        for (String keyword : keywords) {
            float[] keywordVector = textEmbedder.embedText(keyword);
            Pair<String, Double> bestMatch = null;
            double highestSimilarity = -1;

            for (Map.Entry<String, float[]> entry : categoryEmbeddings.entrySet()) {
                double similarity = cosineSimilarity(keywordVector, entry.getValue());
                if (similarity > highestSimilarity) {
                    highestSimilarity = similarity;
                    bestMatch = new Pair<>(entry.getKey(), similarity);
                }
            }

            bestMatches.put(keyword, bestMatch);
        }
        return bestMatches;
    }

    private double cosineSimilarity(float[] vector1, float[] vector2) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            normA += Math.pow(vector1[i], 2);
            normB += Math.pow(vector2[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private List<String> readCategoriesFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read category file", e);
        }
    }
}