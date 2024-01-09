package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import com.chatin.microbloggingappspringboot.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final BloggerService bloggerService;

    @GetMapping
    public List<Post> home() {
        return postService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> optionalPost = postService.getById(id);

        return optionalPost.map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/analyzer/{id}")
    public ResponseEntity<Map<String, Integer>> getPostAnalysis(@PathVariable Long id, Authentication authentication) {

        boolean isOwner = postService.checkPostOwner(authentication, id);
        boolean isAdmin = bloggerService.isAdmin(authentication);
        Optional<Post> optionalPost = postService.getById(id);

        if (optionalPost.isPresent() && (isOwner || isAdmin)) {
            String postBody = optionalPost.get().getBody();
            return new ResponseEntity<>((postService.analyzeText(postBody)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

    @RequestMapping(value = "/{id}", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<Post> writeOrUpdatePost(@PathVariable Long id, @RequestBody Post post, Authentication authentication) {

        boolean isOwner = postService.checkPostOwner(authentication, id);
        boolean isAdmin = bloggerService.isAdmin(authentication);
        Optional<Post> optionalPost = postService.getById(id);

        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            if (isOwner || isAdmin) {
                existingPost.setTitle(post.getTitle());
                existingPost.setBody(post.getBody());
                postService.save(existingPost);
                return new ResponseEntity<>(existingPost, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {

        boolean isOwner = postService.checkPostOwner(authentication, id);
        boolean isAdmin = bloggerService.isAdmin(authentication);
        Optional<Post> optionalPost = postService.getById(id);

        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            if (isOwner || isAdmin) {
                postService.delete(existingPost);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

