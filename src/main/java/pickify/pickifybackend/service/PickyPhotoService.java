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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pickify.pickifybackend.dto.PickyPhotoKeywordResponse;
import pickify.pickifybackend.dto.PickyPhotoRequest;
import pickify.pickifybackend.dto.PickyPhotoResponse;
import pickify.pickifybackend.dto.TestResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

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

        TestResponse testResponse;
        UserMessage result = PromptManager.extractAndGenerateQueries(pickyPhotoRequest.photoUrl(), pickyPhotoRequest.langType());
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
        UserMessage pictureUrl = PromptManager.extractImagesWithKeywords(pickyPhotoRequest.photoUrl(), pickyPhotoKeywordResponse.keywords());
        Response<AiMessage> pictureUrlResponse = model.generate(pictureUrl);

        testResponse.queries().forEach(this::searchImage);

        log.info("pictureUrlResponse : {}", pictureUrlResponse);

        return new PickyPhotoResponse();
    }

    private String searchImage(String query) {
        final String API_KEY = "AIzaSyDQew5mwXtQTRNhEK5ReV3-4czOmeXpZmA"; // Google API 키
        final String SEARCH_ENGINE_ID = "00134324649bb4388"; // 검색 엔진 ID
        final String API_URL = "https://www.googleapis.com/customsearch/v1"; // 기본 제공 URI
        RestTemplate restTemplate = new RestTemplate();

        String urlString = String.format(
                "%s?q=%s&searchType=image&key=%s&cx=%s",
                API_URL, query.replace(" ", "+"), API_KEY, SEARCH_ENGINE_ID
        );

        URI uri = URI.create(urlString);

        String response = restTemplate.getForEntity(uri, String.class).getBody();

        log.info("response: {}", response);

        return response;
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.setRequestProperty("Accept", "application/json");
//
//            // 응답 받기
//            int responseCode = httpURLConnection.getResponseCode();
//            if (responseCode != 200) {
//                throw new RuntimeException("HTTP GET Request Failed with Error Code: " + responseCode);
//            }
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            reader.close();
//
//            return response.toString(); // JSON 응답 반환
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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
