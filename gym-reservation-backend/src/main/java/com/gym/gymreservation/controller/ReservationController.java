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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        try {
            Reservation newReservation = reservationService.createReservation(
                    user.getId(),
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

    // 管理员或本人取消预约
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("预约不存在");
        }
        // 只允许本人或管理员取消
        if (!reservation.getUser().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限取消该预约");
        }
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("删除成功");
    }

    // 获取当前用户的场地预约
    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.gym.gymreservation.model.User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        return ResponseEntity.ok(reservationService.getReservationsByUser(user));
    }
} 