package ru.yirelav.bellintegratortask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasSize;

@EnableAutoConfiguration
class StatsApiTest extends BaseApiTest {

    @BeforeEach
    void BeforeEach() {
        articleRepository.deleteAll();
    }

    @WithMockUser(value = "test_admin", roles = "ADMIN")
    @Test
    void givenDailyStatsReq_shouldReturnLast7DaysStats() throws Exception {
        entityCreator.createNArticlesWithDateOfPublished(1, Instant.now().plus(1, ChronoUnit.DAYS));

        entityCreator.createNArticlesWithDateOfPublished(2, Instant.now());
        entityCreator.createNArticlesWithDateOfPublished(3, Instant.now().minus(1, ChronoUnit.DAYS));
        entityCreator.createNArticlesWithDateOfPublished(4, Instant.now().minus(2, ChronoUnit.DAYS));
        entityCreator.createNArticlesWithDateOfPublished(5, Instant.now().minus(3, ChronoUnit.DAYS));
        entityCreator.createNArticlesWithDateOfPublished(6, Instant.now().minus(4, ChronoUnit.DAYS));
        entityCreator.createNArticlesWithDateOfPublished(7, Instant.now().minus(5, ChronoUnit.DAYS));
        entityCreator.createNArticlesWithDateOfPublished(8, Instant.now().minus(6, ChronoUnit.DAYS));

        entityCreator.createNArticlesWithDateOfPublished(1, Instant.now().minus(7, ChronoUnit.DAYS));

        mockMvc.perform(MockMvcRequestBuilders.get("/articles/stats")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].count").value(8))
                .andExpect(MockMvcResultMatchers.jsonPath("$[6].count").value(2))
                .andDo(MockMvcResultHandlers.print());
    }

    @WithMockUser(value = "test_user")
    @Test
    void givenDailyStatsReqFromUser_shouldReturnForbiddenWith403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/articles/stats")
                        .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andDo(MockMvcResultHandlers.print());
    }

}
