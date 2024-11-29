package pickify.pickifybackend.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pickify.pickifybackend.domain.UserLog;
import pickify.pickifybackend.dto.PickyPhotoRequest;
import pickify.pickifybackend.dto.SearchResultResponse;
import pickify.pickifybackend.service.ImgCategoryService;
import pickify.pickifybackend.service.PickyLLMService;
import pickify.pickifybackend.service.UserLongService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PickyPhotoController {
    private final PickyLLMService pickyLLMService;
    private final ImgCategoryService imgCategoryService;
    private final UserLongService userLongService;

    @PostMapping("/picky/extract")
    public ResponseEntity<List<SearchResultResponse>> getAIResult(@RequestBody PickyPhotoRequest pickyPhotoRequest) {
        String category = imgCategoryService.getAIResult(pickyPhotoRequest.keywords());
        UserLog userLog= UserLog.builder()
                .userUuid(pickyPhotoRequest.userUuid())
                .mainKeyword(pickyPhotoRequest.name())
                .builtInAiKeywords(pickyPhotoRequest.keywords())
                .category(category)
                .build();
        userLongService.saveUserLog(userLog);

        List<SearchResultResponse> result = pickyLLMService.getAIResult(pickyPhotoRequest);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/picky/search")
    public ResponseEntity<SearchResultResponse> getSearchResult(String keywords) {
        SearchResultResponse result = pickyLLMService.getImageSearchResult(keywords);
        return ResponseEntity.ok(result);
    }
}