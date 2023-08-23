package ru.practicum.ewm.controller.private_api;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.dto.SubscriptionDto;
import ru.practicum.ewm.model.dto.event.EventFullDto;
import ru.practicum.ewm.service.impl.SubscriptionServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/subscriptions")
public class PrivateSubscriptionController {
    private final SubscriptionServiceImpl subscriptionService;

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribeToInitiator(@RequestBody @Valid SubscriptionDto subscriptionDto) {
        return subscriptionService.subscribeToInitiator(subscriptionDto);
    }

    @PatchMapping("/unsubscribe")
    public SubscriptionDto unsubscribeToInitiator(@RequestBody @Valid SubscriptionDto subscriptionDto) {
        return subscriptionService.unsubscribeToInitiator(subscriptionDto);
    }

    @GetMapping("/search/initiator")
    public List<EventFullDto> getEventsByInitiatorId(@RequestParam long subscriberId,
                                                     @RequestParam long initiatorId,
                                                     @RequestParam(required = false) String rangeStart,
                                                     @RequestParam(required = false) String rangeEnd,
                                                     @RequestParam(required = false) String text,
                                                     @RequestParam(required = false) String sort,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable page = PageRequest.of(from / size, size);
        return subscriptionService.getEventsByInitiatorId(subscriberId, initiatorId, rangeStart, rangeEnd,
                text, sort, categories, page);
    }

    @GetMapping("/search/all")
    public List<EventFullDto> getFeed(@RequestParam long subscriberId,
                                      @RequestParam(required = false) String rangeStart,
                                      @RequestParam(required = false) String rangeEnd,
                                      @RequestParam(required = false) String text,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable page = PageRequest.of(from / size, size);
        return subscriptionService.getFeed(subscriberId, rangeStart, rangeEnd,
                text, sort, categories, page);
    }
}
