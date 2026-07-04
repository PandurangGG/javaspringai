package com.example.agent;

import com.example.agent.service.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    // Use a dummy key so the app context loads without a real API key during unit tests.
    // Replace with a real key or use @MockBean for integration tests.
    "spring.ai.anthropic.api-key=test-key"
})
class AgentServiceTest {

    @Autowired(required = false)
    AgentService agentService;

    @Test
    void contextLoads() {
        // Verifies Spring context assembles correctly
        assertThat(agentService).isNotNull();
    }
}
