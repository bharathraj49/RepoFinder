
package com.RepoFinder.RepoFinder.Integration;

import com.RepoFinder.RepoFinder.Client.GitHubApiClient;
import com.RepoFinder.RepoFinder.Client.GitHubApiResponse;
import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Repository.GithubRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasItem;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GitHubIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GithubRepository repository;

    // Mock only the external GitHub API
    @MockitoBean
    private GitHubApiClient githubApiClient;

    private static final Long TEST_REPO_ID = 987654321L;

    @BeforeEach
    void setUp() {
        repository.deleteById(TEST_REPO_ID);
    }

    @AfterEach
    void cleanUp() {
        repository.deleteById(TEST_REPO_ID);
    }

    // Test 1: Search GitHub and save repository into PostgreSQL
    @Test
    void shouldSearchAndSaveRepository() throws Exception {

        GitHubApiResponse response =
                mock(GitHubApiResponse.class);

        GitHubApiResponse.Item item =
                mock(GitHubApiResponse.Item.class);

        GitHubApiResponse.Owner owner =
                mock(GitHubApiResponse.Owner.class);

        when(githubApiClient.searchRepositories(
                "integration-test", "Java", "stars"))
                .thenReturn(response);

        when(response.getItems())
                .thenReturn(List.of(item));

        when(item.getId()).thenReturn(TEST_REPO_ID);
        when(item.getName()).thenReturn("integration-repo");
        when(item.getDescription())
                .thenReturn("Integration test repository");
        when(item.getOwner()).thenReturn(owner);
        when(owner.getLogin()).thenReturn("test-owner");
        when(item.getLanguage()).thenReturn("Java");
        when(item.getStars()).thenReturn(500);
        when(item.getForks()).thenReturn(100);
        when(item.getLastUpdated()).thenReturn(null);

        mockMvc.perform(post("/api/github/search")
                        .param("query", "integration-test")
                        .param("language", "Java")
                        .param("sort", "stars"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id")
                        .value(TEST_REPO_ID))
                .andExpect(jsonPath("$[0].RepoName")
                        .value("integration-repo"))
                .andExpect(jsonPath("$[0].language")
                        .value("Java"))
                .andExpect(jsonPath("$[0].stars")
                        .value(500));

        // Verify that the repository was actually saved in PostgreSQL
        GithubEntity savedEntity =
                repository.findById(TEST_REPO_ID).orElseThrow();

        assertEquals("integration-repo", savedEntity.getRepoName());
        assertEquals(500, savedEntity.getStars());
    }

    // Test 2: Retrieve repositories from PostgreSQL
    @Test
    void shouldRetrieveRepositoriesFromDatabase() throws Exception {

        GithubEntity entity = createRepository();

        repository.save(entity);

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("sort", "stars,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                // Check whether our repository exists anywhere in the response
                .andExpect(jsonPath("$[*].id",
                        hasItem(TEST_REPO_ID.intValue())))
                .andExpect(jsonPath("$[*].RepoName",
                        hasItem("integration-repo")))
                .andExpect(jsonPath("$[*].stars",
                        hasItem(500)));
    }


    // Test 3: Return empty list when GitHub API has no results
    @Test
    void shouldHandleEmptyGitHubSearchResults() throws Exception {

        GitHubApiResponse response =
                mock(GitHubApiResponse.class);

        when(githubApiClient.searchRepositories(
                "no-results", null, "stars"))
                .thenReturn(response);

        when(response.getItems())
                .thenReturn(List.of());

        mockMvc.perform(post("/api/github/search")
                        .param("query", "no-results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(githubApiClient).searchRepositories(
                "no-results", null, "stars");
    }

    // Test 4: Reject request when query parameter is missing
    @Test
    void shouldReturnBadRequestWhenQueryIsMissing()
            throws Exception {

        mockMvc.perform(post("/api/github/search"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(githubApiClient);
    }

    // Helper method
    private GithubEntity createRepository() {

        GithubEntity entity = new GithubEntity();

        entity.setId(TEST_REPO_ID);
        entity.setRepoName("integration-repo");
        entity.setDescription("Integration test repository");
        entity.setOwner("test-owner");
        entity.setLanguage("Java");
        entity.setStars(500);
        entity.setForks(100);
        entity.setLastUpdated(Instant.now());

        return entity;
    }
}
