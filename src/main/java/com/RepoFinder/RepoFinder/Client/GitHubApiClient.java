
package com.RepoFinder.RepoFinder.Client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubApiClient {

    private final RestClient restClient;

    public GitHubApiClient(RestClient githubRestClient) {
        this.restClient = githubRestClient;
    }

    public GitHubApiResponse searchRepositories(
            String query,
            String language,
            String sort) {

        String searchQuery = query;

        if (language != null && !language.isBlank()) {
            searchQuery = searchQuery + " language:" + language;
        }


        final String finalSearchQuery = searchQuery;

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", finalSearchQuery)
                        .queryParam("sort", sort)
                        .queryParam("order", "desc")
                        .queryParam("per_page", 30)
                        .build())
                .retrieve()
                .body(GitHubApiResponse.class);
    }
}
