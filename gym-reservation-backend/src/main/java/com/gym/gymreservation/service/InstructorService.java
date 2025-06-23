package com.gym.gymreservation.service;

import com.gym.gymreservation.model.Instructor;
import com.gym.gymreservation.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {
    @Autowired
    private InstructorRepository instructorRepository;

    public List<Instructor> getAll() {
        return instructorRepository.findAll();
    }

    public Optional<Instructor> getById(Long id) {
        return instructorRepository.findById(id);
    }

    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public void delete(Long id) {
        instructorRepository.deleteById(id);
    }
} 