package com.gym.gymreservation.service;

import com.gym.gymreservation.model.CourseSchedule;
import com.gym.gymreservation.model.Course;
import com.gym.gymreservation.repository.CourseScheduleRepository;
import com.gym.gymreservation.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseScheduleService {
    @Autowired
    private CourseScheduleRepository courseScheduleRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    public List<CourseSchedule> getAllSchedules() {
        return courseScheduleRepository.findAll();
    }

    public Optional<CourseSchedule> getScheduleById(Long id) {
        return courseScheduleRepository.findById(id);
    }

    public List<CourseSchedule> getSchedulesByCourseId(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            return courseScheduleRepository.findByCourse(course.get());
        }
        return List.of();
    }

    public CourseSchedule saveSchedule(CourseSchedule schedule) {
        return courseScheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        courseScheduleRepository.deleteById(id);
    }
} 