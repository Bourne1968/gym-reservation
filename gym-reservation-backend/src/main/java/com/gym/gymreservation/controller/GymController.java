package com.gym.gymreservation.controller;

import com.gym.gymreservation.model.Gym;
import com.gym.gymreservation.service.GymService;
import com.gym.gymreservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/gyms")
public class GymController {

    @Autowired
    private GymService gymService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Gym>> getAllGyms() {
        List<Gym> gyms = gymService.getAllGyms();
        return ResponseEntity.ok(gyms);
    }

    @PostMapping
    public ResponseEntity<?> addGym(@RequestBody Gym gym) {
        // 管理员权限校验
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        Gym newGym = gymService.addGym(gym);
        return ResponseEntity.status(201).body(newGym);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGym(@PathVariable Long id, @RequestBody Gym gym) {
        // 管理员权限校验
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        gym.setId(id);
        Gym updatedGym = gymService.addGym(gym); // 这里假设 addGym 方法可做保存和更新
        return ResponseEntity.ok(updatedGym);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGym(@PathVariable Long id) {
        // 管理员权限校验
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        gymService.deleteGym(id);
        return ResponseEntity.ok("删除成功");
    }
} 