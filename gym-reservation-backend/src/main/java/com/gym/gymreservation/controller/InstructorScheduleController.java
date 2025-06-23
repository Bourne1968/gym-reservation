package com.gym.gymreservation.controller;

import com.gym.gymreservation.model.InstructorSchedule;
import com.gym.gymreservation.model.Instructor;
import com.gym.gymreservation.repository.InstructorRepository;
import com.gym.gymreservation.service.InstructorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/instructor-schedules")
public class InstructorScheduleController {
    @Autowired
    private InstructorScheduleService instructorScheduleService;
    @Autowired
    private InstructorRepository instructorRepository;

    // 获取某教练的排班
    @GetMapping("/by-instructor/{instructorId}")
    public ResponseEntity<List<InstructorSchedule>> getByInstructor(@PathVariable Long instructorId) {
        Optional<Instructor> instructorOpt = instructorRepository.findById(instructorId);
        if (instructorOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(instructorScheduleService.getSchedulesByInstructor(instructorOpt.get()));
    }

    // 新增/编辑排班
    @PostMapping
    public ResponseEntity<InstructorSchedule> save(@RequestBody InstructorSchedule schedule) {
        return ResponseEntity.ok(instructorScheduleService.saveSchedule(schedule));
    }

    // 删除排班
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        instructorScheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }
} 