package pickify.pickifybackend.category;

import ai.djl.Application;
import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.ndarray.NDList;
import ai.djl.nn.Block;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.io.IOException;

public class BertTranslator implements Translator<String, NDList> {

    private Block block;
    private Model model;

    public BertTranslator() {
        try {
            // BERT 모델 로드
            //model = Model.newInstance("bert", Application.NLP.TEXT_CLASSIFICATION);
            model = Model.newInstance("bert");
            block = model.getBlock();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load BERT model", e);
        }
    }

    @Override
    public NDList processInput(TranslatorContext ctx, String input) {
        // 입력을 NDList로 변환
        return new NDList(Integer.parseInt(input));
    }

    @Override
    public NDList processOutput(TranslatorContext ctx, NDList list) {
        // 출력 처리: 예측 결과 반환
        return list;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }

    public float[] embedText(String category) {
        // 카테고리를 임베딩하는 메서드
        return new float[768];
    }
}
