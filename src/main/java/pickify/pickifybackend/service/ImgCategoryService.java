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
import pickify.pickifybackend.category.controller.CategoryRequestDto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImgCategoryService {

    @Value("${gcp.vertex.projectId}")
    private String PROJECT_ID;

    @Value("${gcp.vertex.location}")
    private String LOCATION;

    private final String MODEL_NAME = "gemini-1.5-flash-001";

    private static final String CATEGORY_FILE_PATH = "src/main/resources/static/taxonomy.en-US.txt";
    static List<String> categoryList = new ArrayList<>(); //파일에서 읽은 카테고리들을 저장

    public String getAIResult(CategoryRequestDto requestDto) {
        ObjectMapper objectMapper = new ObjectMapper();

        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project(PROJECT_ID)
                .location(LOCATION)
                .modelName(MODEL_NAME)
                .build();

        // 카테고리 파일을 로드합니다.
        loadCategoriesFromFile(CATEGORY_FILE_PATH);

        UserMessage category = PromptManager.extractCategoryFromKeywords(requestDto.getKeywords(), categoryList);

        Response<AiMessage> resultResponse = model.generate(category);

        log.info("Received JSON: {}", resultResponse.content().text());

        return extractCategoryFromJson(resultResponse.content().text());

    }
    public String extractCategoryFromJson(String jsonResponse) {
        Pattern pattern = Pattern.compile("\"category\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonResponse);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // 서버가 시작될 때 파일을 읽어 메모리에 저장하는 메서드
    public void loadCategoriesFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by ">"
                String[] parts = line.split(" > ");
                // Take the first part as the root category
                String rootCategory = parts[0].trim();
                // Add the root category to the list if it's not already present
                if (!categoryList.contains(rootCategory)) {
                    categoryList.add(rootCategory);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
