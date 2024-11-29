package pickify.pickifybackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.dto.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PickyLLMService {
    @Value("${gcp.vertex.projectId}")
    private String PROJECT_ID;

    @Value("${gcp.vertex.location}")
    private String LOCATION;

    private final String MODEL_NAME = "gemini-1.5-flash-001";

    private final PickyPhotoProcessor pickyPhotoProcessor;

    public SearchResultResponse getImageSearchResult(String searchText) {
        pickyPhotoProcessor.searchImageBy(searchText);
        return null;
    }

    public List<SearchResultResponse> getAIResultV2(PickyPhotoRequest pickyPhotoRequest) {
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName(MODEL_NAME)
                .build();

        UserMessage searchCandidates = PromptManager.extractSearchCandidates(pickyPhotoRequest.name(), pickyPhotoRequest.keywords());
        Response<AiMessage> result = model.generate(searchCandidates);

        System.out.println(result.toString());

        return null;
    }

    public PickyPhotoResponse getAIResult(PickyPhotoRequest pickyPhotoRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName(MODEL_NAME)
                .build();

        UserMessage keywordMessage = PromptManager.extractKeywordFromPhoto(pickyPhotoRequest.imageUrl(), pickyPhotoRequest.langType());
        UserMessage onlyKeyword = PromptManager.extractKeywordFromPhoto(pickyPhotoRequest.imageUrl(), pickyPhotoRequest.langType());

        Response<AiMessage> oldResult = model.generate(keywordMessage);
        Response<AiMessage> keywordResponse = model.generate(onlyKeyword);

        // 1. 키워드 추출; 1. Extract keyword
        PickyPhotoKeywordResponse pickyPhotoKeywordResponse;

        TestResponse testResponse;
        UserMessage result = PromptManager.extractAndGenerateQueries(pickyPhotoRequest.imageUrl(), pickyPhotoRequest.langType());
        Response<AiMessage> reulstResponse = model.generate(result);

        log.info("Received JSON: {}", reulstResponse.content().text());

        try {
            pickyPhotoKeywordResponse = objectMapper.readValue(keywordResponse.content().text(), PickyPhotoKeywordResponse.class);

            testResponse = objectMapper.readValue(reulstResponse.content().text(), TestResponse.class);

            log.info("keywordResponse: {}", keywordResponse);
            log.info("PickyPhotoKeywordResponse: {}", pickyPhotoKeywordResponse);
            log.info("testResponse: {}", testResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 2. 추출된 키워드로 사진 URL 추출
        UserMessage pictureUrl = PromptManager.extractImagesWithKeywords(pickyPhotoRequest.imageUrl(), pickyPhotoKeywordResponse.keywords());
        Response<AiMessage> pictureUrlResponse = model.generate(pictureUrl);

        testResponse.queries().forEach(pickyPhotoProcessor::searchImageBy);

        log.info("pictureUrlResponse : {}", pictureUrlResponse);

        return new PickyPhotoResponse();
    }
}
