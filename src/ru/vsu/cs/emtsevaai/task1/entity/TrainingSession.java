package ru.vsu.cs.emtsevaai.task1.entity;

import ru.vsu.cs.emtsevaai.task1.entity.enums.SessionStatus;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;

import java.time.LocalDateTime;

public class TrainingSession extends AbstractEntity {
    private Trainer trainer;
    private WorkoutType workoutType;
    private LocalDateTime startsAt;
    private TrainingLevel requiredLevel;
    private int capacity;
    private SessionStatus status;

    public TrainingSession(
            Trainer trainer,
            WorkoutType workoutType,
            LocalDateTime startsAt,
            TrainingLevel requiredLevel,
            int capacity
    ) {
        this.trainer = trainer;
        this.workoutType = workoutType;
        this.startsAt = startsAt;
        this.requiredLevel = requiredLevel;
        this.capacity = capacity;
        this.status = SessionStatus.PLANNED;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public TrainingLevel getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(TrainingLevel requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}