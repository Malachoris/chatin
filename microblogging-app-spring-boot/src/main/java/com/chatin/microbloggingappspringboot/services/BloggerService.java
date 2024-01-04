package com.chatin.microbloggingappspringboot.services;

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

    public Blogger save(Blogger blogger) {

        if (blogger.getId() == null) {
            if (blogger.getAuthorities().isEmpty()) {
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

    public Optional<Blogger> findOneByEmail(String email) {
        return bloggerRepository.findOneByEmailIgnoreCase(email);
    }
}
