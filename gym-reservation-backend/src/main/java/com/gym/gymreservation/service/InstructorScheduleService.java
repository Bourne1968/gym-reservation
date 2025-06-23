package com.gym.gymreservation.service;

import com.gym.gymreservation.model.InstructorSchedule;
import com.gym.gymreservation.model.Instructor;
import com.gym.gymreservation.repository.InstructorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InstructorScheduleService {
    @Autowired
    private InstructorScheduleRepository instructorScheduleRepository;

    public List<InstructorSchedule> getSchedulesByInstructor(Instructor instructor) {
        return instructorScheduleRepository.findByInstructor(instructor);
    }

    public InstructorSchedule saveSchedule(InstructorSchedule schedule) {
        return instructorScheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        instructorScheduleRepository.deleteById(id);
    }
} 