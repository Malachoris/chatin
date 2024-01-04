package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import com.chatin.microbloggingappspringboot.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    @Autowired
    private final PostService postService;
    @Autowired
    private final BloggerService bloggerService;

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> optionalPost = this.postService.getById(id);

        return optionalPost.map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());

            postService.save(existingPost);
            return new ResponseEntity<>(existingPost, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Post> createNewPost(@RequestBody Post post, Principal principal) {
        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }

        Blogger blogger = bloggerService.findOneByEmail(authUsername).orElseThrow(() -> new IllegalArgumentException("Account not found"));

        post.setBlogger(blogger);
        postService.save(post);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Post> getPostForEdit(@PathVariable Long id) {
        Optional<Post> optionalPost = postService.getById(id);

        return optionalPost.map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            postService.delete(post);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
