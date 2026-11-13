package com.RepoFinder.RepoFinder.Controller;

import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Service.GitHubService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService githubService;

    public GitHubController(GitHubService githubService) {
        this.githubService = githubService;
    }

    @PostMapping("/search")
    public List<GithubEntity> searchRepositories(
            @RequestParam String query,
            @RequestParam(required = false) String language,
            @RequestParam(defaultValue = "stars") String sort) {

        return githubService.searchRepositories(
                query,
                language,
                sort
        );
    }
    @GetMapping("/repositories")
    public List<GithubEntity> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(defaultValue = "stars") String sort) {

        return githubService.getRepositories(
                language,
                minStars,
                sort
        );
    }
}