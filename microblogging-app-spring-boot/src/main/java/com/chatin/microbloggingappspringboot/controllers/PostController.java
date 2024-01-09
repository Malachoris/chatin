package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.services.BloggerDetailsService;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import com.chatin.microbloggingappspringboot.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final BloggerService bloggerService;

    private final BloggerDetailsService bloggerDetailsService;

    @GetMapping
    public List<Post> home() {
        return postService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> optionalPost = this.postService.getById(id);

        return optionalPost.map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/new")
    public ResponseEntity<Post> createNewPost(@RequestBody Post post, Authentication authentication) {
        String authUsername = authentication.getName();

        Blogger blogger = bloggerService.findOneByEmail(authUsername).orElseThrow(() ->
                new IllegalArgumentException("Account not found"));

        post.setBlogger(blogger);
        postService.save(post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Post> writePost(@PathVariable Long id, @RequestBody Post post, Authentication authentication) {

        String authUsername = authentication.getName();

        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            if (!existingPost.getBlogger().getEmail().equals(authUsername)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());

            postService.save(existingPost);
            return new ResponseEntity<>(existingPost, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post, Authentication authentication) {

        String authUsername = authentication.getName();

        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            if (!existingPost.getBlogger().getEmail().equals(authUsername)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());

            postService.save(existingPost);
            return new ResponseEntity<>(existingPost, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        String authUsername = authentication.getName();

        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            if (existingPost.getBlogger().getEmail().equals(authUsername) || bloggerDetailsService.isAdmin(authentication)) {
                postService.delete(existingPost);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

