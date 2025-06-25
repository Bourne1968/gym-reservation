package com.gym.gymreservation.controller;

import com.gym.gymreservation.model.CourseSchedule;
import com.gym.gymreservation.service.CourseScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course-schedules")
public class CourseScheduleController {
    @Autowired
    private CourseScheduleService courseScheduleService;

    @GetMapping
    public ResponseEntity<List<CourseSchedule>> getAllSchedules() {
        return ResponseEntity.ok(courseScheduleService.getAllSchedules());
    }

    @GetMapping(params = "courseId")
    public ResponseEntity<List<CourseSchedule>> getSchedulesByCourseId(@RequestParam Long courseId) {
        return ResponseEntity.ok(courseScheduleService.getSchedulesByCourseId(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseSchedule> getScheduleById(@PathVariable Long id) {
        Optional<CourseSchedule> schedule = courseScheduleService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourseSchedule> addSchedule(@RequestBody CourseSchedule schedule) {
        return ResponseEntity.ok(courseScheduleService.saveSchedule(schedule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseSchedule> updateSchedule(@PathVariable Long id, @RequestBody CourseSchedule schedule) {
        schedule.setId(id);
        return ResponseEntity.ok(courseScheduleService.saveSchedule(schedule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        courseScheduleService.deleteSchedule(id);
        return ResponseEntity.ok("删除成功");
    }
} 