package com.example.AgriProject.service;
import com.example.AgriProject.config.AuthUtil;
import com.example.AgriProject.dto.LoginRequestDto;
import com.example.AgriProject.dto.LoginResponseDto;
import com.example.AgriProject.dto.SignupRequestDto;
import com.example.AgriProject.dto.SignupResponseDto;
import com.example.AgriProject.entity.User;
import com.example.AgriProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUtil authUtil;
    private final AuthenticationManager authenticationManager;
    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(SignupRequestDto signupRequestDto){
        userRepository.findByUsername(signupRequestDto.getUsername())
                .ifPresent(u->{throw new IllegalArgumentException("User already exists!");});


        userRepository.findByEmail(signupRequestDto.getEmail())
                .ifPresent(u->{throw new IllegalArgumentException("Email already exists!!");});

        User user=User.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .role(signupRequestDto.getRole())
                .build();

        user =userRepository.save(user);

        return new SignupResponseDto(user.getId(), user.getUsername());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        Optional<User> userOptional=userRepository.findByUsername(loginRequestDto.getUsername());

        if(userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }

        User user=userOptional.get();

        if(!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("Invalid password");
        }

        String token=authUtil.accessToken(user);

        return new LoginResponseDto(user.getId(),user.getUsername(),token,user.getRole());
    }

    public boolean isFirstUser() {
        return userRepository.count()==0;
    }
}
