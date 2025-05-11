package com.mycompany.subscriptions.user_subscription_service.exception.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
