
package com.RepoFinder.RepoFinder.Controller;

import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import com.RepoFinder.RepoFinder.Service.GitHubService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@Tag(
        name = "GitHub Repository API",
        description = "Search, store, filter and retrieve GitHub repositories"
)
public class GitHubController {

    private final GitHubService githubService;

    public GitHubController(GitHubService githubService) {
        this.githubService = githubService;
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search GitHub repositories",
            description = "Searches GitHub repositories using a query, " +
                    "optional programming language and sorting preference. " +
                    "The results are saved in the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Repositories fetched and saved successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Missing or invalid request parameters"),
            @ApiResponse(responseCode = "500",
                    description = "GitHub API or internal server error")
    })
    public List<GithubEntity> searchRepositories(

            @Parameter(description = "Repository search keyword", required = true,
                    example = "spring boot")
            @RequestParam String query,

            @Parameter(description = "Filter by programming language",
                    example = "Java")
            @RequestParam(required = false) String language,

            @Parameter(description = "Sort results by stars, forks or updated",
                    example = "stars")
            @RequestParam(defaultValue = "stars") String sort) {

        return githubService.searchRepositories(
                query,
                language,
                sort
        );
    }

    @GetMapping("/repositories")
    @Operation(
            summary = "Get stored repositories",
            description = "Retrieves repositories from the database with " +
                    "optional language, minimum stars, minimum forks " +
                    "and sorting filters."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Repositories retrieved successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid filter or sorting parameters"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    public List<GithubEntity> getRepositories(

            @Parameter(description = "Programming language filter",
                    example = "Java")
            @RequestParam(required = false) String language,

            @Parameter(description = "Minimum number of stars",
                    example = "100")
            @RequestParam(required = false) Integer minStars,

            @Parameter(description = "Minimum number of forks",
                    example = "50")
            @RequestParam(required = false) Integer minForks,

            @Parameter(description = "Sort by stars, forks or updated",
                    example = "stars")
            @RequestParam(defaultValue = "stars") String sort) {

        return githubService.getRepositories(
                language,
                minStars,
                minForks,
                sort
        );
    }
}
