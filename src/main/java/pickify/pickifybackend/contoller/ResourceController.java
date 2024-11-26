package pickify.pickifybackend.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pickify.pickifybackend.service.ResourceService;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/upload")
    public String upload(@RequestBody MultipartFile file) {
        return resourceService.uploadFile(file);
    }
}
