package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.entity.request.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT r FROM Request r WHERE r.event.id = :eventId AND r.requester.id = :requesterId")
    Optional<Request> findUserRequestToEvent(long eventId, long requesterId);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :userId")
    List<Request> findUserRequests(long userId);

    @Query("SELECT r FROM Request r WHERE r.event.id = :eventId")
    List<Request> findRequestsToEvent(long eventId);

    @Query("SELECT r FROM Request r WHERE r.id IN :ids")
    List<Request> findRequestsByIds(List<Long> ids);
}
