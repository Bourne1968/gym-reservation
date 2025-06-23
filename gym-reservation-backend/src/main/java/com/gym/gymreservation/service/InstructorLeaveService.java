package com.gym.gymreservation.service;

import com.gym.gymreservation.model.InstructorLeave;
import com.gym.gymreservation.model.User;
import com.gym.gymreservation.repository.InstructorLeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InstructorLeaveService {
    @Autowired
    private InstructorLeaveRepository instructorLeaveRepository;

    public List<InstructorLeave> getLeavesByInstructor(User instructor) {
        return instructorLeaveRepository.findByInstructor(instructor);
    }

    public InstructorLeave saveLeave(InstructorLeave leave) {
        return instructorLeaveRepository.save(leave);
    }

    public void deleteLeave(Long id) {
        instructorLeaveRepository.deleteById(id);
    }

    public InstructorLeave findById(Long id) {
        return instructorLeaveRepository.findById(id).orElse(null);
    }
} 