package com.raichung.shiftledger;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void noTokenNoAccess() throws Exception {
        mvc.perform(get("/api/v1/jobs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signUpThenLogIn() throws Exception {
        String email = randomEmail();
        String token = signUpAndLogIn(email);

        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        String email = randomEmail();
        signUpAndLogIn(email);

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"not-my-password"}
                                """.formatted(email)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saturdayShiftGetsSaturdayRate() throws Exception {
        String token = signUpAndLogIn(randomEmail());
        long jobId = createJob(token);

        // 5pm to 10:30pm with a 30 min break = 5h at 36.60
        postShift(token, jobId, "2026-11-07T17:00:00+10:30", "2026-11-07T22:30:00+10:30", 30)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shift.pay").value(183.00))
                .andExpect(jsonPath("$.warnings").isEmpty());
    }

    @Test
    void christmasDayGetsHolidayRate() throws Exception {
        // holiday dates come from the V3 migration, so this hits the real table
        String token = signUpAndLogIn(randomEmail());
        long jobId = createJob(token);

        postShift(token, jobId, "2026-12-25T10:00:00+10:30", "2026-12-25T16:00:00+10:30", 0)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shift.pay").value(366.00));
    }

    @Test
    void overlappingShiftIsBlocked() throws Exception {
        String token = signUpAndLogIn(randomEmail());
        long jobId = createJob(token);

        postShift(token, jobId, "2026-11-07T17:00:00+10:30", "2026-11-07T22:30:00+10:30", 0)
                .andExpect(status().isCreated());

        postShift(token, jobId, "2026-11-07T20:00:00+10:30", "2026-11-07T23:00:00+10:30", 0)
                .andExpect(status().isConflict());
    }

    @Test
    void cantTouchSomeoneElsesJob() throws Exception {
        String alice = signUpAndLogIn(randomEmail());
        long alicesJob = createJob(alice);
        String bob = signUpAndLogIn(randomEmail());

        mvc.perform(get("/api/v1/jobs").header("Authorization", "Bearer " + bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        postShift(bob, alicesJob, "2026-11-10T09:00:00+10:30", "2026-11-10T17:00:00+10:30", 0)
                .andExpect(status().isNotFound());
    }

    // helpers

    private String signUpAndLogIn(String email) throws Exception {
        mvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"test-password-1","displayName":"Test"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        String body = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"test-password-1"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(body, "$.accessToken");
    }

    private long createJob(String token) throws Exception {
        String body = mvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"employerName":"Test Cafe","weekdayRate":30.50,"saturdayRate":36.60,
                                 "sundayRate":42.70,"publicHolidayRate":61.00}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Number id = JsonPath.read(body, "$.id");
        return id.longValue();
    }

    private ResultActions postShift(String token, long jobId, String start, String end, int breakMinutes)
            throws Exception {
        return mvc.perform(post("/api/v1/shifts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"jobId":%d,"startTime":"%s","endTime":"%s","breakMinutes":%d}
                        """.formatted(jobId, start, end, breakMinutes)));
    }

    // new user per test so tests don't depend on each other
    private static String randomEmail() {
        return "test-" + UUID.randomUUID() + "@example.com";
    }
}