package ru.vsu.cs.emtsevaai.task1.service.impl;

import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.ex.EntityNotFoundException;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.AbstractRepository;
import ru.vsu.cs.emtsevaai.task1.service.TrainingSessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final AbstractRepository<TrainingSession> sessionRepository;

    public TrainingSessionServiceImpl(
            AbstractRepository<TrainingSession> sessionRepository
    ) {
        if (sessionRepository == null) {
            throw new IllegalArgumentException(
                    "Репозиторий занятий не может быть null"
            );
        }

        this.sessionRepository = sessionRepository;
    }

    @Override
    public TrainingSession createSession(
            Trainer trainer,
            WorkoutType workoutType,
            LocalDateTime startsAt,
            TrainingLevel requiredLevel,
            int capacity
    ) {
        validateSession(trainer, workoutType, startsAt, requiredLevel, capacity);

        boolean trainerAlreadyBusy = sessionRepository.findAll().stream()
                .anyMatch(session ->
                        session.getTrainer() != null
                                && session.getStartsAt() != null
                                && session.getTrainer().getId()
                                .equals(trainer.getId())
                                && session.getStartsAt().equals(startsAt)
                );

        if (trainerAlreadyBusy) {
            throw new ValidationException(
                    "У этого тренера уже есть занятие в это время"
            );
        }

        TrainingSession session = new TrainingSession(
                trainer,
                workoutType,
                startsAt,
                requiredLevel,
                capacity
        );

        return sessionRepository.save(session);
    }

    @Override
    public TrainingSession getSession(UUID id) {
        if (id == null) {
            throw new ValidationException("ID занятия не может быть null");
        }

        return sessionRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Занятие с ID " + id + " не найдено"
                        )
                );
    }

    @Override
    public List<TrainingSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Override
    public void deleteSession(UUID id) {
        if (id == null) {
            throw new ValidationException("ID занятия не может быть null");
        }

        boolean deleted = sessionRepository.deleteById(id);

        if (!deleted) {
            throw new EntityNotFoundException(
                    "Занятие с ID " + id + " не найдено"
            );
        }
    }

    private void validateSession(
            Trainer trainer,
            WorkoutType workoutType,
            LocalDateTime startsAt,
            TrainingLevel requiredLevel,
            int capacity
    ) {
        if (trainer == null) {
            throw new ValidationException(
                    "У занятия должен быть тренер"
            );
        }

        if (workoutType == null) {
            throw new ValidationException(
                    "У занятия должен быть выбран вид тренировки"
            );
        }

        if (startsAt == null) {
            throw new ValidationException(
                    "Дата и время занятия не могут быть пустыми"
            );
        }

        if (requiredLevel == null) {
            throw new ValidationException(
                    "Уровень подготовки не может быть пустым"
            );
        }

        if (capacity <= 0) {
            throw new ValidationException(
                    "Количество мест должно быть больше нуля"
            );
        }
    }
}