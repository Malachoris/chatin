package com.chatin.microbloggingappspringboot.controllers;

import com.chatin.microbloggingappspringboot.dto.LoginReqDto;
import com.chatin.microbloggingappspringboot.dto.LoginRespDto;
import com.chatin.microbloggingappspringboot.dto.SignUpDto;
import com.chatin.microbloggingappspringboot.models.Blogger;
import com.chatin.microbloggingappspringboot.security.JwtUtil;
import com.chatin.microbloggingappspringboot.services.BloggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final BloggerService bloggerService;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginReqDto loginReqDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginReqDto.getEmail(), loginReqDto.getPassword()));

            String email = authentication.getName();
            Blogger optionalBlogger = bloggerService.findOneByEmail(email).get();
            String token = JwtUtil.createToken(optionalBlogger);
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
        Optional <Blogger> bloggerExists = bloggerService.findOneByEmail(signUpDto.getEmail());
        if (bloggerExists.isPresent()) {
            return new ResponseEntity<>("User with this email already exists", HttpStatus.BAD_REQUEST);
        }
        Blogger blogger = new Blogger();
        bloggerService.saveUpdate(blogger, signUpDto);
        return new ResponseEntity<>("User is registered successfully!", HttpStatus.OK);
    }

    @PostMapping("/updateBlogger")
    public ResponseEntity<?> updateBlogger(@RequestBody SignUpDto signUpDto, Authentication authentication) {

        String authUsername = authentication.getName();

        Optional<Blogger> optionalBlogger = bloggerService.findOneByEmail(authUsername);
        if (optionalBlogger.isPresent()) {
            Blogger existingBlogger = optionalBlogger.get();

            if (!existingBlogger.getEmail().equals(authUsername)) {
                return new ResponseEntity<>("Email is taken!", HttpStatus.FORBIDDEN);
            }

            bloggerService.saveUpdate(existingBlogger, signUpDto);

            return new ResponseEntity<>(existingBlogger, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
