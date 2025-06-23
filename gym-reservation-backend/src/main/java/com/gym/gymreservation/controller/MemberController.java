package com.gym.gymreservation.controller;

import com.gym.gymreservation.model.Member;
import com.gym.gymreservation.model.HealthRecord;
import com.gym.gymreservation.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberService.getMemberById(id);
        return member.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Member> addMember(@RequestBody Member member) {
        return ResponseEntity.ok(memberService.saveMember(member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        member.setId(id);
        return ResponseEntity.ok(memberService.saveMember(member));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok("删除成功");
    }

    // 健康档案
    @GetMapping("/{id}/health")
    public ResponseEntity<HealthRecord> getHealthRecord(@PathVariable Long id) {
        HealthRecord record = memberService.getHealthRecordByMemberId(id);
        if (record == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(record);
    }

    @PostMapping("/{id}/health")
    public ResponseEntity<HealthRecord> saveHealthRecord(@PathVariable Long id, @RequestBody HealthRecord record) {
        Member member = new Member(); member.setId(id);
        record.setMember(member);
        return ResponseEntity.ok(memberService.saveHealthRecord(record));
    }
} 