package com.RepoFinder.RepoFinder.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

public class RestClientConfig {
    @Bean
    public RestClient githubRestClient() {

        return RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(
                        "Accept",
                        "application/vnd.github+json"
                )
                .defaultHeader(
                        "User-Agent",
                        "RepoFinder"
                )
                .build();
    }
}
