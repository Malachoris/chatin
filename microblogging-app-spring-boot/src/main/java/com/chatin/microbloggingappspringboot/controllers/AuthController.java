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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private final BloggerService bloggerService;

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
        bloggerService.save(signUpDto);
        return new ResponseEntity<>("User is registered successfully!", HttpStatus.OK);
    }

}
