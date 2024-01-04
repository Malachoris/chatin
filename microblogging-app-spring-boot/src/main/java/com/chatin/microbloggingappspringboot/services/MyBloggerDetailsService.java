package com.chatin.microbloggingappspringboot.services;

import com.chatin.microbloggingappspringboot.models.Blogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
public class MyBloggerDetailsService implements UserDetailsService {

    private final BloggerService bloggerService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Blogger> optionalAccount = bloggerService.findOneByEmail(email);
        if (optionalAccount.isEmpty()) {
            throw new UsernameNotFoundException("Account not found");
        }
        Blogger account = optionalAccount.get();
        List<GrantedAuthority> grantedAuthorities = account
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPassword(), grantedAuthorities); // (2)
    }
}