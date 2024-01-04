package com.chatin.microbloggingappspringboot.services;

import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.repositories.BloggerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
@Service
public class MyBloggerDetailsService implements UserDetailsService {

    private final BloggerRepository bloggerRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Blogger blogger = bloggerRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: "+ usernameOrEmail));

        Set<GrantedAuthority> authorities = blogger
                .getAuthorities()
                .stream()
                .map((authority) -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(blogger.getEmail(),
                blogger.getPassword(),
                authorities);
    }
}