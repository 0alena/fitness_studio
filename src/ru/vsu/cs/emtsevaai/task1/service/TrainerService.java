package ru.vsu.cs.emtsevaai.task1.service;

import ru.vsu.cs.emtsevaai.task1.entity.Trainer;

import java.util.List;
import java.util.UUID;

public interface TrainerService {

    Trainer createTrainer(String fullName, String phone, int experienceYears);

    Trainer getTrainer(UUID id);

    List<Trainer> getAllTrainers();
}