package com.chatin.microbloggingappspringboot.repositories;

import com.chatin.microbloggingappspringboot.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
    Optional<Authority> findByName(String name);
    Optional<Authority> findByUsernameOrEmail(String username, String email);
}
