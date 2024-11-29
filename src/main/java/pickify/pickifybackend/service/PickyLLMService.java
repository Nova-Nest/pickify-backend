package pickify.pickifybackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import pickify.pickifybackend.repository.UserLogRepository;
import pickify.pickifybackend.util.PickyPhotoProcessor;

import java.util.ArrayList;
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
    private final UserLogRepository userLogRepository;

    public SearchResultResponse getImageSearchResult(String searchText) {
        pickyPhotoProcessor.searchImageBy(searchText);
        return null;
    }

    public PickyPhotoResponse getAIResult(PickyPhotoRequest pickyPhotoRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName(MODEL_NAME)
                .build();

        UserMessage searchCandidates = PromptManager.extractSearchCandidates(pickyPhotoRequest.name(), pickyPhotoRequest.keywords());
        Response<AiMessage> result = model.generate(searchCandidates);

        List<String> results = new ArrayList<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(result.content().text());
            if (jsonNode.isArray()) {
                for (JsonNode json : jsonNode) {
                    results.add(json.asText());
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // TODO 실제 ObjectId로 넣어야함
        return new PickyPhotoResponse("ObjID", pickyPhotoProcessor.searchImageBy(results));
    }
}
