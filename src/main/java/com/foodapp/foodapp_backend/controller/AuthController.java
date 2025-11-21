package com.foodapp.foodapp_backend.controller;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.dto.LoginRequest;
import com.foodapp.foodapp_backend.dto.RegisterRequest;
import com.foodapp.foodapp_backend.entity.Role;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.UserRepository;
import com.foodapp.foodapp_backend.security.JwtService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;
    

    //registration 
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        
        user.setRoles(Set.of(Role.ROLE_USER));
        
        userRepository.save(user);
        log.info("New user Registered Succesfully {}:",registerRequest.getEmail());
        return ResponseEntity.ok("User Registered Succesfully");
    }

    // ✅ USER LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authManager.authenticate(    //To find the user info authMangaer will search for a Bean which loads the user info.
                    new UsernamePasswordAuthenticationToken(				//here the bean is CustomUserDetailsService @service.
                            loginRequest.getEmail(),	
                            loginRequest.getPassword()	//after having info it has to compare the password with plain and hash password which from db by above service
                    )										//to do that it need PasswordEncoder and BCryptPasswordEncoder to encode password and compare both.
            );

            var authorities = authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList());

            // 2. Create a claims map to hold the roles
            Map<String, Object> extraClaims = Map.of("roles", authorities);

            // 3. Call createToken (instead of generateToken) to include the claims
            String token = jwtService.createToken(extraClaims, loginRequest.getEmail());  
            
            log.info("User logged in Succesfully: {}",loginRequest.getEmail());
            log.info("Sending JWT token to app {}:",token);
            
            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }
}
