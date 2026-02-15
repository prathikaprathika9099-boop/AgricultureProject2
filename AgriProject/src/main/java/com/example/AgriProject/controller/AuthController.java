package com.example.AgriProject.controller;

import com.example.AgriProject.dto.LoginRequestDto;
import com.example.AgriProject.dto.LoginResponseDto;
import com.example.AgriProject.dto.SignupRequestDto;
import com.example.AgriProject.dto.SignupResponseDto;
import com.example.AgriProject.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        try{
            LoginResponseDto responseDto=authService.login(loginRequestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch (IllegalArgumentException exception){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/api/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignupRequestDto signupRequestDto){
        try{
            SignupResponseDto responseDto = authService.signup(signupRequestDto);
            return new ResponseEntity<>(Map.of(
                    "id", String.valueOf(responseDto.getUserId()),
                    "username", responseDto.getUsername()
            ), HttpStatus.CREATED);
        } catch (IllegalArgumentException exception){
            // Return the exception message as JSON
            return new ResponseEntity<>(Map.of("message", exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
