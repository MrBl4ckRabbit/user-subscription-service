package com.mycompany.subscriptions.user_subscription_service.repositories;

import com.mycompany.subscriptions.user_subscription_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
