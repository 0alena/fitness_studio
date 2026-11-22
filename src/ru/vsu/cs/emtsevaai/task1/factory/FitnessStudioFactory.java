package ru.vsu.cs.emtsevaai.task1.factory;

import ru.vsu.cs.emtsevaai.task1.ConsoleUI;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryEnrollmentRepository;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryTrainerRepository;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryTrainingSessionRepository;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryVisitorRepository;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryWorkoutTypeRepository;
import ru.vsu.cs.emtsevaai.task1.service.EnrollmentService;
import ru.vsu.cs.emtsevaai.task1.service.TrainerService;
import ru.vsu.cs.emtsevaai.task1.service.TrainingSessionService;
import ru.vsu.cs.emtsevaai.task1.service.VisitorService;
import ru.vsu.cs.emtsevaai.task1.service.WorkoutTypeService;
import ru.vsu.cs.emtsevaai.task1.service.impl.EnrollmentServiceImpl;
import ru.vsu.cs.emtsevaai.task1.service.impl.TrainerServiceImpl;
import ru.vsu.cs.emtsevaai.task1.service.impl.TrainingSessionServiceImpl;
import ru.vsu.cs.emtsevaai.task1.service.impl.VisitorServiceImpl;
import ru.vsu.cs.emtsevaai.task1.service.impl.WorkoutTypeServiceImpl;

public final class FitnessStudioFactory {

    private FitnessStudioFactory() {
    }

    public static ConsoleUI createConsoleUI() {
        InMemoryVisitorRepository visitorRepository =
                new InMemoryVisitorRepository();
        InMemoryTrainingSessionRepository sessionRepository =
                new InMemoryTrainingSessionRepository();
        InMemoryEnrollmentRepository enrollmentRepository =
                new InMemoryEnrollmentRepository();
        InMemoryTrainerRepository trainerRepository =
                new InMemoryTrainerRepository();
        InMemoryWorkoutTypeRepository workoutTypeRepository =
                new InMemoryWorkoutTypeRepository();

        VisitorService visitorService =
                new VisitorServiceImpl(visitorRepository);
        TrainerService trainerService =
                new TrainerServiceImpl(trainerRepository);
        WorkoutTypeService workoutTypeService =
                new WorkoutTypeServiceImpl(workoutTypeRepository);
        TrainingSessionService sessionService =
                new TrainingSessionServiceImpl(
                        sessionRepository,
                        enrollmentRepository
                );
        EnrollmentService enrollmentService =
                new EnrollmentServiceImpl(
                        enrollmentRepository,
                        visitorRepository,
                        sessionRepository
                );

        return new ConsoleUI(
                visitorService,
                trainerService,
                workoutTypeService,
                sessionService,
                enrollmentService
        );
    }
}