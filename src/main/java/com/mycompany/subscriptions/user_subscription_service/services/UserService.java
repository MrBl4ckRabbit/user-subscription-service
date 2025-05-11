package com.mycompany.subscriptions.user_subscription_service.services;

import com.mycompany.subscriptions.user_subscription_service.exception.exceptions.DuplicateEmailException;
import com.mycompany.subscriptions.user_subscription_service.exception.exceptions.ResourceNotFoundException;
import com.mycompany.subscriptions.user_subscription_service.models.User;
import com.mycompany.subscriptions.user_subscription_service.repositories.UserRepository;
import com.mycompany.subscriptions.user_subscription_service.services.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDTO createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Attempt to create user with duplicate email: {}", user.getEmail());
            throw new DuplicateEmailException("Email already in use");
        }

        log.debug("Saving new user to database: {}", user.getEmail());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public UserDTO updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        return convertToDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
