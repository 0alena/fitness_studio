package ru.vsu.cs.emtsevaai.task1.service.impl;

import ru.vsu.cs.emtsevaai.task1.entity.Enrollment;
import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.entity.enums.SessionStatus;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.ex.EntityNotFoundException;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.AbstractRepository;
import ru.vsu.cs.emtsevaai.task1.service.TrainingSessionService;
import ru.vsu.cs.emtsevaai.task1.service.dto.CreateTrainingSessionRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final AbstractRepository<TrainingSession> sessionRepository;
    private final AbstractRepository<Enrollment> enrollmentRepository;

    public TrainingSessionServiceImpl(
            AbstractRepository<TrainingSession> sessionRepository,
            AbstractRepository<Enrollment> enrollmentRepository
    ) {
        if (sessionRepository == null || enrollmentRepository == null) {
            throw new IllegalArgumentException(
                    "Репозитории занятий и записей не могут быть null"
            );
        }

        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public TrainingSession createSession(
            CreateTrainingSessionRequest request
    ) {
        validateSession(request);

        Trainer trainer = request.getTrainer();
        WorkoutType workoutType = request.getWorkoutType();
        LocalDateTime startsAt = request.getStartsAt();
        LocalDateTime endsAt = request.getEndsAt();
        TrainingLevel requiredLevel = request.getRequiredLevel();
        int capacity = request.getCapacity();

        boolean trainerAlreadyBusy = sessionRepository.findAll().stream()
                .anyMatch(session ->
                        session.getStatus() == SessionStatus.PLANNED
                                && session.getTrainer() != null
                                && session.getStartsAt() != null
                                && session.getEndsAt() != null
                                && session.getTrainer().getId()
                                .equals(trainer.getId())
                                && startsAt.isBefore(session.getEndsAt())
                                && session.getStartsAt().isBefore(endsAt)
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
                endsAt,
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
        TrainingSession session = getSession(id);

        boolean hasEnrollments = enrollmentRepository.findAll().stream()
                .anyMatch(enrollment ->
                        enrollment.getSession() != null
                                && enrollment.getSession().getId()
                                .equals(session.getId())
                );

        if (hasEnrollments) {
            throw new ValidationException(
                    "Нельзя удалить занятие: с ним связаны записи посетителей"
            );
        }

        sessionRepository.deleteById(id);
    }

    @Override
    public void completeSession(UUID id) {
        TrainingSession session = getSession(id);

        if (session.getStatus() != SessionStatus.PLANNED) {
            throw new ValidationException(
                    "Завершить можно только запланированное занятие"
            );
        }

        if (session.getEndsAt() == null
                || session.getEndsAt().isAfter(LocalDateTime.now())) {
            throw new ValidationException(
                    "Нельзя завершить занятие до его окончания"
            );
        }

        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);
    }

    private void validateSession(CreateTrainingSessionRequest request) {
        if (request == null) {
            throw new ValidationException(
                    "Данные занятия не могут быть null"
            );
        }

        Trainer trainer = request.getTrainer();
        WorkoutType workoutType = request.getWorkoutType();
        LocalDateTime startsAt = request.getStartsAt();
        LocalDateTime endsAt = request.getEndsAt();
        TrainingLevel requiredLevel = request.getRequiredLevel();
        int capacity = request.getCapacity();

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

        if (endsAt == null) {
            throw new ValidationException(
                    "Дата и время окончания занятия не могут быть пустыми"
            );
        }

        if (!endsAt.isAfter(startsAt)) {
            throw new ValidationException(
                    "Время окончания должно быть позже времени начала"
            );
        }

        if (startsAt.isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    "Дата занятия не может быть в прошлом"
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