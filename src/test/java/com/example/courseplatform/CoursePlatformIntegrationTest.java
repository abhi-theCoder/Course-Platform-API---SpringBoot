package com.example.courseplatform;

import com.example.courseplatform.dto.LoginRequest;
import com.example.courseplatform.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CoursePlatformIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFullFlow() throws Exception {
        // 1. Register User
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 2. Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = loginResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
        String token = (String) responseMap.get("token");

        // 3. List Courses (Public)
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses", hasSize(greaterThan(0))));

        // 4. Enroll (Authenticated)
        mockMvc.perform(post("/api/courses/physics-101/enroll")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        // 5. Search (Public)
        mockMvc.perform(get("/api/search")
                .param("q", "velocity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(greaterThan(0))));

        // 6. Mark Subtopic Complete (Authenticated)
        mockMvc.perform(post("/api/subtopics/velocity/complete")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));

        // 7. Get Progress (Authenticated)
        // Need enrollment ID, but let's just cheat and assume it's 1 since it's the
        // first one in H2
        mockMvc.perform(get("/api/enrollments/1/progress")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedSubtopics", is(1)));
    }
}
