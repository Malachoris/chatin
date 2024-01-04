package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.dto.SignUpDto;
import com.chatin.microbloggingappspringboot.models.Authority;
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

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    @Autowired
    private final BloggerService bloggerService;
    @Autowired
    private final BloggerRepository bloggerRepository;
    @Autowired
    private final AuthorityRepository authorityRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/createUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody SignUpDto signUpDto, @RequestParam String role) {
        if (!isValidRole(role)) {
            return new ResponseEntity<>("Invalid role provided", HttpStatus.BAD_REQUEST);
        }

        Optional<Blogger> existingUser =bloggerRepository.findByUsernameOrEmail(signUpDto.getUsername(),
                signUpDto.getEmail());
        if (existingUser.isPresent()) {
            if (existingUser.get().getEmail().equals(signUpDto.getEmail())) {
                return new ResponseEntity<>("Email already exists!", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("Username already exist!", HttpStatus.BAD_REQUEST);
            }
        }

        // Create user object
        Blogger blogger = Blogger
                .builder()
                .firstName(signUpDto.getFirstName())
                .username(signUpDto.getUsername()) // You might need to adjust this based on your requirements
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .build();

        // Assign role
        Authority authority = authorityRepository.findByName(role).orElse(null);
        if (authority == null) {
            return new ResponseEntity<>("Role not found", HttpStatus.BAD_REQUEST);
        }
        blogger.setAuthorities(Collections.singleton(authority));

        bloggerService.save(blogger);

        return new ResponseEntity<>("User created successfully!", HttpStatus.OK);
    }

    private boolean isValidRole(String role) {
        return role.equals("ROLE_USER") || role.equals("ROLE_ADMIN");
    }
}

