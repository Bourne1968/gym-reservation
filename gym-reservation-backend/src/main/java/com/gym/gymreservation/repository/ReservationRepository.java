package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.Reservation;
import com.gym.gymreservation.model.User;
import com.gym.gymreservation.model.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    Optional<Reservation> findByGymAndReserveTimeAndStatus(Gym gym, LocalDateTime reserveTime, String status);
} 