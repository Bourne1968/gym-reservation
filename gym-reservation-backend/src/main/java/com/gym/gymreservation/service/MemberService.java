package com.gym.gymreservation.service;

import com.gym.gymreservation.model.Member;
import com.gym.gymreservation.model.HealthRecord;
import com.gym.gymreservation.repository.MemberRepository;
import com.gym.gymreservation.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private HealthRecordRepository healthRecordRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    // 健康档案
    public HealthRecord getHealthRecordByMemberId(Long memberId) {
        return healthRecordRepository.findByMemberId(memberId);
    }

    public HealthRecord saveHealthRecord(HealthRecord record) {
        return healthRecordRepository.save(record);
    }
} 