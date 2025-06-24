package com.gym.gymreservation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_reservation")
public class CourseReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_schedule_id")
    private CourseSchedule courseSchedule;

    private String status = "RESERVED";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CourseSchedule getCourseSchedule() { return courseSchedule; }
    public void setCourseSchedule(CourseSchedule courseSchedule) { this.courseSchedule = courseSchedule; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 