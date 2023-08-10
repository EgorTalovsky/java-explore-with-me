package ru.practicum.ewm.controller.public_api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "10") @Min(0) int size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryService.getAllCategories(from, size, page);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable @Min(0) long catId) {
        return categoryService.getCategoryDtoById(catId);
    }
}
