package com.mycompany.subscriptions.user_subscription_service.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PopularSubscriptionDTO {
    private String serviceName;
    private Long count;
}
