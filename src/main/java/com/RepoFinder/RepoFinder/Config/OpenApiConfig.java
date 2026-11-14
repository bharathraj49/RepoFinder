package com.RepoFinder.RepoFinder.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GitHub Repository Searcher API")
                        .version("1.0.0")
                        .description(
                                "REST API to search GitHub repositories, " +
                                        "store repository details, and retrieve " +
                                        "repositories with filtering and sorting."
                        ));
    }
}