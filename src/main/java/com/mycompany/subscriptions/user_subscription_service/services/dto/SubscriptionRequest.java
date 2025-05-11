package com.mycompany.subscriptions.user_subscription_service.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Builder
@Data
public class SubscriptionRequest {
    @NotBlank
    private String serviceName;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Long userId;
}
