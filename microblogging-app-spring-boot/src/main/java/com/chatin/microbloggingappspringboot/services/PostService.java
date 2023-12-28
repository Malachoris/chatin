package com.chatin.microbloggingappspringboot.services;

import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Stereotype design pattern
@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

//    Optional because we might get nothing null so to avoid errors
//    could be public Post getById
    public Optional<Post> getById(Long id){
//        getById is deprecated in crudRepository :can be deleted:
        return postRepository.findById(id);
    }

//
    public List<Post> getAll(){
        return postRepository.findAll();
    }

//
    public Post save(Post post){
//        Save time the moment new post was created
        if (post.getId()==null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        return postRepository.save(post);
    }

}
