package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.dto.SignUpDto;
import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.repositories.BloggerRepository;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final BloggerService bloggerService;
    private final BloggerRepository bloggerRepository;

    @PostMapping("/addUpdateBlogger")
    public ResponseEntity<String> createUpdateBlogger(@RequestBody SignUpDto signUpDto) {
        try {
            Optional<Blogger> existingBlogger = bloggerService.findOneByEmail(signUpDto.getEmail());
            if (existingBlogger.isPresent()) {
                bloggerService.saveUpdate(existingBlogger.get(), signUpDto);
                return new ResponseEntity<>("Blogger updated successfully!", HttpStatus.OK);
            }

            Blogger blogger = new Blogger();
            bloggerService.saveUpdate(blogger, signUpDto);
            return new ResponseEntity<>("Blogger registered successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred during Blogger creation/update.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteBlogger/{id}")
    public ResponseEntity<?> deleteBlogger(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = bloggerService.isAdmin(authentication);
        if (isAdmin){
            Optional<Blogger> BloggerToDelete = bloggerService.getById(id);
            if (BloggerToDelete.isEmpty()) {
                return new ResponseEntity<>("Blogger not found", HttpStatus.NOT_FOUND);
            }

            bloggerRepository.delete(BloggerToDelete.get());
        }

        return new ResponseEntity<>("Blogger deleted successfully!", HttpStatus.OK);
    }

}

