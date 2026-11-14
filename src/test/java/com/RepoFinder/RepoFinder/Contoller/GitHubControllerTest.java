
package com.RepoFinder.RepoFinder.Controller;

import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Service.GitHubService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GitHubControllerTest {

    @Mock
    private GitHubService githubService;

    @InjectMocks
    private GitHubController githubController;

    private MockMvc mockMvc;

    // Test 1: Search repositories successfully
    @Test
    void shouldSearchRepositoriesSuccessfully() throws Exception {

        mockMvc = MockMvcBuilders
                .standaloneSetup(githubController)
                .build();

        GithubEntity entity = createRepository();

        when(githubService.searchRepositories(
                "spring boot", "Java", "stars"))
                .thenReturn(List.of(entity));

        mockMvc.perform(post("/api/github/search")
                        .param("query", "spring boot")
                        .param("language", "Java")
                        .param("sort", "stars"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(123))
                .andExpect(jsonPath("$[0].RepoName").value("spring-boot"))
                .andExpect(jsonPath("$[0].language").value("Java"))
                .andExpect(jsonPath("$[0].stars").value(1000));

        verify(githubService).searchRepositories(
                "spring boot", "Java", "stars");
    }

    // Test 2: Search without optional parameters
    @Test
    void shouldSearchRepositoriesWithoutOptionalParameters()
            throws Exception {

        mockMvc = MockMvcBuilders
                .standaloneSetup(githubController)
                .build();

        when(githubService.searchRepositories(
                "spring boot", null, "stars"))
                .thenReturn(List.of());

        mockMvc.perform(post("/api/github/search")
                        .param("query", "spring boot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(githubService).searchRepositories(
                "spring boot", null, "stars");
    }

    // Test 3: Get all repositories successfully
    @Test
    void shouldGetRepositoriesSuccessfully() throws Exception {

        mockMvc = MockMvcBuilders
                .standaloneSetup(githubController)
                .build();

        GithubEntity entity = createRepository();

        when(githubService.getRepositories(
                null, null, null, "stars"))
                .thenReturn(List.of(entity));

        mockMvc.perform(get("/api/github/repositories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(123))
                .andExpect(jsonPath("$[0].RepoName").value("spring-boot"))
                .andExpect(jsonPath("$[0].stars").value(1000));

        verify(githubService).getRepositories(
                null, null, null, "stars");
    }

    // Test 4: Get repositories with filters
    @Test
    void shouldGetRepositoriesWithFilters() throws Exception {

        mockMvc = MockMvcBuilders
                .standaloneSetup(githubController)
                .build();

        when(githubService.getRepositories(
                "Java", 100, 200, "stars,desc"))
                .thenReturn(List.of(createRepository()));

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("minForks", "200")
                        .param("sort", "stars,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(githubService).getRepositories(
                "Java", 100, 200, "stars,desc");
    }

    // Test 5: Return empty list when no repositories exist
    @Test
    void shouldReturnEmptyListWhenNoRepositoriesFound()
            throws Exception {

        mockMvc = MockMvcBuilders
                .standaloneSetup(githubController)
                .build();

        when(githubService.getRepositories(
                null, null, null, "stars"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/github/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // Test 6: Return HTTP 400 when required query is missing
    @Test
    void shouldReturnBadRequestWhenQueryIsMissing()
            throws Exception {

        mockMvc = MockMvcBuilders
                .standaloneSetup(githubController)
                .build();

        mockMvc.perform(post("/api/github/search"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(githubService);
    }

    // Helper method to create repository entity
    private GithubEntity createRepository() {

        GithubEntity entity = new GithubEntity();

        entity.setId(123L);
        entity.setRepoName("spring-boot");
        entity.setDescription("Spring Boot framework");
        entity.setOwner("spring-projects");
        entity.setLanguage("Java");
        entity.setStars(1000);
        entity.setForks(500);

        return entity;
    }
}
