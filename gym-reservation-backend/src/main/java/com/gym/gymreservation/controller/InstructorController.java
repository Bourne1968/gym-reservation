package com.gym.gymreservation.controller;

import com.gym.gymreservation.model.Instructor;
import com.gym.gymreservation.service.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {
    @Autowired
    private InstructorService instructorService;

    @GetMapping
    public ResponseEntity<List<Instructor>> getAll() {
        return ResponseEntity.ok(instructorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instructor> getById(@PathVariable Long id) {
        Optional<Instructor> instructor = instructorService.getById(id);
        return instructor.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Instructor> create(@RequestBody Instructor instructor) {
        return ResponseEntity.ok(instructorService.save(instructor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Instructor> update(@PathVariable Long id, @RequestBody Instructor instructor) {
        instructor.setId(id);
        return ResponseEntity.ok(instructorService.save(instructor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        instructorService.delete(id);
        return ResponseEntity.ok().build();
    }
} 