package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class HomeRestController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAll();
    }

    @PostMapping
    public Post putPost(@RequestBody Post post) {
        return postService.save(post);
    }

    @GetMapping("/posts/{id}")
    public Optional<Post> getPostById(@PathVariable Long id) {
        return postService.getById(id);
    }
}
