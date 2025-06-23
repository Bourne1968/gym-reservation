package com.gym.gymreservation.service;

import com.gym.gymreservation.model.Gym;
import com.gym.gymreservation.model.Reservation;
import com.gym.gymreservation.model.User;
import com.gym.gymreservation.repository.GymRepository;
import com.gym.gymreservation.repository.ReservationRepository;
import com.gym.gymreservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GymRepository gymRepository;

    public Reservation createReservation(Long userId, Long gymId, LocalDateTime reserveTime) {
        // 1. 查找用户和场地
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new RuntimeException("Gym not found with id: " + gymId));

        // 冲突校验：同一场地同一时间已被预约
        reservationRepository.findByGymAndReserveTimeAndStatus(gym, reserveTime, "RESERVED")
                .ifPresent(r -> { throw new RuntimeException("该场地该时间已被预约，请选择其他时间"); });

        // 2. 创建新的预约对象
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setGym(gym);
        reservation.setReserveTime(reserveTime);
        reservation.setStatus("RESERVED");

        // 3. 保存预约
        return reservationRepository.save(reservation);
    }

    public Iterable<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
} 