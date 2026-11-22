package ru.vsu.cs.emtsevaai.task1.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.emtsevaai.task1.entity.Enrollment;
import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.entity.Visitor;
import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.entity.enums.EnrollmentStatus;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryEnrollmentRepository;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryTrainingSessionRepository;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryVisitorRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnrollmentServiceImplTest {

    private InMemoryEnrollmentRepository enrollmentRepository;
    private InMemoryVisitorRepository visitorRepository;
    private InMemoryTrainingSessionRepository sessionRepository;
    private EnrollmentServiceImpl enrollmentService;

    private Visitor beginnerVisitor;
    private Visitor secondVisitor;
    private Trainer trainer;
    private WorkoutType workoutType;

    @BeforeEach
    void setUp() {
        enrollmentRepository = new InMemoryEnrollmentRepository();
        visitorRepository = new InMemoryVisitorRepository();
        sessionRepository = new InMemoryTrainingSessionRepository();

        enrollmentService = new EnrollmentServiceImpl(
                enrollmentRepository,
                visitorRepository,
                sessionRepository
        );

        beginnerVisitor = visitorRepository.save(
                new Visitor(
                        "Иван Иванов",
                        "+79991110001",
                        TrainingLevel.BEGINNER
                )
        );

        secondVisitor = visitorRepository.save(
                new Visitor(
                        "Пётр Петров",
                        "+79991110002",
                        TrainingLevel.BEGINNER
                )
        );

        trainer = new Trainer(
                "Анна Смирнова",
                "+79991112233",
                5
        );

        workoutType = new WorkoutType(
                "Йога",
                "Групповое занятие по йоге"
        );
    }

    @Test
    void shouldCreateEnrollmentWithValidData() {
        TrainingSession session = createSession(
                TrainingLevel.BEGINNER,
                5,
                LocalDateTime.now().plusDays(1)
        );

        Enrollment enrollment = enrollmentService.createEnrollment(
                beginnerVisitor.getId(),
                session.getId()
        );

        assertNotNull(enrollment);
        assertEquals(beginnerVisitor, enrollment.getVisitor());
        assertEquals(session, enrollment.getSession());
        assertEquals(EnrollmentStatus.BOOKED, enrollment.getStatus());
    }

    @Test
    void shouldRejectVisitorWithInsufficientTrainingLevel() {
        TrainingSession session = createSession(
                TrainingLevel.ADVANCED,
                5,
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(
                ValidationException.class,
                () -> enrollmentService.createEnrollment(
                        beginnerVisitor.getId(),
                        session.getId()
                )
        );
    }

    @Test
    void shouldRejectDuplicateEnrollment() {
        TrainingSession session = createSession(
                TrainingLevel.BEGINNER,
                5,
                LocalDateTime.now().plusDays(1)
        );

        enrollmentService.createEnrollment(
                beginnerVisitor.getId(),
                session.getId()
        );

        assertThrows(
                ValidationException.class,
                () -> enrollmentService.createEnrollment(
                        beginnerVisitor.getId(),
                        session.getId()
                )
        );
    }

    @Test
    void shouldRejectEnrollmentWhenSessionIsFull() {
        TrainingSession session = createSession(
                TrainingLevel.BEGINNER,
                1,
                LocalDateTime.now().plusDays(1)
        );

        enrollmentService.createEnrollment(
                beginnerVisitor.getId(),
                session.getId()
        );

        assertThrows(
                ValidationException.class,
                () -> enrollmentService.createEnrollment(
                        secondVisitor.getId(),
                        session.getId()
                )
        );
    }

    @Test
    void shouldRejectEnrollmentForSessionThatAlreadyStarted() {
        TrainingSession session = createSession(
                TrainingLevel.BEGINNER,
                5,
                LocalDateTime.now().minusHours(2)
        );

        assertThrows(
                ValidationException.class,
                () -> enrollmentService.createEnrollment(
                        beginnerVisitor.getId(),
                        session.getId()
                )
        );
    }

    private TrainingSession createSession(
            TrainingLevel requiredLevel,
            int capacity,
            LocalDateTime startsAt
    ) {
        TrainingSession session = new TrainingSession(
                trainer,
                workoutType,
                startsAt,
                startsAt.plusHours(1),
                requiredLevel,
                capacity
        );

        return sessionRepository.save(session);
    }
}