package pickify.pickifybackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.dto.PickyPhotoKeywordResponse;
import pickify.pickifybackend.dto.PickyPhotoRequest;
import pickify.pickifybackend.dto.PickyPhotoResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class PickyPhotoService {
    @Value("${gcp.vertex.projectId}")
    private String PROJECT_ID;

    @Value("${gcp.vertex.location}")
    private String LOCATION;

    private final String MODEL_NAME = "gemini-1.5-flash-001";

    public PickyPhotoResponse getAIResult(PickyPhotoRequest pickyPhotoRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName(MODEL_NAME)
                .build();

        UserMessage keywordMessage = extractKeywordFromPhoto(pickyPhotoRequest.photoUrl(), pickyPhotoRequest.langType());
        UserMessage onlyKeyword = PromptManager.extractKeywordFromPhoto(pickyPhotoRequest.photoUrl(), pickyPhotoRequest.langType());

        Response<AiMessage> oldResult = model.generate(keywordMessage);
        Response<AiMessage> keywordResponse = model.generate(onlyKeyword);

        // 1. 키워드 추출; 1. Extract keyword
        PickyPhotoKeywordResponse pickyPhotoKeywordResponse;
        try {
            pickyPhotoKeywordResponse = objectMapper.readValue(keywordResponse.content().text(), PickyPhotoKeywordResponse.class);
            log.info("keywordResponse: {}", keywordResponse);
            log.info("PickyPhotoKeywordResponse: {}", pickyPhotoKeywordResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 2. 추출된 키워드로 사진 URL 추출
        UserMessage pictureUrl = PromptManager.extractImagesWithKeywords(pickyPhotoRequest.photoUrl(), pickyPhotoKeywordResponse.keywords());
        Response<AiMessage> pictureUrlResponse = model.generate(pictureUrl);

        log.info("pictureUrlResponse : {}", pictureUrlResponse);

        return new PickyPhotoResponse();
    }

    private UserMessage extractKeywordFromPhoto(String photoUrl, String langType) {
        return UserMessage.from(
                ImageContent.from(photoUrl),
                TextContent.from(String.format("""
                        You can extract some keywords with the pictures. Your response should be %s
                        And Find some pictures related with image links.
                        The recommend url should be an image like jpg or and so on.
                        You act like crawling.
                        If the result must be google image search, you should extract image url and give it.
                        Also you can find some items related with the image, if the image is garment you should give recommendUrl with a matching coordination or
                        the image is an appliance you should give recommendUrl with a using appliance such as computer - mouse, keyboard...
                        You also recommend each of an item in type such as if type is mouse, you should give one url of mouse
                        output should be Json format
                        
                        format : 
                        
                        {
                            "langType" : %s,
                            "keywords" : [keyword_list],
                            "relatedUrl" : [url_list]
                            "recommendUrl" : [{"type" : "url_list"}],
                        }
                        """, langType, langType)
                )
        );
    }
}
