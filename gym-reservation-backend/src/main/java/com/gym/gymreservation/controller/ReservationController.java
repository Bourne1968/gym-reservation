package com.gym.gymreservation.controller;

import com.gym.gymreservation.dto.ReservationRequestDTO;
import com.gym.gymreservation.model.Reservation;
import com.gym.gymreservation.service.ReservationService;
import com.gym.gymreservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDTO request) {
        try {
            Reservation newReservation = reservationService.createReservation(
                    request.getUserId(),
                    request.getGymId(),
                    request.getReserveTime()
            );
            return ResponseEntity.status(201).body(newReservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 管理员查询所有预约
    @GetMapping("/all")
    public ResponseEntity<?> getAllReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    // 管理员强制取消预约
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有管理员可以操作");
        }
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("删除成功");
    }
} 