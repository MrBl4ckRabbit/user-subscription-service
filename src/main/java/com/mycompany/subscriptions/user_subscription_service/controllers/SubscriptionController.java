package com.mycompany.subscriptions.user_subscription_service.controllers;

import com.mycompany.subscriptions.user_subscription_service.services.SubscriptionService;
import com.mycompany.subscriptions.user_subscription_service.services.dto.PopularSubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDTO> addSubscription(
            @PathVariable Long userId,
            @RequestBody SubscriptionRequest request) {
        request.setUserId(userId);
        log.info("Adding subscription for user ID: {}", userId);
        SubscriptionDTO subscription = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptions(@PathVariable Long userId) {
        log.debug("Fetching subscriptions for user ID: {}", userId);
        List<SubscriptionDTO> subscriptions = subscriptionService.getSubscriptionsByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @Transactional
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscription(
            @PathVariable Long userId,
            @PathVariable Long subscriptionId) {
        log.warn("Deleting subscription ID: {} for user ID: {}", subscriptionId, userId);
        subscriptionService.deleteSubscription(subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<PopularSubscriptionDTO>> getTopSubscriptions() {
        log.info("Fetching top 3 popular subscriptions");
        List<PopularSubscriptionDTO> topSubscriptions = subscriptionService.getTop3PopularSubscriptions();
        return ResponseEntity.ok(topSubscriptions);
    }

}
