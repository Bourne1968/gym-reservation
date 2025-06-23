package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.InstructorLeave;
import com.gym.gymreservation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstructorLeaveRepository extends JpaRepository<InstructorLeave, Long> {
    List<InstructorLeave> findByInstructor(User instructor);
} 