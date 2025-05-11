package com.mycompany.subscriptions.user_subscription_service.repositories;

import com.mycompany.subscriptions.user_subscription_service.models.Subscription;
import com.mycompany.subscriptions.user_subscription_service.services.dto.PopularSubscriptionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);

    @Query("SELECT new com.mycompany.subscriptions.user_subscription_service.services.dto.PopularSubscriptionDTO(s.serviceName, COUNT(s)) FROM Subscription s GROUP BY s.serviceName ORDER BY COUNT(s) DESC")
    List<PopularSubscriptionDTO> findPopularSubscriptions();
}
