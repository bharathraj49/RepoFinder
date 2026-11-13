package com.RepoFinder.RepoFinder.Repository;

import com.RepoFinder.RepoFinder.Entity.GithubEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GithubRepository extends JpaRepository<GithubEntity,Long>{
    @Query("""
        SELECT r FROM GithubEntity as r
        WHERE (:language IS NULL OR
               LOWER(r.language) = LOWER(:language))
        AND (:minStars IS NULL OR r.stars >= :minStars)
    """)
    List<GithubEntity> findRepositories(
            @Param("language") String language,
            @Param("minStars") Integer minStars,
            Sort sort
    );
}
