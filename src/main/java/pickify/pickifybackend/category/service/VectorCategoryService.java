package pickify.pickifybackend.category.service;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.category.CategoryLoader;
import pickify.pickifybackend.category.SentenceBERTTranslator;
import pickify.pickifybackend.category.controller.CategoryRequestDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class VectorCategoryService {

    private final Map<String, String> parentMap = new HashMap<>();
    private final Map<String, Set<String>> categoryTree = new HashMap<>();
    private final Map<String, double[]> categoryVectors = new HashMap<>();
    private static final String CATEGORY_FILE_PATH = "src/main/resources/static/taxonomy.en-US.txt";

    private Predictor<String, double[]> embeddingPredictor;

    @PostConstruct
    public void init() throws IOException, ModelException {
        loadCategories();
        loadModel();
        generateCategoryEmbeddings();
    }

    // Taxonomy 로드
    private void loadCategories() {
        CategoryLoader.loadCategories(CATEGORY_FILE_PATH, categoryTree, parentMap);
    }

    // Sentence-BERT 모델 로드
    private void loadModel() throws ModelException, IOException {
        Model model = Model.newInstance("sentence-transformers/all-MiniLM-L6-v2");
        Translator<String, double[]> translator = new SentenceBERTTranslator();
        embeddingPredictor = model.newPredictor(translator);
    }

    // 카테고리 임베딩 생성
    private void generateCategoryEmbeddings() {
        for (String category : categoryTree.keySet()) {
            try {
                double[] embedding = embeddingPredictor.predict(category);
                categoryVectors.put(category, embedding);
            } catch (TranslateException e) {
                e.printStackTrace();
            }
        }
    }

    // 키워드 기반 카테고리 매칭
    public String matchCategory(CategoryRequestDto requestDto) {
        try {
            double[] keywordEmbedding = embeddingPredictor.predict(String.join(" ", requestDto.getKeywords()));

            String bestCategory = "Uncategorized";
            double maxSimilarity = 0;

            for (Map.Entry<String, double[]> entry : categoryVectors.entrySet()) {
                double similarity = calculateCosineSimilarity(keywordEmbedding, entry.getValue());
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    bestCategory = entry.getKey();
                }
            }

            return bestCategory;
        } catch (TranslateException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    // 코사인 유사도 계산
    private double calculateCosineSimilarity(double[] vector1, double[] vector2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += Math.pow(vector1[i], 2);
            norm2 += Math.pow(vector2[i], 2);
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
