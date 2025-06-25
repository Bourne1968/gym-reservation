package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.CourseSchedule;
import com.gym.gymreservation.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {
    List<CourseSchedule> findByCourse(Course course);
} 