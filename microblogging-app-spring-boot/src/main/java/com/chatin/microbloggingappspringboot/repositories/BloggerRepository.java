package com.chatin.microbloggingappspringboot.repositories;

import com.chatin.microbloggingappspringboot.models.Blogger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BloggerRepository extends JpaRepository<Blogger, Long> {
    Optional<Blogger> findOneByEmailIgnoreCase(String email);
    Optional<Blogger> findByUsernameOrEmail(String username, String email);
    Optional<Blogger> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

}
