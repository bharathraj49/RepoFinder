
package com.RepoFinder.RepoFinder.Service;

import com.RepoFinder.RepoFinder.Client.GitHubApiClient;
import com.RepoFinder.RepoFinder.Client.GitHubApiResponse;
import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Repository.GithubRepository;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        List<GithubEntity> entities = response.getItems()
                .stream()
                .map(item -> {

                    GithubEntity entity = repository
                            .findById(item.getId())
                            .orElse(new GithubEntity());

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
                })
                .toList();

        return repository.saveAll(entities);
    }


    @Override
    @Transactional(readOnly = true)
    public List<GithubEntity> getRepositories(
            String language,
            Integer minStars,
            Integer minForks,
            String sort) {

        Sort sorting = getSort(sort);

        Specification<GithubEntity> specification =
                (root, query, cb) -> {

                    List<Predicate> predicates = new ArrayList<>();


                    if (language != null && !language.isBlank()) {
                        predicates.add(
                                cb.equal(
                                        cb.lower(root.get("language")),
                                        language.trim().toLowerCase(Locale.ROOT)
                                )
                        );
                    }


                    if (minStars != null) {
                        predicates.add(
                                cb.greaterThanOrEqualTo(
                                        root.<Integer>get("stars"),
                                        minStars
                                )
                        );
                    }


                    if (minForks != null) {
                        predicates.add(
                                cb.greaterThanOrEqualTo(
                                        root.<Integer>get("forks"),
                                        minForks
                                )
                        );
                    }

                    return cb.and(
                            predicates.toArray(new Predicate[0])
                    );
                };

        return repository.findAll(specification, sorting);
    }


    private Sort getSort(String sort) {

        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "stars");
        }

        String[] parts = sort.split(",");

        if (parts.length > 2) {
            throw new IllegalArgumentException(
                    "Invalid sort format. Use field,direction"
            );
        }

        String field = parts[0].trim().toLowerCase(Locale.ROOT);

        Sort.Direction direction = Sort.Direction.DESC;

        if (parts.length == 2) {
            try {
                direction = Sort.Direction.fromString(
                        parts[1].trim()
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid sort direction. Use asc or desc"
                );
            }
        }

        String property = switch (field) {
            case "stars" -> "stars";
            case "forks" -> "forks";
            case "updated", "lastupdated" -> "lastUpdated";

            default -> throw new IllegalArgumentException(
                    "Invalid sort field. Use stars, forks, or updated"
            );
        };

        return Sort.by(direction, property);
    }
}
