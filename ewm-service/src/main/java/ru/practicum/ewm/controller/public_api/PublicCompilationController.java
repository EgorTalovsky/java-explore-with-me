package ru.practicum.ewm.controller.public_api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.dto.compilation.CompilationDto;
import ru.practicum.ewm.service.CompilationService;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationResponseDtoById(@PathVariable @Min(0) long compId) {
        return compilationService.getCompilationResponseDtoById(compId);
    }

    @GetMapping()
    public List<CompilationDto> getCompilations(@RequestParam(required = false) boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        Pageable page = PageRequest.of(from / size, size);
        return compilationService.getAllCompilations(pinned, page);
    }
}
