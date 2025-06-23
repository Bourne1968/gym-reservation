package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
} 