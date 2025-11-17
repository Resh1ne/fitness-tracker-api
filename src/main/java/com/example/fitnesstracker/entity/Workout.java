package com.example.fitnesstracker.entity;

import com.example.fitnesstracker.entity.enums.WorkoutType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_type")
    private WorkoutType type;
}