package com.chatin.microbloggingappspringboot.services;

import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public Post save(Post post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Map<String, Integer> analyzeText(String postBody) {
        // Remove punctuation and convert to lowercase
        String cleanText = postBody.replaceAll("[^\\s\\p{L}0-9]", "").toLowerCase();

        // Split string to substrings
        String[] words = cleanText.split("\\s+");

        // Count word occurrences
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String word : words) {
            wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
        }

        // Display top 5 words
        return wordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public boolean checkPostOwner(Authentication authentication, Long postId) {
        String authUsername = authentication.getName();

        Optional<Post> optionalPost = this.getById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Blogger postOwner = post.getBlogger();

            return postOwner.getEmail().equals(authUsername);
        }

        return false;
    }

}
