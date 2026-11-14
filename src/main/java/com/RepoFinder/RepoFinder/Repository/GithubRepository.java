
package com.RepoFinder.RepoFinder.Repository;

import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GithubRepository extends
        JpaRepository<GithubEntity, Long>,
        JpaSpecificationExecutor<GithubEntity> {
}
