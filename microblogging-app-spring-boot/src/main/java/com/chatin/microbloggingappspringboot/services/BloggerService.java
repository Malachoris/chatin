package com.chatin.microbloggingappspringboot.services;

import com.chatin.microbloggingappspringboot.dto.SignUpDto;
import com.chatin.microbloggingappspringboot.exceptions.RegistrationException;
import com.chatin.microbloggingappspringboot.models.Authority;
import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.repositories.AuthorityRepository;
import com.chatin.microbloggingappspringboot.repositories.BloggerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BloggerService {

    private final PasswordEncoder passwordEncoder;
    private final BloggerRepository bloggerRepository;
    private final AuthorityRepository authorityRepository;

    public Blogger save(SignUpDto signUpDto) {
        Optional<Blogger> existingUser = bloggerRepository.findByUsernameOrEmail(signUpDto.getUsername(), signUpDto.getEmail());

        if (existingUser.isPresent()) {
            throw new RegistrationException("User with the provided email or username already exists");
        }

        Blogger blogger = existingUser.orElseGet(Blogger::new);

        blogger.setFirstName(signUpDto.getFirstName());
        blogger.setUsername(signUpDto.getUsername());
        blogger.setEmail(signUpDto.getEmail());
        blogger.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        if (blogger.getId() == null) {
            Set<Authority> authorities = new HashSet<>();
            authorityRepository.findById("ROLE_USER").ifPresent(authorities::add);
            blogger.setAuthorities(authorities);
            blogger.setCreatedAt(LocalDateTime.now());
        }

        blogger.setUpdatedAt(LocalDateTime.now());

        return bloggerRepository.save(blogger);
    }

//    saveBlogger
//    updateBloggerInfo make email and/or username if updating an empty type (String username = "") check if other users don't have same username or email.
//    deleteBlogger

    public Optional<Blogger> findOneByEmail(String email) {
        return bloggerRepository.findOneByEmailIgnoreCase(email);
    }
}
