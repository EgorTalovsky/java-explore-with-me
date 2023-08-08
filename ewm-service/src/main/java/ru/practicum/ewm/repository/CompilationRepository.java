package ru.practicum.ewm.repository;

import ru.practicum.ewm.model.entity.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("SELECT c FROM Compilation c WHERE c.pinned = :pinned")
    List<Compilation> getAllCompilations(boolean pinned, Pageable page);
}
