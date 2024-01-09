package com.chatin.microbloggingappspringboot.security;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true,
        jsr250Enabled = true)
public class WebSecurityConfig {

    private final BloggerDetailsService bloggerDetailsService;

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public WebSecurityConfig(BloggerDetailsService bloggerDetailsService, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.bloggerDetailsService = bloggerDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;

    }

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
        //  H2 somehow is visible only with this.
        http.headers().frameOptions().disable();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(antMatcher("/api/**")).permitAll();
                    auth.requestMatchers(PathRequest.toH2Console()).permitAll();
                    auth.anyRequest().authenticated();
                    try {
                        auth.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .and().addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
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
