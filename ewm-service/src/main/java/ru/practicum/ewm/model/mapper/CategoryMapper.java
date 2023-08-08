package ru.practicum.ewm.model.mapper;

import ru.practicum.ewm.model.entity.Category;
import ru.practicum.ewm.model.dto.CategoryDto;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
