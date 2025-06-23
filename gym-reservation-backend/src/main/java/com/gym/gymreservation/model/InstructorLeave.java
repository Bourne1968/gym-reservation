package com.gym.gymreservation.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class InstructorLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private String status; // PENDING, APPROVED, REJECTED

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getInstructor() { return instructor; }
    public void setInstructor(User instructor) { this.instructor = instructor; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 