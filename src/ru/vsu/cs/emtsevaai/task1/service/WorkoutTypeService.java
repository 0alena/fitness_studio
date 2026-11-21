package ru.vsu.cs.emtsevaai.task1.service;

import ru.vsu.cs.emtsevaai.task1.entity.WorkoutType;

import java.util.List;
import java.util.UUID;

public interface WorkoutTypeService {

    WorkoutType createWorkoutType(String name, String description);

    WorkoutType getWorkoutType(UUID id);

    List<WorkoutType> getAllWorkoutTypes();
}