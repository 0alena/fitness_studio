package ru.vsu.cs.emtsevaai.task1.service;

import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.service.dto.CreateTrainingSessionRequest;

import java.util.List;
import java.util.UUID;

public interface TrainingSessionService {

    TrainingSession createSession(CreateTrainingSessionRequest request);

    TrainingSession getSession(UUID id);

    List<TrainingSession> getAllSessions();

    void deleteSession(UUID id);

    void completeSession(UUID id);
}