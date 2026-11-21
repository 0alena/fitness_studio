package ru.vsu.cs.emtsevaai.task1.service;

import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TrainingSessionService {

    TrainingSession createSession(
            Trainer trainer,
            WorkoutType workoutType,
            LocalDateTime startsAt,
            TrainingLevel requiredLevel,
            int capacity
    );

    TrainingSession getSession(UUID id);

    List<TrainingSession> getAllSessions();

    void deleteSession(UUID id);
}