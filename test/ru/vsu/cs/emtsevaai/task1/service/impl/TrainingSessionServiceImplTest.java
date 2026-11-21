package ru.vsu.cs.emtsevaai.task1.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.entity.enums.SessionStatus;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryEnrollmentRepository;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryTrainingSessionRepository;
import ru.vsu.cs.emtsevaai.task1.service.dto.CreateTrainingSessionRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrainingSessionServiceImplTest {

    private InMemoryTrainingSessionRepository sessionRepository;
    private TrainingSessionServiceImpl sessionService;
    private Trainer trainer;
    private WorkoutType workoutType;
    private LocalDateTime startsAt;

    @BeforeEach
    void setUp() {
        sessionRepository = new InMemoryTrainingSessionRepository();

        sessionService = new TrainingSessionServiceImpl(
                sessionRepository,
                new InMemoryEnrollmentRepository()
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

        startsAt = LocalDateTime.now().plusDays(1);
    }

    @Test
    void shouldCreateSessionWithValidData() {
        TrainingSession session = sessionService.createSession(
                createRequest(
                        trainer,
                        workoutType,
                        startsAt,
                        startsAt.plusHours(1),
                        TrainingLevel.BEGINNER,
                        10
                )
        );

        assertNotNull(session);
        assertEquals(trainer, session.getTrainer());
        assertEquals(workoutType, session.getWorkoutType());
        assertEquals(startsAt, session.getStartsAt());
        assertEquals(startsAt.plusHours(1), session.getEndsAt());
        assertEquals(TrainingLevel.BEGINNER, session.getRequiredLevel());
        assertEquals(10, session.getCapacity());
        assertEquals(SessionStatus.PLANNED, session.getStatus());
    }

    @Test
    void shouldRejectEndTimeBeforeStartTime() {
        assertThrows(
                ValidationException.class,
                () -> sessionService.createSession(
                        createRequest(
                                trainer,
                                workoutType,
                                startsAt,
                                startsAt,
                                TrainingLevel.BEGINNER,
                                10
                        )
                )
        );
    }

    @Test
    void shouldRejectSessionInThePast() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(2);

        assertThrows(
                ValidationException.class,
                () -> sessionService.createSession(
                        createRequest(
                                trainer,
                                workoutType,
                                pastStart,
                                pastStart.plusHours(1),
                                TrainingLevel.BEGINNER,
                                10
                        )
                )
        );
    }

    @Test
    void shouldRejectZeroCapacity() {
        assertThrows(
                ValidationException.class,
                () -> sessionService.createSession(
                        createRequest(
                                trainer,
                                workoutType,
                                startsAt,
                                startsAt.plusHours(1),
                                TrainingLevel.BEGINNER,
                                0
                        )
                )
        );
    }

    @Test
    void shouldRejectOverlappingSessionsForSameTrainer() {
        sessionService.createSession(
                createRequest(
                        trainer,
                        workoutType,
                        startsAt,
                        startsAt.plusHours(1),
                        TrainingLevel.BEGINNER,
                        10
                )
        );

        assertThrows(
                ValidationException.class,
                () -> sessionService.createSession(
                        createRequest(
                                trainer,
                                workoutType,
                                startsAt.plusMinutes(30),
                                startsAt.plusHours(1).plusMinutes(30),
                                TrainingLevel.BEGINNER,
                                10
                        )
                )
        );
    }

    private CreateTrainingSessionRequest createRequest(
            Trainer requestTrainer,
            WorkoutType requestWorkoutType,
            LocalDateTime requestStartsAt,
            LocalDateTime requestEndsAt,
            TrainingLevel requiredLevel,
            int capacity
    ) {
        return new CreateTrainingSessionRequest(
                requestTrainer,
                requestWorkoutType,
                requestStartsAt,
                requestEndsAt,
                requiredLevel,
                capacity
        );
    }
}