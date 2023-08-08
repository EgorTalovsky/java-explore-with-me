package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.entity.Category;
import ru.practicum.ewm.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto, long catId);

    void deleteCategory(long catId);

    List<CategoryDto> getAllCategories(int from, int size, Pageable page);

    CategoryDto getCategoryDtoById(long catId);

    Category getCategoryById(long id);
}

