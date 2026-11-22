package ru.vsu.cs.emtsevaai.task1.service.impl;

import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;
import ru.vsu.cs.emtsevaai.task1.ex.EntityNotFoundException;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.AbstractRepository;
import ru.vsu.cs.emtsevaai.task1.service.WorkoutTypeService;
import ru.vsu.cs.emtsevaai.task1.util.StringUtils;

import java.util.List;
import java.util.UUID;

public class WorkoutTypeServiceImpl implements WorkoutTypeService {

    private final AbstractRepository<WorkoutType> workoutTypeRepository;

    public WorkoutTypeServiceImpl(
            AbstractRepository<WorkoutType> workoutTypeRepository
    ) {
        if (workoutTypeRepository == null) {
            throw new IllegalArgumentException(
                    "Репозиторий видов тренировок не может быть null"
            );
        }

        this.workoutTypeRepository = workoutTypeRepository;
    }

    @Override
    public WorkoutType createWorkoutType(String name, String description) {
        if (StringUtils.isNullOrBlank(name)) {
            throw new ValidationException(
                    "Название тренировки не может быть пустым"
            );
        }

        String normalizedName = name.trim();

        boolean nameExists = workoutTypeRepository.findAll().stream()
                .anyMatch(workoutType ->
                        workoutType.getName() != null
                                && workoutType.getName().trim()
                                .equalsIgnoreCase(normalizedName)
                );

        if (nameExists) {
            throw new ValidationException(
                    "Такой вид тренировки уже существует"
            );
        }

        String normalizedDescription =
                description == null ? "" : description.trim();

        WorkoutType workoutType = new WorkoutType(
                normalizedName,
                normalizedDescription
        );

        return workoutTypeRepository.save(workoutType);
    }

    @Override
    public WorkoutType getWorkoutType(UUID id) {
        if (id == null) {
            throw new ValidationException(
                    "ID вида тренировки не может быть null"
            );
        }

        return workoutTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Вид тренировки с ID " + id + " не найден"
                ));
    }

    @Override
    public List<WorkoutType> getAllWorkoutTypes() {
        return workoutTypeRepository.findAll();
    }
}