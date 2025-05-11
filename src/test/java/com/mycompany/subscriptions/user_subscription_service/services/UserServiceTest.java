package com.mycompany.subscriptions.user_subscription_service.services;

import com.mycompany.subscriptions.user_subscription_service.exception.exceptions.DuplicateEmailException;
import com.mycompany.subscriptions.user_subscription_service.exception.exceptions.ResourceNotFoundException;
import com.mycompany.subscriptions.user_subscription_service.models.User;
import com.mycompany.subscriptions.user_subscription_service.repositories.UserRepository;
import com.mycompany.subscriptions.user_subscription_service.services.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "Test User";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ShouldReturnSavedUser_WhenDataValid() {
        User user = createTestUser();
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        UserDTO result = userService.createUser(user);

        assertThat(result)
                .extracting(UserDTO::getName, UserDTO::getEmail)
                .containsExactly(TEST_NAME, TEST_EMAIL);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        User user = createTestUser();
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(TEST_USER_ID);

        assertThat(result.getId()).isEqualTo(TEST_USER_ID);
    }

    @Test
    void updateUser_ShouldUpdateFields_WhenUserExists() {
        User existingUser = createTestUser();
        User updates = new User();
        updates.setName("New Name");
        updates.setEmail("new@example.com");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.updateUser(TEST_USER_ID, updates);

        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
        doNothing().when(userRepository).deleteById(TEST_USER_ID);

        userService.deleteUser(TEST_USER_ID);

        verify(userRepository).deleteById(TEST_USER_ID);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user1 = createTestUser();
        User user2 = createTestUser(2L, "another@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        assertThat(userService.getAllUsers())
                .extracting(UserDTO::getEmail)
                .containsExactlyInAnyOrder(TEST_EMAIL, "another@example.com");
    }

    private User createTestUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setName(TEST_NAME);
        user.setEmail(email);
        return user;
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        User user = createTestUser();
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(TEST_USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(TEST_USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    private User createTestUser() {
        return User.builder()
                .id(TEST_USER_ID)
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .build();
    }
}