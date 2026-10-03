package com.project.fitness;

import com.project.fitness.model.User;
import com.project.fitness.model.UserRole;
import com.project.fitness.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FitnessAuthIntegrationTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;

    @Test
    void cookieAuthenticationScopesActivityAndEnforcesRoles() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String firstEmail = "first-" + suffix + "@example.test";
        String secondEmail = "second-" + suffix + "@example.test";

        Csrf csrf = csrf();
        MvcResult signup = mockMvc.perform(post("/api/auth/register")
                        .cookie(csrf.cookie()).header("X-XSRF-TOKEN", csrf.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"a-secure-test-password","firstName":"Ari","lastName":"Runner"}
                                """.formatted(firstEmail)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();
        String firstId = jsonString(signup, "id");

        Cookie firstSession = login(firstEmail, "a-secure-test-password", csrf);
        mockMvc.perform(get("/api/auth/me").cookie(firstSession))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(firstId));

        MvcResult createdActivity = mockMvc.perform(post("/api/activities")
                        .cookie(csrf.cookie(), firstSession).header("X-XSRF-TOKEN", csrf.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"RUNNING","duration":32,"caloriesBurned":260,"startTime":"2026-10-04T06:30:00","additionalMetrics":{}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(firstId))
                .andReturn();
        String activityId = jsonString(createdActivity, "id");
        mockMvc.perform(get("/api/dashboard/summary").cookie(firstSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActivities").value(1))
                .andExpect(jsonPath("$.recentActivities[0].type").value("RUNNING"));

        mockMvc.perform(get("/api/admin/overview").cookie(firstSession))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/auth/register")
                        .cookie(csrf.cookie()).header("X-XSRF-TOKEN", csrf.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"another-secure-password","firstName":"Sam","lastName":"Walker"}
                                """.formatted(secondEmail)))
                .andExpect(status().isCreated());
        Cookie secondSession = login(secondEmail, "another-secure-password", csrf);
        mockMvc.perform(get("/api/activities").cookie(secondSession))
                .andExpect(status().isOk()).andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
        mockMvc.perform(post("/api/recommendations/{activityId}/generate", activityId)
                        .cookie(csrf.cookie(), secondSession).header("X-XSRF-TOKEN", csrf.value()))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/recommendations/{activityId}/generate", activityId)
                        .cookie(csrf.cookie(), firstSession).header("X-XSRF-TOKEN", csrf.value()))
                .andExpect(status().isServiceUnavailable());

        User admin = userRepository.findByEmail(firstEmail);
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
        Cookie adminSession = login(firstEmail, "a-secure-test-password", csrf);
        mockMvc.perform(get("/api/admin/overview").cookie(adminSession))
                .andExpect(status().isNotFound());

        MvcResult logout = mockMvc.perform(post("/api/auth/logout")
                        .cookie(csrf.cookie(), adminSession).header("X-XSRF-TOKEN", csrf.value()))
                .andExpect(status().isNoContent()).andReturn();
        assertThat(logout.getResponse().getHeader("Set-Cookie")).contains("Max-Age=0");
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    private Cookie login(String email, String password, Csrf csrf) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .cookie(csrf.cookie()).header("X-XSRF-TOKEN", csrf.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password)))
                .andExpect(status().isOk()).andReturn();
        Cookie session = result.getResponse().getCookie("FITTRACKER_AUTH");
        assertThat(session).isNotNull();
        assertThat(result.getResponse().getHeader("Set-Cookie")).contains("HttpOnly", "SameSite=Lax");
        return session;
    }

    private Csrf csrf() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        Cookie cookie = result.getResponse().getCookie("XSRF-TOKEN");
        assertThat(cookie).isNotNull();
        return new Csrf(cookie, cookie.getValue());
    }

    private String jsonString(MvcResult result, String field) throws Exception {
        return com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$." + field);
    }

    private record Csrf(Cookie cookie, String value) {}
}
