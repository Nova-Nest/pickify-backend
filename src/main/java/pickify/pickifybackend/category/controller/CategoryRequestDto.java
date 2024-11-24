package pickify.pickifybackend.category.controller;

import lombok.Getter;

import java.util.List;

@Getter
public class CategoryRequestDto {
    private List<String> keywords;
}
