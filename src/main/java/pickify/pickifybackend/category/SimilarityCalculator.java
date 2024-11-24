package pickify.pickifybackend.category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilarityCalculator {

//    public static Map<String, String> matchKeywordsToCategories(List<String> keywords, List<String> categories) {
//        Map<String, String> keywordCategoryMap = new HashMap<>();
//        LevenshteinDistance distance = new LevenshteinDistance(); // 기본적인 문자열 맵핑.. todo: BERT 기반 NLP 로 대체
//
//        for (String keyword : keywords) {
//            String bestMatch = null;
//            float highestScore = 0;
//
//            for (String category : categories) {
//                float score = distance.getDistance(keyword.toLowerCase(), category.toLowerCase());
//                if (score > highestScore) {
//                    highestScore = score;
//                    bestMatch = category;
//                }
//            }
//
//            keywordCategoryMap.put(keyword, bestMatch);
//        }
//
//        return keywordCategoryMap;
//    }
}
