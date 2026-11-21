package ru.vsu.cs.emtsevaai.task1.service.impl;

import ru.vsu.cs.emtsevaai.task1.entity.Enrollment;
import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.entity.Visitor;
import ru.vsu.cs.emtsevaai.task1.entity.enums.EnrollmentStatus;
import ru.vsu.cs.emtsevaai.task1.entity.enums.SessionStatus;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.ex.EntityNotFoundException;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.AbstractRepository;
import ru.vsu.cs.emtsevaai.task1.service.EnrollmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EnrollmentServiceImpl implements EnrollmentService {

    private final AbstractRepository<Enrollment> enrollmentRepository;
    private final AbstractRepository<Visitor> visitorRepository;
    private final AbstractRepository<TrainingSession> sessionRepository;

    public EnrollmentServiceImpl(
            AbstractRepository<Enrollment> enrollmentRepository,
            AbstractRepository<Visitor> visitorRepository,
            AbstractRepository<TrainingSession> sessionRepository
    ) {
        if (enrollmentRepository == null
                || visitorRepository == null
                || sessionRepository == null) {
            throw new IllegalArgumentException(
                    "Репозитории не могут быть null"
            );
        }

        this.enrollmentRepository = enrollmentRepository;
        this.visitorRepository = visitorRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Enrollment createEnrollment(UUID visitorId, UUID sessionId) {
        validateEnrollmentIds(visitorId, sessionId);
        Visitor visitor = findVisitor(visitorId);
        TrainingSession session = findSession(sessionId);

        validateSessionCanBeBooked(session);
        validateVisitorLevel(visitor, session);
        ensureVisitorIsNotAlreadyEnrolled(visitorId, sessionId);
        ensureSessionHasFreeSpaces(session);

        Enrollment enrollment = new Enrollment(visitor, session);
        return enrollmentRepository.save(enrollment);
    }

    private void validateEnrollmentIds(UUID visitorId, UUID sessionId) {
        if (visitorId == null || sessionId == null) {
            throw new ValidationException(
                    "ID посетителя и занятия не могут быть null"
            );
        }
    }

    private Visitor findVisitor(UUID visitorId) {
        return visitorRepository.findById(visitorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Посетитель с ID " + visitorId + " не найден"
                ));
    }

    private TrainingSession findSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Занятие с ID " + sessionId + " не найдено"
                ));
    }

    private void validateSessionCanBeBooked(TrainingSession session) {
        if (session.getStatus() != SessionStatus.PLANNED) {
            throw new ValidationException(
                    "Записаться можно только на запланированное занятие"
            );
        }

        if (session.getStartsAt() == null
                || session.getStartsAt().isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    "Нельзя записаться на уже начавшееся занятие"
            );
        }
    }

    private void validateVisitorLevel(
            Visitor visitor,
            TrainingSession session
    ) {
        if (visitor.getLevel() == null
                || session.getRequiredLevel() == null) {
            throw new ValidationException(
                    "Не указан уровень подготовки посетителя или занятия"
            );
        }

        if (!isLevelEnough(visitor.getLevel(), session.getRequiredLevel())) {
            throw new ValidationException(
                    "Уровень подготовки посетителя не подходит для занятия"
            );
        }
    }

    private void ensureVisitorIsNotAlreadyEnrolled(
            UUID visitorId,
            UUID sessionId
    ) {
        boolean alreadyEnrolled = enrollmentRepository.findAll().stream()
                .anyMatch(enrollment ->
                        enrollment.getVisitor() != null
                                && enrollment.getSession() != null
                                && enrollment.getVisitor().getId()
                                .equals(visitorId)
                                && enrollment.getSession().getId()
                                .equals(sessionId)
                                && enrollment.getStatus()
                                != EnrollmentStatus.CANCELLED
                );

        if (alreadyEnrolled) {
            throw new ValidationException(
                    "Посетитель уже записан на это занятие"
            );
        }
    }

    private void ensureSessionHasFreeSpaces(TrainingSession session) {
        long bookedCount = enrollmentRepository.findAll().stream()
                .filter(enrollment ->
                        enrollment.getSession() != null
                                && enrollment.getSession().getId()
                                .equals(session.getId())
                )
                .filter(enrollment ->
                        enrollment.getStatus() == EnrollmentStatus.BOOKED
                )
                .count();

        if (bookedCount >= session.getCapacity()) {
            throw new ValidationException(
                    "На занятии больше нет свободных мест"
            );
        }
    }

    @Override
    public Enrollment getEnrollment(UUID id) {
        if (id == null) {
            throw new ValidationException(
                    "ID записи не может быть null"
            );
        }

        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Запись с ID " + id + " не найдена"
                ));
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByVisitor(UUID visitorId) {
        if (visitorId == null) {
            throw new ValidationException(
                    "ID посетителя не может быть null"
            );
        }

        visitorRepository.findById(visitorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Посетитель с ID " + visitorId + " не найден"
                ));

        return enrollmentRepository.findAll().stream()
                .filter(enrollment ->
                        enrollment.getVisitor() != null
                                && enrollment.getVisitor().getId()
                                .equals(visitorId)
                )
                .collect(Collectors.toList());
    }

    @Override
    public void cancelEnrollment(UUID id) {
        Enrollment enrollment = getEnrollment(id);

        if (enrollment.getStatus() != EnrollmentStatus.BOOKED) {
            throw new ValidationException(
                    "Отменить можно только активную запись"
            );
        }

        if (enrollment.getSession() == null
                || enrollment.getSession().getStartsAt() == null
                || !enrollment.getSession().getStartsAt()
                .isAfter(LocalDateTime.now())) {
            throw new ValidationException(
                    "Нельзя отменить запись после начала занятия"
            );
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void markAttended(UUID id) {
        Enrollment enrollment = getEnrollment(id);

        if (enrollment.getStatus() != EnrollmentStatus.BOOKED) {
            throw new ValidationException(
                    "Отметить посещение можно только для активной записи"
            );
        }

        ensureSessionHasEnded(enrollment);

        enrollment.setStatus(EnrollmentStatus.ATTENDED);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void markMissed(UUID id) {
        Enrollment enrollment = getEnrollment(id);

        if (enrollment.getStatus() != EnrollmentStatus.BOOKED) {
            throw new ValidationException(
                    "Отметить пропуск можно только для активной записи"
            );
        }

        ensureSessionHasEnded(enrollment);

        enrollment.setStatus(EnrollmentStatus.MISSED);
        enrollmentRepository.save(enrollment);
    }

    private boolean isLevelEnough(
            TrainingLevel visitorLevel,
            TrainingLevel requiredLevel
    ) {
        return getLevelRank(visitorLevel) >= getLevelRank(requiredLevel);
    }

    private void ensureSessionHasEnded(Enrollment enrollment) {
        if (enrollment.getSession() == null
                || enrollment.getSession().getEndsAt() == null
                || enrollment.getSession().getEndsAt()
                .isAfter(LocalDateTime.now())) {
            throw new ValidationException(
                    "Отметить посещение или пропуск можно только после окончания занятия"
            );
        }
    }

    private int getLevelRank(TrainingLevel level) {
        switch (level) {
            case BEGINNER:
                return 1;
            case INTERMEDIATE:
                return 2;
            case ADVANCED:
                return 3;
            default:
                throw new ValidationException(
                        "Неизвестный уровень подготовки"
                );
        }
    }
}