package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.CourseReservation;
import com.gym.gymreservation.model.User;
import com.gym.gymreservation.model.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseReservationRepository extends JpaRepository<CourseReservation, Long> {
    List<CourseReservation> findByUser(User user);
    List<CourseReservation> findByCourseSchedule(CourseSchedule courseSchedule);
    boolean existsByUserAndCourseSchedule(User user, CourseSchedule courseSchedule);
} 