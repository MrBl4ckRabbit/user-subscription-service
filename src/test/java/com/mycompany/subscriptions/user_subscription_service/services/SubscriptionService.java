package com.mycompany.subscriptions.user_subscription_service.services;

import com.mycompany.subscriptions.user_subscription_service.exception.exceptions.ResourceNotFoundException;
import com.mycompany.subscriptions.user_subscription_service.models.Subscription;
import com.mycompany.subscriptions.user_subscription_service.models.User;
import com.mycompany.subscriptions.user_subscription_service.repositories.SubscriptionRepository;
import com.mycompany.subscriptions.user_subscription_service.repositories.UserRepository;
import com.mycompany.subscriptions.user_subscription_service.services.dto.PopularSubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void createSubscription_ShouldCreateSubscription_WhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        SubscriptionRequest request = SubscriptionRequest.builder()
                .serviceName("Netflix")
                .startDate(LocalDate.now())
                .userId(1L)
                .build();

        Subscription subscription = Subscription.builder()
                .id(100L)
                .serviceName(request.getServiceName())
                .startDate(request.getStartDate())
                .user(user)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionDTO result = subscriptionService.createSubscription(request);

        assertNotNull(result);
        assertEquals("Netflix", result.getServiceName());
        assertEquals(1L, result.getUserId());
    }

    @Test
    void createSubscription_ShouldThrowException_WhenUserNotFound() {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .userId(999L)
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.createSubscription(request));
        verifyNoInteractions(subscriptionRepository);
    }


    @Test
    void getSubscriptionsByUserId_ShouldReturnSubscriptions_WhenUserExists() {
        User user = new User();
        user.setId(1L);

        Subscription subscription = new Subscription();
        subscription.setId(100L);
        subscription.setServiceName("Spotify");
        subscription.setUser(user);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(subscriptionRepository.findByUserId(1L)).thenReturn(List.of(subscription));

        List<SubscriptionDTO> result = subscriptionService.getSubscriptionsByUserId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Spotify", result.get(0).getServiceName());
    }

    @Test
    void deleteSubscription_ShouldDeleteSubscription_WhenExists() {
        when(subscriptionRepository.existsById(100L)).thenReturn(true);

        subscriptionService.deleteSubscription(100L);

        verify(subscriptionRepository, times(1)).deleteById(100L);
    }

    @Test
    void deleteSubscription_ShouldThrowException_WhenSubscriptionNotFound() {
        when(subscriptionRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.deleteSubscription(999L));
    }

    @Test
    void getTop3PopularSubscriptions_ShouldReturnTopSubscriptions() {
        when(subscriptionRepository.findPopularSubscriptions()).thenReturn(List.of(
                new PopularSubscriptionDTO("Netflix", 50L),
                new PopularSubscriptionDTO("Spotify", 40L),
                new PopularSubscriptionDTO("Amazon Prime", 30L)
        ));

        List<PopularSubscriptionDTO> result = subscriptionService.getTop3PopularSubscriptions();

        assertEquals(3, result.size());
        assertEquals("Netflix", result.get(0).getServiceName());
    }
}
