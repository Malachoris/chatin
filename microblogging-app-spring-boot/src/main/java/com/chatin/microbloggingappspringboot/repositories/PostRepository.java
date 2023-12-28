package com.chatin.microbloggingappspringboot.repositories;

import com.chatin.microbloggingappspringboot.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
