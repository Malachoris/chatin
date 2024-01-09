package com.chatin.microbloggingappspringboot.services;

import com.chatin.microbloggingappspringboot.dto.SignUpDto;
import com.chatin.microbloggingappspringboot.models.Authority;
import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.repositories.AuthorityRepository;
import com.chatin.microbloggingappspringboot.repositories.BloggerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service
@RequiredArgsConstructor
public class BloggerService {

    private final PasswordEncoder passwordEncoder;
    private final BloggerRepository bloggerRepository;
    private final AuthorityRepository authorityRepository;

    public Blogger saveSeed(Blogger blogger) {

        if (blogger.getId() == null) {
            if (blogger.getAuthorities() == null || blogger.getAuthorities().isEmpty()) {
                Set<Authority> authorities = new HashSet<>();
                authorityRepository.findById("ROLE_USER").ifPresent(authorities::add);
                blogger.setAuthorities(authorities);
            }
            blogger.setCreatedAt(LocalDateTime.now());
        }
        blogger.setUpdatedAt(LocalDateTime.now());
        blogger.setPassword(passwordEncoder.encode(blogger.getPassword()));
        return bloggerRepository.save(blogger);
    }


    public Blogger saveUpdate(Blogger blogger, SignUpDto signUpDto) {
        if (blogger.getId() == null) {
            setDefaultAuthorities(blogger);
            setSignUpDetails(blogger, signUpDto);
            blogger.setCreatedAt(LocalDateTime.now());
        } else {
            blogger = bloggerRepository.findById(blogger.getId()).orElseThrow(EntityNotFoundException::new);
            updateBloggerDetails(blogger, signUpDto);
            blogger.setUpdatedAt(LocalDateTime.now());
        }
        return bloggerRepository.save(blogger);
    }

    private void setDefaultAuthorities(Blogger blogger) {
        if (blogger.getAuthorities() == null || blogger.getAuthorities().isEmpty()) {
            Set<Authority> authorities = new HashSet<>();
            authorityRepository.findById("ROLE_USER").ifPresent(authorities::add);
            blogger.setAuthorities(authorities);
        }
    }

    private void setSignUpDetails(Blogger blogger, SignUpDto signUpDto) {
        blogger.setFirstName(signUpDto.getFirstName());
        blogger.setUsername(signUpDto.getUsername());
        blogger.setEmail(signUpDto.getEmail());
        blogger.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
    }

    private void updateBloggerDetails(Blogger blogger, SignUpDto signUpDto) {
        if (isNotBlank(signUpDto.getFirstName())) {
            blogger.setFirstName(signUpDto.getFirstName());
        }
        if (isNotBlank(signUpDto.getUsername())) {
            blogger.setUsername(signUpDto.getUsername());
        }
        if (isNotBlank(signUpDto.getEmail())) {
            System.out.println("Login again with new email.");
            blogger.setEmail(signUpDto.getEmail());
        }
        if (isNotBlank(signUpDto.getPassword())) {
            blogger.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        }
    }

    public void addRoleToBlogger(Blogger blogger, String roleName) {
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(blogger.getId());

        if (optionalBlogger.isPresent()) {
            Blogger existingBlogger = optionalBlogger.get();

            Optional<Authority> optionalAuthority = authorityRepository.findById(roleName);

            if (optionalAuthority.isPresent()) {
                Authority authority = optionalAuthority.get();

                Set<Authority> authorities = existingBlogger.getAuthorities()
                        .stream()
                        .map(auth -> (Authority) auth)
                        .collect(Collectors.toSet());

                authorities.add(authority);
                existingBlogger.setAuthorities(authorities);

                bloggerRepository.save(existingBlogger);
            } else {
                throw new IllegalArgumentException("Role not found: " + roleName);
            }
        } else {
            throw new IllegalArgumentException("Blogger not found.");
        }
    }

    public void deleteRoleFromBlogger(Blogger blogger, String roleName) {
        Optional<Blogger> optionalBlogger = bloggerRepository.findById(blogger.getId());

        if (optionalBlogger.isPresent()) {
            Blogger existingBlogger = optionalBlogger.get();

            Optional<Authority> optionalAuthority = authorityRepository.findById(roleName);

            if (optionalAuthority.isPresent()) {
                Authority authority = optionalAuthority.get();

                Set<Authority> authorities = existingBlogger.getAuthorities()
                        .stream()
                        .map(auth -> (Authority) auth)
                        .collect(Collectors.toSet());

                authorities.remove(authority);
                existingBlogger.setAuthorities(authorities);

                    bloggerRepository.save(existingBlogger);
                } else {
                    throw new IllegalStateException("Blogger has no authorities.");
                }
            } else {
                throw new IllegalArgumentException("Role not found: " + roleName);
            }
        }

    public Optional<Blogger> findOneByEmail(String email) {
        return bloggerRepository.findOneByEmailIgnoreCase(email);
    }

    public Optional<Blogger> getById(Long id) {
        return bloggerRepository.findById(id);
    }
}
