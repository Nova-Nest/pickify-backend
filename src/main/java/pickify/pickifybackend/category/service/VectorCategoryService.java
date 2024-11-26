package pickify.pickifybackend.category.service;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.category.BertTranslator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VectorCategoryService {
    // BertTranslator 인스턴스
    private final BertTranslator bertTranslator;

    // 카테고리 임베딩을 저장할 맵
    private final Map<String, float[]> categoryEmbeddings = new HashMap<>();

    // 생성자
    public VectorCategoryService() throws IOException, ModelException, TranslateException {
        this.bertTranslator = new BertTranslator();
        loadCategoryEmbeddings("src/main/resources/static/taxonomy.en-US.txt");
    }

    // 카테고리 임베딩을 로드하는 메서드
    private void loadCategoryEmbeddings(String filePath) throws IOException, TranslateException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String category = line.split(" > ")[0];
                categoryEmbeddings.put(category, bertTranslator.embedText(category));
            }
        }
    }

    // 키워드로 카테고리를 매칭하는 메서드
    public String matchCategory(String keyword) {
        float[] keywordEmbedding = bertTranslator.embedText(keyword);

        String bestMatch = null;
        double bestSimilarity = -1;

        for (Map.Entry<String, float[]> entry : categoryEmbeddings.entrySet()) {
            double similarity = cosineSimilarity(keywordEmbedding, entry.getValue());
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestMatch = entry.getKey();
            }
        }

        return bestMatch;
    }

    // 코사인 유사도를 계산하는 메서드
    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}