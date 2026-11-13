
package com.RepoFinder.RepoFinder.Service;

import com.RepoFinder.RepoFinder.Client.GitHubApiClient;
import com.RepoFinder.RepoFinder.Client.GitHubApiResponse;
import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Repository.GithubRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GitHubServiceImpl implements GitHubService {

    private final GitHubApiClient githubApiClient;
    private final GithubRepository repository;

    public GitHubServiceImpl(
            GitHubApiClient githubApiClient,
            GithubRepository repository) {

        this.githubApiClient = githubApiClient;
        this.repository = repository;
    }


    @Override
    @Transactional
    public List<GithubEntity> searchRepositories(
            String query,
            String language,
            String sort) {

        // Fetch data from GitHub API
        GitHubApiResponse response =
                githubApiClient.searchRepositories(
                        query,
                        language,
                        sort
                );


        if (response == null || response.getItems() == null) {
            throw new RuntimeException(
                    "Invalid response from GitHub API"
            );
        }

        List<GithubEntity> entities =
                response.getItems().stream()
                        .map(this::convertToEntity)
                        .toList();

        return repository.saveAll(entities);
    }


    @Override
    @Transactional(readOnly = true)
    public List<GithubEntity> getRepositories(
            String language,
            Integer minStars,
            String sort) {

        return repository.findAll().stream()

                .filter(repo ->
                        language == null ||
                                language.isBlank() ||
                                (repo.getLanguage() != null &&
                                        repo.getLanguage().equalsIgnoreCase(language))
                )

                .filter(repo ->
                        minStars == null ||
                                repo.getStars() >= minStars
                )


                .sorted((a, b) -> {

                    if ("stars".equalsIgnoreCase(sort)) {
                        return Integer.compare(
                                b.getStars(),
                                a.getStars()
                        );
                    }

                    return 0;
                })

                .toList();
    }

    private GithubEntity convertToEntity(
            GitHubApiResponse.Item item) {

        GithubEntity entity = new GithubEntity();

        entity.setId(item.getId());
        entity.setRepoName(item.getName());
        entity.setDescription(item.getDescription());

        entity.setOwner(
                item.getOwner() != null
                        ? item.getOwner().getLogin()
                        : null
        );

        entity.setLanguage(item.getLanguage());
        entity.setStars(item.getStars());
        entity.setForks(item.getForks());
        entity.setLastUpdated(item.getLastUpdated());

        return entity;
    }
}
