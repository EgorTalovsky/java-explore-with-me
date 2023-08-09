package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.IncorrectFieldException;
import ru.practicum.ewm.exception.NoDataException;
import ru.practicum.ewm.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.model.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.model.dto.event.EventShortDto;
import ru.practicum.ewm.model.entity.Compilation;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.EventService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    public CompilationDto addCompilation(UpdateCompilationRequest updateCompilationRequest) {
        if (updateCompilationRequest.getPinned() == null) {
            updateCompilationRequest.setPinned(false);
        }
        if (updateCompilationRequest.getTitle().length() > 50) {
            throw new IncorrectFieldException("Слишком длинное название, максимум 50 символов");
        }
        Compilation compilation = Compilation.builder()
                .events(updateCompilationRequest.getEvents())
                .title(updateCompilationRequest.getTitle())
                .pinned(updateCompilationRequest.getPinned())
                .build();
        compilationRepository.save(compilation);
        List<EventShortDto> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    public List<CompilationDto> getAllCompilations(boolean pinned, Pageable page) {
        List<Compilation> compilations = compilationRepository.getAllCompilations(pinned, page);
        List<CompilationDto> compilationsResponseDto = new ArrayList<>();
        for (Compilation compilation : compilations) {
            CompilationDto compilationDto = CompilationDto.builder()
                    .id(compilation.getId())
                    .events(eventService.getEventsByIds(compilation.getEvents()))
                    .pinned(compilation.isPinned())
                    .title(compilation.getTitle())
                    .build();
            compilationsResponseDto.add(compilationDto);
        }
        return compilationsResponseDto;
    }

    public void deleteCompilation(long compId) {
        checkForExistCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoDataException("Компиляция не найдена"));
        ;
        List<Long> ids;
        if (updateCompilationRequest.getEvents() != null) {
            ids = updateCompilationRequest.getEvents();
        } else {
            ids = null;
        }
        if (updateCompilationRequest.getTitle() != null && updateCompilationRequest.getTitle().length() > 50) {
            throw new IncorrectFieldException("Слишком длинное название, максимум 50 символов");
        }
        Compilation newCompilationToSave = Compilation.builder()
                .id(compilation.getId())
                .title(updateCompilationRequest.getTitle() != null ? updateCompilationRequest.getTitle() : compilation.getTitle())
                .pinned(updateCompilationRequest.getPinned() != null ? updateCompilationRequest.getPinned() : compilation.isPinned())
                .events(ids)
                .build();
        List<EventShortDto> events = eventService.getEventsByIds(ids);
        compilationRepository.save(newCompilationToSave);
        return CompilationDto.builder()
                .id(newCompilationToSave.getId())
                .events(events)
                .pinned(newCompilationToSave.isPinned())
                .title(newCompilationToSave.getTitle())
                .build();
    }

    public CompilationDto getCompilationResponseDtoById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NoDataException("Компиляция не найдена"));
        ;
        List<EventShortDto> events = eventService.getEventsByIds(compilation.getEvents());
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    private void checkForExistCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NoDataException("Подборка не найдена");
        }
    }
}
