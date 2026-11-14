
package com.RepoFinder.RepoFinder.Service;

import com.RepoFinder.RepoFinder.Client.GitHubApiClient;
import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Repository.GithubRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitHubServiceGetRepositoriesTest {

    @Mock
    private GitHubApiClient githubApiClient;

    @Mock
    private GithubRepository repository;

    @InjectMocks
    private GitHubServiceImpl githubService;

    // Test 1: Filter by language and minimum stars
    @Test
    void shouldApplyLanguageAndMinStarsFilters() {

        when(repository.findAll(
                any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        List<GithubEntity> result =
                githubService.getRepositories(
                        "Java", 100, null, "stars,desc");

        assertNotNull(result);

        verify(repository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "stars"))
        );
    }

    // Test 2: Sort by stars descending
    @Test
    void shouldSortByStarsDescending() {

        when(repository.findAll(
                any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        githubService.getRepositories(
                null, null, null, "stars,desc");

        verify(repository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "stars"))
        );
    }

    // Test 3: Sort by forks ascending
    @Test
    void shouldSortByForksAscending() {

        when(repository.findAll(
                any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        githubService.getRepositories(
                null, null, null, "forks,asc");

        verify(repository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.ASC, "forks"))
        );
    }

    // Test 4: Sort by last updated
    @Test
    void shouldSortByLastUpdated() {

        when(repository.findAll(
                any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        githubService.getRepositories(
                null, null, null, "updated,desc");

        verify(repository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "lastUpdated"))
        );
    }

    // Test 5: Use default sorting when sort is null
    @Test
    void shouldUseDefaultSortingWhenSortIsNull() {

        when(repository.findAll(
                any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        githubService.getRepositories(
                null, null, null, null);

        verify(repository).findAll(
                any(Specification.class),
                eq(Sort.by(Sort.Direction.DESC, "stars"))
        );
    }

    // Test 6: Reject invalid sort field
    @Test
    void shouldThrowExceptionForInvalidSortField() {

        assertThrows(IllegalArgumentException.class, () ->
                githubService.getRepositories(
                        null, null, null, "name,desc"));

        verifyNoInteractions(repository);
    }

    // Test 7: Reject invalid sort direction
    @Test
    void shouldThrowExceptionForInvalidSortDirection() {

        assertThrows(IllegalArgumentException.class, () ->
                githubService.getRepositories(
                        null, null, null, "stars,wrong"));

        verifyNoInteractions(repository);
    }

    // Test 8: Reject invalid sort format
    @Test
    void shouldThrowExceptionForInvalidSortFormat() {

        assertThrows(IllegalArgumentException.class, () ->
                githubService.getRepositories(
                        null, null, null, "stars,desc,asc"));

        verifyNoInteractions(repository);
    }
}
