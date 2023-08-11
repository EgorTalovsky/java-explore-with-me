package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.entity.Subscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT s FROM Subscription s WHERE s.subscriber.id = :subscriberId AND initiator.id = :initiatorId " +
            "AND s.isSubscribed = :isSubscribed")
    Optional<Subscription> findSubscription(long initiatorId, long subscriberId, boolean isSubscribed);

    @Query("SELECT s FROM Subscription s WHERE s.subscriber.id = :subscriberId AND s.isSubscribed = true")
    Optional<List<Subscription>> findTrueSubscriptionsBySubscriberId(long subscriberId);
}