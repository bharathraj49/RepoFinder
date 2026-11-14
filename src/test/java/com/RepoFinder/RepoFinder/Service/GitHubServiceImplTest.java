
package com.RepoFinder.RepoFinder.Service;

import com.RepoFinder.RepoFinder.Client.GitHubApiClient;
import com.RepoFinder.RepoFinder.Client.GitHubApiResponse;
import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Repository.GithubRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubServiceImplTest {

    @Mock
    private GitHubApiClient githubApiClient;

    @Mock
    private GithubRepository repository;

    @InjectMocks
    private GitHubServiceImpl githubService;

    // Test 1: Fetch and save repositories successfully
    @Test
    void shouldFetchAndSaveRepositoriesSuccessfully() {

        GitHubApiResponse response = mock(GitHubApiResponse.class);
        GitHubApiResponse.Item item = createItem();

        when(githubApiClient.searchRepositories(
                "spring boot", "Java", "stars"))
                .thenReturn(response);

        when(response.getItems()).thenReturn(List.of(item));
        when(repository.findById(123L)).thenReturn(Optional.empty());

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<GithubEntity> result =
                githubService.searchRepositories("spring boot", "Java", "stars");

        assertEquals(1, result.size());
        assertEquals("spring-boot", result.get(0).getRepoName());
        assertEquals("Java", result.get(0).getLanguage());
        assertEquals(1000, result.get(0).getStars());
        assertEquals(500, result.get(0).getForks());

        verify(repository).saveAll(anyList());
    }

    // Test 2: Handle empty GitHub API results
    @Test
    void shouldHandleEmptySearchResults() {

        GitHubApiResponse response = mock(GitHubApiResponse.class);

        when(githubApiClient.searchRepositories(
                "unknown-project", null, "stars"))
                .thenReturn(response);

        when(response.getItems()).thenReturn(List.of());

        List<GithubEntity> result =
                githubService.searchRepositories(
                        "unknown-project", null, "stars");

        assertTrue(result.isEmpty());
    }

    // Test 3: Handle null API response
    @Test
    void shouldThrowExceptionWhenApiResponseIsNull() {

        when(githubApiClient.searchRepositories(
                "spring boot", null, "stars"))
                .thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                githubService.searchRepositories(
                        "spring boot", null, "stars"));
    }

    // Test 4: Handle null items in API response
    @Test
    void shouldThrowExceptionWhenApiItemsAreNull() {

        GitHubApiResponse response = mock(GitHubApiResponse.class);

        when(githubApiClient.searchRepositories(
                "spring boot", null, "stars"))
                .thenReturn(response);

        when(response.getItems()).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                githubService.searchRepositories(
                        "spring boot", null, "stars"));
    }

    // Test 5: Update existing repository instead of creating a duplicate
    @Test
    void shouldUpdateExistingRepository() {

        GitHubApiResponse response = mock(GitHubApiResponse.class);
        GitHubApiResponse.Item item = createItem();

        GithubEntity existingEntity = new GithubEntity();

        when(githubApiClient.searchRepositories(
                "spring boot", "Java", "stars"))
                .thenReturn(response);

        when(response.getItems()).thenReturn(List.of(item));

        when(repository.findById(123L))
                .thenReturn(Optional.of(existingEntity));

        when(repository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<GithubEntity> result =
                githubService.searchRepositories("spring boot", "Java", "stars");

        assertEquals(1, result.size());
        assertEquals(123L, result.get(0).getId());
        assertEquals("spring-boot", result.get(0).getRepoName());

        verify(repository).findById(123L);
        verify(repository).saveAll(anyList());
    }

    // Helper method to create mock GitHub repository data
    private GitHubApiResponse.Item createItem() {

        GitHubApiResponse.Item item =
                mock(GitHubApiResponse.Item.class);

        GitHubApiResponse.Owner owner =
                mock(GitHubApiResponse.Owner.class);

        when(item.getId()).thenReturn(123L);
        when(item.getName()).thenReturn("spring-boot");
        when(item.getDescription())
                .thenReturn("Spring Boot framework");
        when(item.getOwner()).thenReturn(owner);
        when(owner.getLogin()).thenReturn("spring-projects");
        when(item.getLanguage()).thenReturn("Java");
        when(item.getStars()).thenReturn(1000);
        when(item.getForks()).thenReturn(500);
        when(item.getLastUpdated()).thenReturn(null);

        return item;
    }
}
