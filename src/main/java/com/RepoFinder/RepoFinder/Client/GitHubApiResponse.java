package com.RepoFinder.RepoFinder.Client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public class GitHubApiResponse {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {

        private Long id;
        private String name;
        private String description;
        private Owner owner;
        private String language;

        @JsonProperty("stargazers_count")
        private Integer stars;

        @JsonProperty("forks_count")
        private Integer forks;

        @JsonProperty("updated_at")
        private Instant lastUpdated;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Owner getOwner() {
            return owner;
        }

        public String getLanguage() {
            return language;
        }

        public Integer getStars() {
            return stars;
        }

        public Integer getForks() {
            return forks;
        }

        public Instant getLastUpdated() {
            return lastUpdated;
        }
    }

    public static class Owner {

        private String login;

        public String getLogin() {
            return login;
        }
    }
}
