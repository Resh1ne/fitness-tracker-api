package com.example.fitnesstracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "workout_date", nullable = false)
    private LocalDate date;

    @Column(name = "duration_minutes", nullable = false)
    private Integer duration;

    @Column(name = "calories_burned", nullable = false)
    private Integer calories;

    @Column(name = "notes")
    private String notes;
}