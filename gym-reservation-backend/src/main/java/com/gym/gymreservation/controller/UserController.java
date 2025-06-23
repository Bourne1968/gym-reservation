package com.gym.gymreservation.controller;

import com.gym.gymreservation.dto.JwtAuthenticationResponse;
import com.gym.gymreservation.dto.LoginRequest;
import com.gym.gymreservation.model.User;
import com.gym.gymreservation.repository.UserRepository;
import com.gym.gymreservation.security.JwtTokenProvider;
import com.gym.gymreservation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            com.gym.gymreservation.model.User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            String role = user != null ? user.getRole() : "USER";
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, role));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An internal error occurred");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> payload) {
        try {
            String username = (String) payload.get("username");
            String password = (String) payload.get("password");
            String adminCode = (String) payload.getOrDefault("adminCode", "");
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            if ("ADMIN2024".equals(adminCode)) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 查询所有用户（仅管理员）
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 删除用户（仅管理员）
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("删除成功");
    }

    // 启用/禁用用户（仅管理员）
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        User target = userRepository.findById(id).orElse(null);
        if (target == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }
        target.setEnabled(enabled);
        userRepository.save(target);
        return ResponseEntity.ok("操作成功");
    }
} 