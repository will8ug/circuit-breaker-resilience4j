package io.will.circuitbreakerresilience4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class HelloControllerTest {
    @Autowired
    private MockMvc mvc;

    // GET /health
    @Test
    public void testHealth() throws Exception {
        ResultActions resultActions = mvc.perform(get("/health"));

        resultActions.andExpect(status().isNoContent());
    }

    // GET /hello-no-error
    @Test
    public void testCircuitBreaker_fallbackMethod() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            try {
                ResultActions resultActions = mvc.perform(get("/hello-no-error"));

                resultActions.andExpect(status().isOk());
                resultActions.andExpect(content().string(in(Arrays.asList("Hello", "Please try it out later"))));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

    // GET /hello-translated-error
    @Test
    public void testCircuitBreaker_translatedError() {
        IntStream.rangeClosed(1, 3).forEach(i -> {
            try {
                mvc.perform(get("/hello-translated-error"));

                fail("Should not come here");
            } catch (Exception e) {
                assertNotNull(e.getMessage());
                assertTrue(e.getMessage().contains("Test error for circuit breaker"));
            }
        });

        IntStream.rangeClosed(4, 10).forEach(i -> {
            try {
                ResultActions resultActions = mvc.perform(get("/hello-translated-error"));

                resultActions.andExpect(status().isServiceUnavailable());
                resultActions.andExpect(content().string("Please try it out later"));
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

}
