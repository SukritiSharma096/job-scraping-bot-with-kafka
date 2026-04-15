package com.Universal_bot_jobs.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Universal Bot Jobs API")
                        .version("1.0")
                        .description("API documentation for Universal Job Scraping Bot"));
    }
}
