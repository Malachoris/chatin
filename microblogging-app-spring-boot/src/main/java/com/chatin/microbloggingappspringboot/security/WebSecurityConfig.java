package com.chatin.microbloggingappspringboot.security;

import com.chatin.microbloggingappspringboot.repositories.BloggerRepository;
//import com.chatin.microbloggingappspringboot.security.JwtTokenFilter;
import com.chatin.microbloggingappspringboot.services.BloggerDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

//    private final BloggerDetailsService bloggerDetailsService;
//
//    public WebSecurityConfig(BloggerDetailsService bloggerDetailsService) {
//        this.bloggerDetailsService = bloggerDetailsService;
//    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Note: spring security requestMatchers updated again
        // https://stackoverflow.com/questions/76809698/spring-security-method-cannot-decide-pattern-is-mvc-or-not-spring-boot-applicati
        http.headers().frameOptions().disable();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(antMatcher("/api/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/posts/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/posts/{id}/edit/**")).authenticated();
                    auth.requestMatchers(antMatcher("/api/posts/{id}/**")).authenticated();
                    auth.requestMatchers(antMatcher("/api/admin/**")).authenticated();
                    auth.requestMatchers(PathRequest.toH2Console()).permitAll();
                    auth.anyRequest().authenticated();
                })

                .formLogin(form -> form
                        .loginPage("/api/login")
                        .loginProcessingUrl("/api")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/api", true)
                        .failureUrl("/login?error")
                        .permitAll()
                );

        return http.build();
    }
}
