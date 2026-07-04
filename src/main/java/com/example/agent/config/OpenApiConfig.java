package com.example.agent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springAiAgentOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring AI Agent API")
                        .description("REST API for interacting with an agentic AI assistant powered by Spring AI and Claude (Anthropic). " +
                                "The agent supports tool-calling, conversation memory, and a rich set of built-in tools.")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Pandurang")
                                .email("pgiriyammanavar@deloitte.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}
