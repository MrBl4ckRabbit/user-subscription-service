package com.mycompany.subscriptions.user_subscription_service.services.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Builder
@Data
public class SubscriptionDTO {
    private Long id;
    private String serviceName;
    private LocalDate startDate;
    private Long userId;
}
