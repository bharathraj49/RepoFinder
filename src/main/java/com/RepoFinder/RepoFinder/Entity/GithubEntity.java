package com.RepoFinder.RepoFinder.Entity;

import jakarta.persistence.*;

import java.time.Instant;

import java.util.List;
@Entity
@Table(name = "github_repositories")
public class GithubEntity {
    @Id
    private Long id;
    private String RepoName;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String owner;
    private String language;
    @ElementCollection
    private List<String>Technologies;
    private Integer stars;
    private Integer forks;
    private Instant lastUpdated;

    public GithubEntity() {
    }

    public GithubEntity(Instant lastUpdated, Integer forks, Integer stars, List<String> technologies, String language, String owner, String description, String repoName, Long id) {
        this.lastUpdated = lastUpdated;
        this.forks = forks;
        this.stars = stars;
        Technologies = technologies;
        this.language = language;
        this.owner = owner;
        this.description = description;
        RepoName = repoName;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepoName() {
        return RepoName;
    }

    public void setRepoName(String repoName) {
        RepoName = repoName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getTechnologies() {
        return Technologies;
    }

    public void setTechnologies(List<String> technologies) {
        Technologies = technologies;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Integer getForks() {
        return forks;
    }

    public void setForks(Integer forks) {
        this.forks = forks;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
