package com.chatin.microbloggingappspringboot.repositories;

import com.chatin.microbloggingappspringboot.models.Blogger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BloggerRepository extends JpaRepository<Blogger, Long> {
    Optional<Blogger> findOneByEmailIgnoreCase(String email);

}
