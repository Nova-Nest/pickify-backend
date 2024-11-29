package pickify.pickifybackend.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pickify.pickifybackend.dto.PickyPhotoRequest;
import pickify.pickifybackend.dto.PickyPhotoResponse;
import pickify.pickifybackend.dto.PickyRelatedProductResponse;
import pickify.pickifybackend.dto.SearchResultResponse;
import pickify.pickifybackend.service.PickyLLMService;

@RequiredArgsConstructor
@RestController
public class PickyPhotoController {
    private final PickyLLMService pickyLLMService;

    @PostMapping("/picky/extract")
    public ResponseEntity<PickyPhotoResponse> getAIResult(@RequestBody PickyPhotoRequest pickyPhotoRequest) {
        PickyPhotoResponse result = pickyLLMService.getAIResult(pickyPhotoRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/picky/relateProduct")
    public ResponseEntity<PickyRelatedProductResponse> getRelatedProduct(String productId) {
        PickyRelatedProductResponse result = pickyLLMService.getRelatedProduct(productId);
        return ResponseEntity.ok(result);
    }

//    @GetMapping("/picky/picky-suggestion")
//    public ResponseEntity<List<>> getSuggestion(String keywords) {
//
//    }

//    @PostMapping("/picky/search")
//    public ResponseEntity<SearchResultResponse> getSearchResult(String keywords) {
//        SearchResultResponse result = pickyLLMService.getImageSearchResult(keywords);
//        return ResponseEntity.ok(result);
//    }
}