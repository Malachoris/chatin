package com.chatin.microbloggingappspringboot.security;

import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import com.chatin.microbloggingappspringboot.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import java.util.Optional;

@Component
public class PostSecurity {

    private final BloggerService bloggerService;
    private final PostService postService;

    @Autowired
    public PostSecurity(BloggerService bloggerService, PostService postService) {
        this.bloggerService = bloggerService;
        this.postService = postService;
    }

    public boolean checkPostOwner(Authentication authentication, Long postId) {
        String authUsername = authentication.getName();

        Optional<Post> optionalPost = postService.getById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Blogger postOwner = post.getBlogger();

            return postOwner.getEmail().equals(authUsername);
        }

        return false;
    }


}
