package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.EntityAlreadyExistException;
import ru.practicum.ewm.exception.IncorrectFieldException;
import ru.practicum.ewm.exception.NoDataException;
import ru.practicum.ewm.model.dto.CategoryDto;
import ru.practicum.ewm.model.entity.Category;
import ru.practicum.ewm.model.entity.event.Event;
import ru.practicum.ewm.model.mapper.CategoryMapper;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryDto addCategory(CategoryDto categoryDto) {
        validateLength(categoryDto.getName().length());
        Category category = categoryRepository.findCategoryByName(categoryDto.getName()).orElse(null);
        if (category != null)
            throw new EntityAlreadyExistException("Категория с такими именем уже существует");
        Category categoryForSave = Category.builder()
                .name(categoryDto.getName())
                .build();
        return CategoryMapper.toCategoryDto(categoryRepository.save(categoryForSave));
    }

    public CategoryDto updateCategory(CategoryDto categoryDto, long catId) {
        categoryDto.setId(catId);
        if (categoryDto.getName() == null) {
            categoryDto.setName("");
        }
        validateLength(categoryDto.getName().length());
        Category category = categoryRepository.findCategoryByName(categoryDto.getName()).orElse(null);
        if (category != null && category.getId() != categoryDto.getId())
            throw new EntityAlreadyExistException("Категория с такими именем уже существует");
        Category findedCategory = getCategoryById(categoryDto.getId());
        if (categoryDto.getName().isEmpty()) {
            categoryDto.setName(null);
        }
        String name;
        if (categoryDto.getName() != null)
            name = categoryDto.getName();
        else
            name = findedCategory.getName();
        Category newCategory = new Category(categoryDto.getId(), name);
        categoryRepository.save(newCategory);
        return CategoryMapper.toCategoryDto(newCategory);
    }

    public void deleteCategory(long catId) {
        getCategoryById(catId);
        List<Event> events = eventRepository.findEventByCategory(catId);
        if (events.size() > 0)
            throw new EntityAlreadyExistException("У категории есть события, ее нельзя удалить");
        categoryRepository.deleteById(catId);
    }

    public List<CategoryDto> getAllCategories(int from, int size, Pageable page) {
        return categoryRepository.getAllCategoriesWithPageable(page)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryDtoById(long catId) {
        return CategoryMapper.toCategoryDto(getCategoryById(catId));
    }

    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NoDataException("Категория не найдена"));
    }

    private void validateLength(long length) {
        if (length > 50)
            throw new IncorrectFieldException("Имя больше 50 символов");
    }
}

