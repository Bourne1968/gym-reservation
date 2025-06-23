package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.InstructorSchedule;
import com.gym.gymreservation.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstructorScheduleRepository extends JpaRepository<InstructorSchedule, Long> {
    List<InstructorSchedule> findByInstructor(Instructor instructor);
} 