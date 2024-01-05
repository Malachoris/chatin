package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.dto.LoginReqDto;
import com.chatin.microbloggingappspringboot.dto.LoginRespDto;
import com.chatin.microbloggingappspringboot.dto.SignUpDto;
import com.chatin.microbloggingappspringboot.models.Authority;
import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.models.Post;
import com.chatin.microbloggingappspringboot.repositories.AuthorityRepository;
import com.chatin.microbloggingappspringboot.repositories.BloggerRepository;
import com.chatin.microbloggingappspringboot.security.JwtUtil;
import com.chatin.microbloggingappspringboot.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HomeController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BloggerRepository bloggerRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PostService postService;

    private JwtUtil jwtUtil;
    public HomeController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginReqDto loginReqDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsernameOrEmail(), loginReqDto.getPassword()));

            String email = authentication.getName();
            Blogger blogger = new Blogger(email, "");
            String token = JwtUtil.createToken(blogger);
            LoginRespDto loginRes = new LoginRespDto(email, token);

            return new ResponseEntity<>(("User logged in successfully!." + loginRes), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){

        Optional<Blogger> existingUser =bloggerRepository.findByUsernameOrEmail(signUpDto.getUsername(),signUpDto.getEmail());
        if (existingUser.isPresent()) {
            if (existingUser.get().getEmail().equals(signUpDto.getEmail())) {
                return new ResponseEntity<>("Email already exists!", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("Username already exist!", HttpStatus.BAD_REQUEST);
            }
        }
        // creating user object
        Blogger blogger = Blogger
                .builder()
                .firstName(signUpDto.getFirstName())
                .username(signUpDto.getUsername())
                .email(signUpDto.getEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .build();
        Authority authority = authorityRepository.findByName("ROLE_USER").get();
        blogger.setAuthorities(Collections.singleton(authority));
        bloggerRepository.save(blogger);
        return new ResponseEntity<>("User is registered successfully!", HttpStatus.OK);
    }

    @GetMapping
    public List<Post> home() {
        return postService.getAll();
    }

}
