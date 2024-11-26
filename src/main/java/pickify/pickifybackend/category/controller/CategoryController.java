package pickify.pickifybackend.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pickify.pickifybackend.category.service.CategoryService;
import pickify.pickifybackend.category.service.VectorCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories/test")
public class CategoryController {
    private final CategoryService categoryService;
    private final VectorCategoryService vectorCategoryService;

    @PostMapping("/match")
    public CategoryResponseDto matchCategory(@RequestBody CategoryRequestDto request) {
        return categoryService.matchCategory(request);
    }

    @PostMapping("/vector/match")
    public String matchVectorCategory(@RequestBody String keyword) {
        return vectorCategoryService.matchCategory(keyword);
    }
}
