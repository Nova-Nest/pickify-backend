package pickify.pickifybackend.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pickify.pickifybackend.dto.PickyPhotoRequest;
import pickify.pickifybackend.dto.PickyPhotoResponse;
import pickify.pickifybackend.service.PickyPhotoService;

@RequiredArgsConstructor
@RestController
public class PickyPhotoController {
    private final PickyPhotoService pickyPhotoService;

    @PostMapping("/picky/extract")
    public ResponseEntity<PickyPhotoResponse> getAIResult(@RequestBody PickyPhotoRequest pickyPhotoRequest) {
        PickyPhotoResponse result = pickyPhotoService.getAIResult(pickyPhotoRequest);
        return ResponseEntity.ok(result);
    }
}
