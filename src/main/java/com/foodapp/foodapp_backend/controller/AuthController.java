package com.foodapp.foodapp_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.foodapp.foodapp_backend.dto.LoginRequest;
import com.foodapp.foodapp_backend.dto.RegisterRequest;
import com.foodapp.foodapp_backend.entity.Role;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.RoleRepository;
import com.foodapp.foodapp_backend.repository.UserRepository;
import com.foodapp.foodapp_backend.security.JwtService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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
        
        Role userRole = roleRepository.findByRoleName("USER")
        		.orElseGet(() -> roleRepository.save(new Role("USER")));
        
        user.getRoles().add(userRole);
        
        userRepository.save(user);
        
        return ResponseEntity.ok("User Registered Succesfully");
    }

    // ✅ USER LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtService.generateToken(loginRequest.getEmail());

            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
    }
}
