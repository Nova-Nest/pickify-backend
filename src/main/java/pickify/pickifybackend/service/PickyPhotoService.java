package pickify.pickifybackend.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pickify.pickifybackend.dto.PickyPhotoRequest;
import pickify.pickifybackend.dto.PickyPhotoResponse;

@RequiredArgsConstructor
@Service
public class PickyPhotoService {
    @Value("${gcp.vertex.projectId}")
    private String PROJECT_ID;

    @Value("${gcp.vertex.location}")
    private String LOCATION;

    private final String MODEL_NAME = "gemini-1.5-flash-001";

    public PickyPhotoResponse getAIResult(PickyPhotoRequest pickyPhotoRequest) {
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName(MODEL_NAME)
                .build();

        UserMessage keywordMessage = extractKeywordFromPhoto(pickyPhotoRequest.photoUrl(), pickyPhotoRequest.langType());

        Response<AiMessage> keywordResponse = model.generate(keywordMessage);
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
