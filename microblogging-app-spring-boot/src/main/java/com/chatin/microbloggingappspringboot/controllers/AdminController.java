package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.repositories.AuthorityRepository;
import com.chatin.microbloggingappspringboot.repositories.BloggerRepository;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final BloggerService bloggerService;
    @Autowired
    private final BloggerRepository bloggerRepository;
    @Autowired
    private final AuthorityRepository authorityRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;


    @DeleteMapping("/deleteUser/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable Blogger blogger) {

        Optional<Blogger> userToDelete =bloggerService.findOneByEmail(blogger.getEmail());
        if (userToDelete.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        bloggerRepository.delete(userToDelete.get());

        return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
    }

    private boolean isValidRole(String role) {
        return role.equals("ROLE_USER") || role.equals("ROLE_ADMIN");
    }
}

