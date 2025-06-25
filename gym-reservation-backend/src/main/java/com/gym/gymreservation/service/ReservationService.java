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
import java.util.List;

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

        // 1. 时间限制：只能预约未来时间
        if (reserveTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("只能预约未来的时间！");
        }

        // 2. 用户冲突检测：该用户该时间段是否已有预约
        for (Reservation r : reservationRepository.findByUser(user)) {
            if (r.getReserveTime().equals(reserveTime) && "RESERVED".equals(r.getStatus())) {
                throw new RuntimeException("你该时间段已有预约，不能重复预约！");
            }
        }

        // 3. 场地冲突校验
        reservationRepository.findByGymAndReserveTimeAndStatus(gym, reserveTime, "RESERVED")
                .ifPresent(r -> { throw new RuntimeException("该场地该时间已被预约，请选择其他时间"); });

        // 4. 检查场地余量
        if (gym.getCapacity() <= 0) {
            throw new RuntimeException("该场地已无可用余量！");
        }

        // 5. 创建新的预约对象
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setGym(gym);
        reservation.setReserveTime(reserveTime);
        reservation.setStatus("RESERVED");

        // 6. 保存预约
        Reservation saved = reservationRepository.save(reservation);

        // 7. 更新场地余量
        gym.setCapacity(gym.getCapacity() - 1);
        gymRepository.save(gym);

        return saved;
    }

    public Iterable<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }
} 