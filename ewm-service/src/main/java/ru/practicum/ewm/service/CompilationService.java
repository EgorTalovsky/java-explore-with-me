package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(UpdateCompilationRequest compilation);

    CompilationDto getCompilationResponseDtoById(long compId);

    List<CompilationDto> getAllCompilations(boolean pinned, Pageable page);

    void deleteCompilation(long comId);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest compilation);
}
