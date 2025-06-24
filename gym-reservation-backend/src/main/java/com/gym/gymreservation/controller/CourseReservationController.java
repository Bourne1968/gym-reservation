package com.gym.gymreservation.controller;

import com.gym.gymreservation.model.*;
import com.gym.gymreservation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/course-reservations")
public class CourseReservationController {
    @Autowired
    private CourseReservationRepository courseReservationRepository;
    @Autowired
    private CourseScheduleRepository courseScheduleRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createCourseReservation(@RequestBody Map<String, Object> payload) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        Long courseScheduleId = Long.valueOf(payload.get("courseScheduleId").toString());
        CourseSchedule schedule = courseScheduleRepository.findById(courseScheduleId).orElse(null);
        if (schedule == null) {
            return ResponseEntity.badRequest().body("课程安排不存在");
        }
        if (courseReservationRepository.existsByUserAndCourseSchedule(user, schedule)) {
            return ResponseEntity.badRequest().body("你已预约该课程");
        }
        Integer current = schedule.getCurrentParticipants() == null ? 0 : schedule.getCurrentParticipants();
        if (current >= schedule.getMaxParticipants()) {
            return ResponseEntity.badRequest().body("课程已满员");
        }
        CourseReservation reservation = new CourseReservation();
        reservation.setUser(user);
        reservation.setCourseSchedule(schedule);
        reservation.setStatus("RESERVED");
        courseReservationRepository.save(reservation);
        schedule.setCurrentParticipants(current + 1);
        courseScheduleRepository.save(schedule);
        return ResponseEntity.status(201).body(reservation);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
        }
        return ResponseEntity.ok(courseReservationRepository.findByUser(user));
    }
} 