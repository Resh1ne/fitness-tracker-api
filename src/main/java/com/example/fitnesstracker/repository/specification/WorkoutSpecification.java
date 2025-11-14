package com.example.fitnesstracker.repository.specification;

import com.example.fitnesstracker.entity.Workout;
import com.example.fitnesstracker.entity.enums.WorkoutType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class WorkoutSpecification {

    public static Specification<Workout> hasType(WorkoutType type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    public static Specification<Workout> dateFrom(LocalDate dateFrom) {
        return (root, query, cb) ->
                dateFrom == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("date"), dateFrom);
    }

    public static Specification<Workout> dateTo(LocalDate dateTo) {
        return (root, query, cb) ->
                dateTo == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("date"), dateTo);
    }

    public static Specification<Workout> durationFrom(Integer durationFrom) {
        return (root, query, cb) ->
                durationFrom == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("duration"), durationFrom);
    }

    public static Specification<Workout> durationTo(Integer durationTo) {
        return (root, query, cb) ->
                durationTo == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("duration"), durationTo);
    }
}
