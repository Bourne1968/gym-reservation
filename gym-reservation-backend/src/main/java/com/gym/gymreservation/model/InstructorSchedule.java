package com.gym.gymreservation.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
public class InstructorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    private String weekDay; // 周几，如 MONDAY, TUESDAY
    private LocalTime startTime;
    private LocalTime endTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
} 