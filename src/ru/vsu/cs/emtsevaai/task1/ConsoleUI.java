package ru.vsu.cs.emtsevaai.task1;

import ru.vsu.cs.emtsevaai.task1.entity.Enrollment;
import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.entity.TrainingSession;
import ru.vsu.cs.emtsevaai.task1.entity.Visitor;
import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.entity.enums.EnrollmentStatus;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.service.EnrollmentService;
import ru.vsu.cs.emtsevaai.task1.service.TrainerService;
import ru.vsu.cs.emtsevaai.task1.service.TrainingSessionService;
import ru.vsu.cs.emtsevaai.task1.service.VisitorService;
import ru.vsu.cs.emtsevaai.task1.service.WorkoutTypeService;
import ru.vsu.cs.emtsevaai.task1.service.dto.CreateTrainingSessionRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleUI {

    private final VisitorService visitorService;
    private final TrainerService trainerService;
    private final WorkoutTypeService workoutTypeService;
    private final TrainingSessionService sessionService;
    private final EnrollmentService enrollmentService;
    private final Scanner scanner = new Scanner(System.in);

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleUI(
            VisitorService visitorService,
            TrainerService trainerService,
            WorkoutTypeService workoutTypeService,
            TrainingSessionService sessionService,
            EnrollmentService enrollmentService
    ) {
        this.visitorService = visitorService;
        this.trainerService = trainerService;
        this.workoutTypeService = workoutTypeService;
        this.sessionService = sessionService;
        this.enrollmentService = enrollmentService;
    }

    public void start() {
        boolean running = true;

        while (running) {
            printMainMenu();
            System.out.print("Выберите действие: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    manageVisitors();
                    break;
                case "2":
                    manageSessions();
                    break;
                case "3":
                    manageEnrollments();
                    break;
                case "4":
                    manageTrainers();
                    break;
                case "5":
                    manageWorkoutTypes();
                    break;
                case "0":
                    running = false;
                    System.out.println("Программа завершена.");
                    break;
                default:
                    System.out.println("Неизвестная команда. Попробуйте ещё раз.");
                    break;
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Система управления фитнес-студией ===");
        System.out.println("1. Управление посетителями");
        System.out.println("2. Управление занятиями");
        System.out.println("3. Записи на занятия");
        System.out.println("4. Управление тренерами");
        System.out.println("5. Управление видами тренировок");
        System.out.println("0. Выход");
    }

    private void manageVisitors() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n=== Управление посетителями ===");
            System.out.println("1. Показать всех посетителей");
            System.out.println("2. Добавить посетителя");
            System.out.println("3. Удалить посетителя");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runSafely(this::showVisitors);
                    break;
                case "2":
                    runSafely(this::addVisitor);
                    break;
                case "3":
                    runSafely(this::deleteVisitor);
                    break;
                case "0":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Неизвестная команда.");
                    break;
            }
        }
    }

    private void showVisitors() {
        List<Visitor> visitors = visitorService.getAllVisitors();

        if (visitors.isEmpty()) {
            System.out.println("Посетителей пока нет.");
            return;
        }

        System.out.println("\n=== Посетители ===");
        for (Visitor visitor : visitors) {
            System.out.println("Имя: " + visitor.getFullName());
            System.out.println("Телефон: " + visitor.getPhone());
            System.out.println("Уровень: " + visitor.getLevel());
            System.out.println("ID: " + visitor.getId());
            System.out.println("--------------------");
        }
    }

    private void addVisitor() {
        System.out.print("Введите имя и фамилию: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Введите телефон: ");
        String phone = scanner.nextLine().trim();

        TrainingLevel level = readTrainingLevel();

        Visitor visitor = visitorService.createVisitor(
                fullName,
                phone,
                level
        );

        System.out.println("Посетитель добавлен.");
        System.out.println("ID посетителя: " + visitor.getId());
    }

    private void deleteVisitor() {
        UUID visitorId = readUuid(
                "Введите ID посетителя для удаления (0 — назад): "
        );
        if (visitorId == null) {
            return;
        }

        List<Enrollment> enrollments =
                enrollmentService.getEnrollmentsByVisitor(visitorId);

        if (!enrollments.isEmpty()) {
            System.out.println(
                    "Нельзя удалить посетителя: у него есть записи на занятия."
            );
            return;
        }

        visitorService.deleteVisitor(visitorId);
        System.out.println("Посетитель удалён.");
    }

    private void manageSessions() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n=== Управление занятиями ===");
            System.out.println("1. Показать все занятия");
            System.out.println("2. Создать занятие");
            System.out.println("3. Завершить занятие");
            System.out.println("4. Удалить занятие");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runSafely(this::showSessions);
                    break;
                case "2":
                    runSafely(this::addSession);
                    break;
                case "3":
                    runSafely(this::completeSession);
                    break;
                case "4":
                    runSafely(this::deleteSession);
                    break;
                case "0":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Неизвестная команда.");
                    break;
            }
        }
    }

    private void showSessions() {
        List<TrainingSession> sessions = sessionService.getAllSessions();

        if (sessions.isEmpty()) {
            System.out.println("Занятий пока нет.");
            return;
        }

        System.out.println("\n=== Занятия ===");
        for (TrainingSession session : sessions) {
            printSession(session);
        }
    }

    private void addSession() {
        Trainer trainer = selectTrainer();
        if (trainer == null) {
            return;
        }

        WorkoutType workoutType = selectWorkoutType();
        if (workoutType == null) {
            return;
        }

        LocalDateTime startsAt = readDateTime("Начало занятия");
        LocalDateTime endsAt = readDateTime("Окончание занятия");
        TrainingLevel requiredLevel = readTrainingLevel();
        int capacity = readInt("Количество мест: ");

        CreateTrainingSessionRequest request = new CreateTrainingSessionRequest(
                trainer,
                workoutType,
                startsAt,
                endsAt,
                requiredLevel,
                capacity
        );

        TrainingSession session = sessionService.createSession(request);

        System.out.println("Занятие создано.");
        System.out.println("ID занятия: " + session.getId());
    }

    private void completeSession() {
        UUID sessionId = readUuid(
                "Введите ID занятия для завершения (0 — назад): "
        );
        if (sessionId == null) {
            return;
        }

        sessionService.completeSession(sessionId);
        System.out.println("Занятие завершено.");
    }

    private void deleteSession() {
        UUID sessionId = readUuid(
                "Введите ID занятия для удаления (0 — назад): "
        );
        if (sessionId == null) {
            return;
        }

        sessionService.deleteSession(sessionId);
        System.out.println("Занятие удалено.");
    }

    private void printSession(TrainingSession session) {
        long bookedCount = enrollmentService.getAllEnrollments()
                .stream()
                .filter(enrollment ->
                        enrollment.getSession() != null
                                && enrollment.getSession().getId()
                                .equals(session.getId())
                )
                .filter(enrollment ->
                        enrollment.getStatus() == EnrollmentStatus.BOOKED
                )
                .count();

        System.out.println("\nТренировка: "
                + session.getWorkoutType().getName());
        System.out.println("Тренер: "
                + session.getTrainer().getFullName());
        System.out.println("Время: "
                + session.getStartsAt().format(dateTimeFormatter)
                + " — "
                + session.getEndsAt().format(dateTimeFormatter));
        System.out.println("Нужный уровень: "
                + session.getRequiredLevel());
        System.out.println("Записано мест: "
                + bookedCount + " из " + session.getCapacity());
        System.out.println("Статус занятия: "
                + session.getStatus());
        System.out.println("ID занятия: "
                + session.getId());
        System.out.println("--------------------");
    }

    private void manageEnrollments() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n=== Записи на занятия ===");
            System.out.println("1. Показать занятия");
            System.out.println("2. Записать посетителя");
            System.out.println("3. Показать все записи");
            System.out.println("4. Показать записи посетителя");
            System.out.println("5. Отменить запись");
            System.out.println("6. Отметить посещение");
            System.out.println("7. Отметить пропуск");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    runSafely(this::showSessions);
                    break;
                case "2":
                    runSafely(this::createEnrollment);
                    break;
                case "3":
                    runSafely(this::showEnrollments);
                    break;
                case "4":
                    runSafely(this::showEnrollmentsByVisitor);
                    break;
                case "5":
                    runSafely(this::cancelEnrollment);
                    break;
                case "6":
                    runSafely(this::markAttended);
                    break;
                case "7":
                    runSafely(this::markMissed);
                    break;
                case "0":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Неизвестная команда.");
                    break;
            }
        }
    }

    private void manageTrainers() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n=== Управление тренерами ===");
            System.out.println("1. Показать всех тренеров");
            System.out.println("2. Добавить тренера");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    runSafely(this::showTrainers);
                    break;
                case "2":
                    runSafely(this::addTrainer);
                    break;
                case "0":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Неизвестная команда.");
            }
        }
    }

    private void showTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        if (trainers.isEmpty()) {
            System.out.println("Тренеров пока нет.");
            return;
        }

        System.out.println("\n=== Тренеры ===");
        for (Trainer trainer : trainers) {
            System.out.println("Имя: " + trainer.getFullName());
            System.out.println("Телефон: " + trainer.getPhone());
            System.out.println("Стаж: " + trainer.getExperienceYears() + " лет");
            System.out.println("ID: " + trainer.getId());
            System.out.println("--------------------");
        }
    }

    private void addTrainer() {
        System.out.print("Имя и фамилия тренера: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Телефон тренера: ");
        String phone = scanner.nextLine().trim();
        int experienceYears = readInt("Стаж тренера в годах: ");

        Trainer trainer = trainerService.createTrainer(
                fullName,
                phone,
                experienceYears
        );
        System.out.println("Тренер добавлен. ID: " + trainer.getId());
    }

    private void manageWorkoutTypes() {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n=== Управление видами тренировок ===");
            System.out.println("1. Показать все виды тренировок");
            System.out.println("2. Добавить вид тренировки");
            System.out.println("0. Назад");
            System.out.print("Выберите действие: ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    runSafely(this::showWorkoutTypes);
                    break;
                case "2":
                    runSafely(this::addWorkoutType);
                    break;
                case "0":
                    inMenu = false;
                    break;
                default:
                    System.out.println("Неизвестная команда.");
            }
        }
    }

    private void showWorkoutTypes() {
        List<WorkoutType> workoutTypes = workoutTypeService.getAllWorkoutTypes();
        if (workoutTypes.isEmpty()) {
            System.out.println("Видов тренировок пока нет.");
            return;
        }

        System.out.println("\n=== Виды тренировок ===");
        for (WorkoutType workoutType : workoutTypes) {
            System.out.println("Название: " + workoutType.getName());
            System.out.println("Описание: " + workoutType.getDescription());
            System.out.println("ID: " + workoutType.getId());
            System.out.println("--------------------");
        }
    }

    private void addWorkoutType() {
        System.out.print("Название тренировки: ");
        String name = scanner.nextLine().trim();
        System.out.print("Описание тренировки: ");
        String description = scanner.nextLine().trim();

        WorkoutType workoutType = workoutTypeService.createWorkoutType(
                name,
                description
        );
        System.out.println("Вид тренировки добавлен. ID: " + workoutType.getId());
    }

    private Trainer selectTrainer() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        if (trainers.isEmpty()) {
            System.out.println("Сначала добавьте тренера в разделе «Управление тренерами».");
            return null;
        }

        System.out.println("\nВыберите тренера (0 — отмена):");
        for (int i = 0; i < trainers.size(); i++) {
            System.out.printf("%d. %s, стаж %d лет%n",
                    i + 1,
                    trainers.get(i).getFullName(),
                    trainers.get(i).getExperienceYears());
        }

        int selection = readInt("Ваш выбор: ");
        if (selection == 0) {
            return null;
        }
        if (selection < 1 || selection > trainers.size()) {
            throw new IllegalArgumentException("Нет тренера с таким номером");
        }
        return trainers.get(selection - 1);
    }

    private WorkoutType selectWorkoutType() {
        List<WorkoutType> workoutTypes = workoutTypeService.getAllWorkoutTypes();
        if (workoutTypes.isEmpty()) {
            System.out.println("Сначала добавьте вид тренировки в соответствующем разделе.");
            return null;
        }

        System.out.println("\nВыберите вид тренировки (0 — отмена):");
        for (int i = 0; i < workoutTypes.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, workoutTypes.get(i).getName());
        }

        int selection = readInt("Ваш выбор: ");
        if (selection == 0) {
            return null;
        }
        if (selection < 1 || selection > workoutTypes.size()) {
            throw new IllegalArgumentException("Нет вида тренировки с таким номером");
        }
        return workoutTypes.get(selection - 1);
    }

    private void createEnrollment() {
        UUID visitorId = readUuid("Введите ID посетителя (0 — назад): ");
        if (visitorId == null) {
            return;
        }

        UUID sessionId = readUuid("Введите ID занятия (0 — назад): ");
        if (sessionId == null) {
            return;
        }

        Enrollment enrollment =
                enrollmentService.createEnrollment(visitorId, sessionId);

        System.out.println("Посетитель записан на занятие.");
        System.out.println("ID записи: " + enrollment.getId());
    }

    private void showEnrollments() {
        List<Enrollment> enrollments =
                enrollmentService.getAllEnrollments();

        if (enrollments.isEmpty()) {
            System.out.println("Записей пока нет.");
            return;
        }

        System.out.println("\n=== Все записи ===");
        for (Enrollment enrollment : enrollments) {
            printEnrollment(enrollment);
        }
    }

    private void showEnrollmentsByVisitor() {
        UUID visitorId = readUuid("Введите ID посетителя (0 — назад): ");
        if (visitorId == null) {
            return;
        }

        List<Enrollment> enrollments =
                enrollmentService.getEnrollmentsByVisitor(visitorId);

        if (enrollments.isEmpty()) {
            System.out.println("У этого посетителя нет записей.");
            return;
        }

        for (Enrollment enrollment : enrollments) {
            printEnrollment(enrollment);
        }
    }

    private void cancelEnrollment() {
        UUID enrollmentId = readUuid(
                "Введите ID записи для отмены (0 — назад): "
        );
        if (enrollmentId == null) {
            return;
        }

        enrollmentService.cancelEnrollment(enrollmentId);
        System.out.println("Запись отменена.");
    }

    private void markAttended() {
        UUID enrollmentId = readUuid(
                "Введите ID записи для отметки посещения (0 — назад): "
        );
        if (enrollmentId == null) {
            return;
        }

        enrollmentService.markAttended(enrollmentId);
        System.out.println("Посещение отмечено.");
    }

    private void markMissed() {
        UUID enrollmentId = readUuid(
                "Введите ID записи для отметки пропуска (0 — назад): "
        );
        if (enrollmentId == null) {
            return;
        }

        enrollmentService.markMissed(enrollmentId);
        System.out.println("Отмечен пропуск.");
    }

    private void printEnrollment(Enrollment enrollment) {
        System.out.println("\nПосетитель: "
                + enrollment.getVisitor().getFullName());
        System.out.println("Занятие: "
                + enrollment.getSession()
                .getWorkoutType()
                .getName());
        System.out.println("Дата и время: "
                + enrollment.getSession()
                .getStartsAt()
                .format(dateTimeFormatter));
        System.out.println("Время окончания: "
                + enrollment.getSession()
                .getEndsAt()
                .format(dateTimeFormatter));
        System.out.println("Статус записи: "
                + enrollment.getStatus());
        System.out.println("ID записи: "
                + enrollment.getId());
        System.out.println("--------------------");
    }

    private TrainingLevel readTrainingLevel() {
        while (true) {
            System.out.println("Выберите уровень подготовки:");
            System.out.println("1. BEGINNER — начальный");
            System.out.println("2. INTERMEDIATE — средний");
            System.out.println("3. ADVANCED — продвинутый");
            System.out.print("Ваш выбор: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return TrainingLevel.BEGINNER;
                case "2":
                    return TrainingLevel.INTERMEDIATE;
                case "3":
                    return TrainingLevel.ADVANCED;
                default:
                    System.out.println("Введите 1, 2 или 3.");
                    break;
            }
        }
    }

    private LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(
                    prompt + " (например, 2026-12-01 18:30): "
            );

            String input = scanner.nextLine().trim();

            try {
                return LocalDateTime.parse(input, dateTimeFormatter);
            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Неверный формат. Пример: 2026-12-01 18:30"
                );
            }
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private UUID readUuid(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Действие отменено.");
                return null;
            }

            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException exception) {
                System.out.println(
                        "Неверный ID. Скопируйте полный ID из списка "
                                + "или введите 0 для возврата."
                );
            }
        }
    }

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            System.out.println("Ошибка: " + exception.getMessage());
        }
    }
}