package com.gym.gymreservation.controller;

import com.gym.gymreservation.model.InstructorLeave;
import com.gym.gymreservation.model.User;
import com.gym.gymreservation.repository.UserRepository;
import com.gym.gymreservation.service.InstructorLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/instructor-leaves")
public class InstructorLeaveController {
    @Autowired
    private InstructorLeaveService instructorLeaveService;
    @Autowired
    private UserRepository userRepository;

    // 获取某教练的请假记录
    @GetMapping("/by-instructor/{instructorId}")
    public ResponseEntity<List<InstructorLeave>> getByInstructor(@PathVariable Long instructorId) {
        Optional<User> instructorOpt = userRepository.findById(instructorId);
        if (instructorOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(instructorLeaveService.getLeavesByInstructor(instructorOpt.get()));
    }

    // 新增/编辑请假
    @PostMapping
    public ResponseEntity<InstructorLeave> save(@RequestBody InstructorLeave leave) {
        // 新增时默认状态为PENDING
        if (leave.getId() == null) leave.setStatus("PENDING");
        return ResponseEntity.ok(instructorLeaveService.saveLeave(leave));
    }

    // 删除请假
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        instructorLeaveService.deleteLeave(id);
        return ResponseEntity.ok().build();
    }

    // 审核请假（通过/拒绝）
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        InstructorLeave leave = instructorLeaveService.findById(id);
        if (leave == null) return ResponseEntity.notFound().build();
        leave.setStatus(status);
        instructorLeaveService.saveLeave(leave);
        return ResponseEntity.ok().build();
    }
} 