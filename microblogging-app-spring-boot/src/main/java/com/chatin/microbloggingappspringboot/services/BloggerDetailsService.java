package com.chatin.microbloggingappspringboot.services;

import com.chatin.microbloggingappspringboot.models.Blogger;
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
public class BloggerDetailsService implements UserDetailsService {

    private final BloggerService bloggerService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Blogger blogger = bloggerService.findOneByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: "+ email));

        Set<GrantedAuthority> authorities = blogger
                .getAuthorities()
                .stream()
                .map((authority) -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(blogger.getEmail(),
                blogger.getPassword(),
                authorities);
    }
}