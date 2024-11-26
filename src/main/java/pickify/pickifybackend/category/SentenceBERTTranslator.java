package pickify.pickifybackend.category;

import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

public class SentenceBERTTranslator implements Translator<String, double[]> {

    @Override
    public NDList processInput(TranslatorContext ctx, String input) throws TranslateException {
        // 입력 텍스트를 NDList로 변환
        NDManager manager = ctx.getNDManager();
        return new NDList(manager.create(input));
    }

    @Override
    public double[] processOutput(TranslatorContext ctx, NDList list) throws TranslateException {
        // 모델의 출력을 double 배열로 변환
        return list.singletonOrThrow().toDoubleArray();
    }

    @Override
    public Batchifier getBatchifier() {
        return null; // 배치 처리가 필요 없는 경우 null 반환
    }
}
