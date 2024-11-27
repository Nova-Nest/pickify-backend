package pickify.pickifybackend.category;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TextToVector {
    private static final int VECTOR_SIZE = 8; // 벡터 크기 (임의 설정)

    public float[] embedText(String text) {
        // 간단한 랜덤 벡터 생성 (예시)
        float[] embedding = new float[VECTOR_SIZE];
        Random random = new Random(text.hashCode()); // 텍스트 기반 해시로 일관된 결과 생성
        for (int i = 0; i < VECTOR_SIZE; i++) {
            embedding[i] = random.nextFloat();
        }
        return embedding;
    }
}
