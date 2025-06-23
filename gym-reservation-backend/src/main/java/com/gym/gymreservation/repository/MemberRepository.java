package com.gym.gymreservation.repository;

import com.gym.gymreservation.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByCardNumber(String cardNumber);
} 