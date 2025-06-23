package com.gym.gymreservation.service;

import com.gym.gymreservation.model.Gym;
import com.gym.gymreservation.repository.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GymService {

    @Autowired
    private GymRepository gymRepository;

    public List<Gym> getAllGyms() {
        return gymRepository.findAll();
    }

    public Gym addGym(Gym gym) {
        return gymRepository.save(gym);
    }

    public void deleteGym(Long id) {
        gymRepository.deleteById(id);
    }

    // 未来可以添加创建、更新、删除场地等方法
} 