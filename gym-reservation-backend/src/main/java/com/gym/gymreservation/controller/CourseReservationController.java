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

    @PostMapping("/waitlist")
    public ResponseEntity<?> addToWaitlist(@RequestBody Map<String, Object> payload) {
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
        
        // 检查是否已经在候补名单中
        if (courseReservationRepository.existsByUserAndCourseSchedule(user, schedule)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("您已在候补名单中");
        }
        
        CourseReservation reservation = new CourseReservation();
        reservation.setUser(user);
        reservation.setCourseSchedule(schedule);
        reservation.setStatus("WAITLIST");
        courseReservationRepository.save(reservation);
        
        return ResponseEntity.status(201).body(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录");
        }
        CourseReservation reservation = courseReservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("预约不存在");
        }
        // 只允许本人或管理员取消
        if (!reservation.getUser().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权限取消该预约");
        }
        // 业务逻辑：删除预约并回退名额
        CourseSchedule schedule = reservation.getCourseSchedule();
        if (reservation.getStatus() != null && reservation.getStatus().equals("RESERVED") && schedule.getCurrentParticipants() != null && schedule.getCurrentParticipants() > 0) {
            schedule.setCurrentParticipants(schedule.getCurrentParticipants() - 1);
            courseScheduleRepository.save(schedule);
        }
        courseReservationRepository.deleteById(id);
        return ResponseEntity.ok("取消成功");
    }
} 