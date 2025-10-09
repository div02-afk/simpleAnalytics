package com.simpleAnalytics.Analytics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI analyticsOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8002");
        localServer.setDescription("Local development server");

        Contact contact = new Contact();
        contact.setName("Simple Analytics Team");
        contact.setEmail("support@simpleanalytics.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Analytics Service API")
                .version("1.0.0")
                .description("Analytics API for event ingestion system. Provides real-time analytics, metrics, and insights from event data stored in ClickHouse.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
