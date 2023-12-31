package com.degoke.moniwallet.user.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.degoke.moniwallet.core.payload.response.MessageResponse;
import com.degoke.moniwallet.security.jwt.JwtUtils;
import com.degoke.moniwallet.security.service.UserDetailsImpl;
import com.degoke.moniwallet.user.entity.User;
import com.degoke.moniwallet.user.entity.UserRole;
import com.degoke.moniwallet.user.entity.UserRoleEnum;
import com.degoke.moniwallet.user.payload.request.LoginRequest;
import com.degoke.moniwallet.user.payload.request.RegisterRequest;
import com.degoke.moniwallet.user.payload.response.JwtResponse;
import com.degoke.moniwallet.user.repository.UserRepository;
import com.degoke.moniwallet.user.repository.UserRoleRepository;

import jakarta.validation.Valid;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository  userRoleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is already in use"));
        }

        User user = new User(registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getEmail(),
            encoder.encode(registerRequest.getPassword()));

        Set<String> strRoles = registerRequest.getRole();
        Set<UserRole> roles = new HashSet<>();

        if (strRoles == null) {
            UserRole userRole = userRoleRepository.findByName(UserRoleEnum.USER)
                .orElseThrow(() -> new RuntimeException("Error: role not found"));
            
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        UserRole adminRole = userRoleRepository.findByName(UserRoleEnum.ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: role not found"));
                        roles.add(adminRole);
                        break;
                    default:
                        UserRole userRole = userRoleRepository.findByName(UserRoleEnum.USER)
                            .orElseThrow(() -> new RuntimeException("Error: role not found"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
            userDetails.getId(),
            userDetails.getEmail(),
            roles));
    }
    

}
