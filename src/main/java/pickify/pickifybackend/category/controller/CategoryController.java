package pickify.pickifybackend.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pickify.pickifybackend.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories/test")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/match")
    public String matchCategory(@RequestBody CategoryRequestDto request) {
        return categoryService.findCategory(request);
    }
}
