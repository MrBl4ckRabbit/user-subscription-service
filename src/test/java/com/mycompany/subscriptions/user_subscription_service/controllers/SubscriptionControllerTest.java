package com.mycompany.subscriptions.user_subscription_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.subscriptions.user_subscription_service.services.SubscriptionService;
import com.mycompany.subscriptions.user_subscription_service.services.dto.PopularSubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionDTO;
import com.mycompany.subscriptions.user_subscription_service.services.dto.SubscriptionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @Test
    void getTopSubscriptions_ShouldReturnTop3() throws Exception {
        String endpoint = "/api/users/{userId}/subscriptions/top";

        when(subscriptionService.getTop3PopularSubscriptions()).thenReturn(List.of(
                new PopularSubscriptionDTO("Netflix", 100L),
                new PopularSubscriptionDTO("Spotify", 80L),
                new PopularSubscriptionDTO("Amazon Prime", 60L)
        ));

        mockMvc.perform(MockMvcRequestBuilders.get(endpoint, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[0].serviceName").value("Netflix"))
                .andExpect(jsonPath("$[0].count").value(100))
                .andExpect(jsonPath("$[1].serviceName").value("Spotify"))
                .andExpect(jsonPath("$[2].serviceName").value("Amazon Prime"));
    }


    @Test
    void addSubscription_ShouldReturnCreatedSubscription() throws Exception {
        SubscriptionRequest request = SubscriptionRequest.builder()
                .serviceName("Netflix")
                .startDate(LocalDate.now())
                .userId(1L)
                .build();

        SubscriptionDTO response = SubscriptionDTO.builder()
                .id(100L)
                .serviceName("Netflix")
                .startDate(LocalDate.now())
                .userId(1L)
                .build();

        when(subscriptionService.createSubscription(Mockito.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/1/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.serviceName").value("Netflix"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void getUserSubscriptions_ShouldReturnListOfSubscriptions() throws Exception {
        SubscriptionDTO subscription = SubscriptionDTO.builder()
                .id(101L)
                .serviceName("Spotify")
                .startDate(LocalDate.now())
                .userId(1L)
                .build();

        when(subscriptionService.getSubscriptionsByUserId(1L)).thenReturn(List.of(subscription));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].serviceName").value("Spotify"));
    }

    @Test
    void deleteSubscription_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(subscriptionService).deleteSubscription(100L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1/subscriptions/100"))
                .andExpect(status().isNoContent());
    }
}
