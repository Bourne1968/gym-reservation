package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    HealthRecord findByMemberId(Long memberId);
} 