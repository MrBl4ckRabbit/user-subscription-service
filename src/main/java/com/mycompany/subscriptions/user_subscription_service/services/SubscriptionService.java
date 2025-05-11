package com.mycompany.subscriptions.user_subscription_service.services;


import com.mycompany.subscriptions.user_subscription_service.exception.exceptions.ResourceNotFoundException;
import com.mycompany.subscriptions.user_subscription_service.models.Subscription;
import com.mycompany.subscriptions.user_subscription_service.models.User;
import com.mycompany.subscriptions.user_subscription_service.repositories.SubscriptionRepository;
import com.mycompany.subscriptions.user_subscription_service.repositories.UserRepository;
import com.mycompany.subscriptions.user_subscription_service.services.dto.PopularSubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubscriptionDTO createSubscription(SubscriptionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("Creating subscription for user {}: {}", request.getUserId(), request.getServiceName());

        Subscription subscription = Subscription.builder()
                .serviceName(request.getServiceName())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .user(user)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return convertToDTO(savedSubscription);

    }

    public List<SubscriptionDTO> getSubscriptionsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        log.debug("Fetching subscriptions for user ID: {}", userId);

        return subscriptionRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public void deleteSubscription(Long subscriptionId) {
        if (!subscriptionRepository.existsById(subscriptionId)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + subscriptionId);
        }

        log.warn("Deleting subscription ID: {}", subscriptionId);

        subscriptionRepository.deleteById(subscriptionId);

        log.info("Subscription ID {} successfully deleted", subscriptionId);
    }

    public List<PopularSubscriptionDTO> getTop3PopularSubscriptions() {
        log.info("Fetching top 3 popular subscriptions");
        return subscriptionRepository.findPopularSubscriptions().stream().limit(3).toList();
    }

    private SubscriptionDTO convertToDTO(Subscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .serviceName(subscription.getServiceName())
                .startDate(subscription.getStartDate())
                .userId(subscription.getUser().getId())
                .build();
    }
}
