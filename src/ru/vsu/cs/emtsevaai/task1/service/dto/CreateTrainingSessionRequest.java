package ru.vsu.cs.emtsevaai.task1.service.dto;

import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;

import java.time.LocalDateTime;

public class CreateTrainingSessionRequest {

    private final Trainer trainer;
    private final WorkoutType workoutType;
    private final LocalDateTime startsAt;
    private final LocalDateTime endsAt;
    private final TrainingLevel requiredLevel;
    private final int capacity;

    public CreateTrainingSessionRequest(
            Trainer trainer,
            WorkoutType workoutType,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            TrainingLevel requiredLevel,
            int capacity
    ) {
        this.trainer = trainer;
        this.workoutType = workoutType;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.requiredLevel = requiredLevel;
        this.capacity = capacity;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public TrainingLevel getRequiredLevel() {
        return requiredLevel;
    }

    public int getCapacity() {
        return capacity;
    }
}