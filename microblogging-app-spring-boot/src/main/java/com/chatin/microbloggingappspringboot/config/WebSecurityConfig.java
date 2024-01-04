package com.chatin.microbloggingappspringboot.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Note: spring security requestMatchers updated again
        // https://stackoverflow.com/questions/76809698/spring-security-method-cannot-decide-pattern-is-mvc-or-not-spring-boot-applicati
        http.headers().frameOptions().disable();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(antMatcher("/api/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/posts/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/posts/new/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/login/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/signup/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/register/**")).permitAll();
                    auth.requestMatchers(antMatcher("/api/posts/{id}/edit/**")).authenticated();
                    auth.requestMatchers(antMatcher("/api/posts/{id}/**")).authenticated();
                    auth.requestMatchers("/images/**", "/css/**", "/js/**", "/WEB-INF/views/**").permitAll();
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
