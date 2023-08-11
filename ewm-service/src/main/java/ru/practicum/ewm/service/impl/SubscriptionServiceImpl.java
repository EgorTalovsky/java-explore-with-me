package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.IncorrectFieldException;
import ru.practicum.ewm.exception.NoDataException;
import ru.practicum.ewm.model.dto.SubscriptionDto;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.model.entity.Subscription;
import ru.practicum.ewm.model.entity.User;
import ru.practicum.ewm.model.entity.event.Event;
import ru.practicum.ewm.model.entity.event.EventState;
import ru.practicum.ewm.model.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.SubscriptionRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.model.mapper.EventMapper.toDateFromString;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public SubscriptionDto subscribeToInitiator(SubscriptionDto subscriptionDto) {
        if (subscriptionRepository.findSubscription(subscriptionDto.getInitiatorId(), subscriptionDto.getSubscriberId(),
                subscriptionDto.getIsSubscribe()).isPresent()) {
            throw new IncorrectFieldException("Вы уже подписаны на пользователя");
        }
        checkSubscriptionFields(subscriptionDto, true);
        List<User> users = makeInitiatorAndSubscriber(subscriptionDto);
        subscriptionDto.setTimestamp(LocalDateTime.now());
        Subscription subscription = Subscription.builder()
                .isSubscribed(subscriptionDto.getIsSubscribe())
                .timestamp(subscriptionDto.getTimestamp())
                .initiator(users.get(0))
                .subscriber(users.get(1))
                .build();;
        subscriptionRepository.save(subscription);
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .subscriberId(subscription.getSubscriber().getId())
                .initiatorId(subscription.getInitiator().getId())
                .isSubscribe(subscription.isSubscribed())
                .timestamp(subscription.getTimestamp())
                .build();
    }

    public SubscriptionDto unsubscribeToInitiator(SubscriptionDto subscriptionDto) {
        Subscription subscription = subscriptionRepository.findSubscription(subscriptionDto.getInitiatorId(),
                subscriptionDto.getSubscriberId(), true).orElse(null);
        if (subscription == null) {
            throw new IncorrectFieldException("Вы не подписаны на этого инициатора");
        }
        checkSubscriptionFields(subscriptionDto, false);
        subscriptionDto.setTimestamp(LocalDateTime.now());
        Subscription subscriptionUpdate = Subscription.builder()
                .id(subscription.getId())
                .isSubscribed(subscriptionDto.getIsSubscribe())
                .timestamp(subscriptionDto.getTimestamp())
                .subscriber(subscription.getSubscriber())
                .initiator(subscription.getInitiator())
                .build();
        subscriptionRepository.save(subscriptionUpdate);
        return SubscriptionDto.builder()
                .id(subscriptionUpdate.getId())
                .subscriberId(subscriptionUpdate.getSubscriber().getId())
                .initiatorId(subscriptionUpdate.getInitiator().getId())
                .isSubscribe(subscriptionUpdate.isSubscribed())
                .timestamp(subscriptionUpdate.getTimestamp())
                .build();
    }

    public List<EventFullDto> getEventsByInitiatorId(long subscriberId, long initiatorId, String rangeStart, String rangeEnd,
                                                     String text, String sort, List<Long> categories, Pageable page) {
        if (subscriptionRepository.findSubscription(initiatorId, subscriberId, true).isEmpty()) {
            throw new IncorrectFieldException("Подписка не найдена");
        }
        List<Optional<LocalDateTime>> dates = makeDatesForSearch(rangeStart, rangeEnd);
        if (sort == null) {
            sort = "EVENT_DATE";
        }
        List<Event> events = eventRepository.findEventsByPublicSearchSubscriptions(
                Collections.singletonList(initiatorId),
                text,
                categories,
                dates.get(0).orElse(null),
                dates.get(1).orElse(null),
                EventState.PUBLISHED,
                page);
        if (sort.equals("EVENT_DATE")) {
            return events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .map(EventMapper::toEventFullDtoFromEvent)
                    .collect(Collectors.toList());
        } else if (sort.equals("VIEWS")) {
            return events.stream()
                    .sorted(Comparator.comparingLong(x -> -x.getViews()))
                    .map(EventMapper::toEventFullDtoFromEvent)
                    .collect(Collectors.toList());
        }
        throw new IncorrectFieldException("Неизвестный параметр сортировки");
    }

    public List<EventFullDto> getFeed(long subscriberId, String rangeStart, String rangeEnd,
                                      String text, String sort, List<Long> categories, Pageable page) {
        List<Subscription> subscriptions = subscriptionRepository.findTrueSubscriptionsBySubscriberId(subscriberId)
                .orElseThrow(() -> new NoDataException("Подписка не найдена"));
        List<Long> initiatorsId = subscriptions.stream()
                .map(x -> x.getInitiator().getId())
                .collect(Collectors.toList());
        List<Optional<LocalDateTime>> dates = makeDatesForSearch(rangeStart, rangeEnd);
        if (sort == null) {
            sort = "EVENT_DATE";
        }
        List<Event> events = eventRepository.findEventsByPublicSearchSubscriptions(initiatorsId, text,
                categories, dates.get(0).orElse(null), dates.get(1).orElse(null), EventState.PUBLISHED, page);
        if (sort.equals("EVENT_DATE")) {
            return events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .map(EventMapper::toEventFullDtoFromEvent)
                    .collect(Collectors.toList());
        } else if (sort.equals("VIEWS")) {
            return events.stream()
                    .sorted(Comparator.comparingLong(x -> -x.getViews()))
                    .map(EventMapper::toEventFullDtoFromEvent)
                    .collect(Collectors.toList());
        }
        throw new IncorrectFieldException("Неизвестный параметр сортировки");
    }

    private List<User> makeInitiatorAndSubscriber(SubscriptionDto subscriptionDto) {
        List<User> users = new ArrayList<>();
        User initiator = userRepository.findById(subscriptionDto.getInitiatorId())
                .orElseThrow(() -> new NoDataException("Пользователь не найден"));
        User subscriber = userRepository.findById(subscriptionDto.getSubscriberId())
                .orElseThrow(() -> new NoDataException("Подписчик не найден"));
        users.add(initiator);
        users.add(subscriber);
        return users;
    }

    private void checkSubscriptionFields(SubscriptionDto subscriptionDto, Boolean isSub) {
        if (subscriptionDto.getSubscriberId().equals(subscriptionDto.getInitiatorId())) {
            throw new IncorrectFieldException("Идентификаторы инициатора и подписчика совпадают");
        }

        if (!subscriptionDto.getIsSubscribe() && isSub) {
            throw new IncorrectFieldException("Отписка вместо подписки");
        }
        if (subscriptionDto.getIsSubscribe() && !isSub) {
            throw new IncorrectFieldException("Подписка вместо отписки");
        }
    }

    private List<Optional<LocalDateTime>> makeDatesForSearch(String rangeStart, String rangeEnd) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = null;
        if (rangeStart != null) {
            startDate = toDateFromString(rangeStart);
        }
        if (rangeEnd != null) {
            endDate = toDateFromString(rangeEnd);
        }
        return List.of(Optional.of(startDate), Optional.ofNullable(endDate));
    }
}
