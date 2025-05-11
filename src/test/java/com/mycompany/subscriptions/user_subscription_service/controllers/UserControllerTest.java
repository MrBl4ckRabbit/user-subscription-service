package com.mycompany.subscriptions.user_subscription_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.subscriptions.user_subscription_service.services.UserService;
import com.mycompany.subscriptions.user_subscription_service.services.dto.UserDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDTO request = UserDTO.builder()
                .name("Rustam")
                .email("rustam@example.com")
                .build();

        UserDTO response = UserDTO.builder()
                .id(1L)
                .name("Rustam")
                .email("rustam@example.com")
                .build();

        Mockito.when(userService.createUser(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rustam"))
                .andExpect(jsonPath("$.email").value("rustam@example.com"));
    }

    @Test
    void createUser_ShouldReturn400_WhenNameBlank() throws Exception {
        mockMvc.perform(post("/api/users")
                        .content("{ \"name\": \"\", \"email\": \"valid@email.com\" }")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_ShouldReturnUserInfo() throws Exception {
        UserDTO user = UserDTO.builder()
                .id(1L)
                .name("Rustam")
                .email("rustam@example.com")
                .build();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rustam"))
                .andExpect(jsonPath("$.email").value("rustam@example.com"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
