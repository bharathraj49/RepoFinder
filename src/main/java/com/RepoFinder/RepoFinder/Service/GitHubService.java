package com.RepoFinder.RepoFinder.Service;


import com.RepoFinder.RepoFinder.Entity.GithubEntity;

import java.util.List;

public interface GitHubService {

    List<GithubEntity> searchRepositories(
            String query,
            String language,
            String sort
    );

    List<GithubEntity> getRepositories(
            String language,
            Integer minStars,
            String sort
    );
}