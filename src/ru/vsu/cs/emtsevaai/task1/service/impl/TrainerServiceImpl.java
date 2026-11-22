package ru.vsu.cs.emtsevaai.task1.service.impl;

import ru.vsu.cs.emtsevaai.task1.entity.Trainer;
import ru.vsu.cs.emtsevaai.task1.ex.EntityNotFoundException;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.AbstractRepository;
import ru.vsu.cs.emtsevaai.task1.service.TrainerService;
import ru.vsu.cs.emtsevaai.task1.util.StringUtils;

import java.util.List;
import java.util.UUID;

public class TrainerServiceImpl implements TrainerService {

    private final AbstractRepository<Trainer> trainerRepository;

    public TrainerServiceImpl(AbstractRepository<Trainer> trainerRepository) {
        if (trainerRepository == null) {
            throw new IllegalArgumentException(
                    "Репозиторий тренеров не может быть null"
            );
        }

        this.trainerRepository = trainerRepository;
    }

    @Override
    public Trainer createTrainer(
            String fullName,
            String phone,
            int experienceYears
    ) {
        if (StringUtils.isNullOrBlank(fullName)) {
            throw new ValidationException("Имя тренера не может быть пустым");
        }

        if (StringUtils.isNullOrBlank(phone)) {
            throw new ValidationException("Телефон тренера не может быть пустым");
        }

        if (experienceYears < 0) {
            throw new ValidationException(
                    "Стаж тренера не может быть отрицательным"
            );
        }

        String normalizedPhone = phone.trim();

        boolean phoneExists = trainerRepository.findAll().stream()
                .anyMatch(trainer ->
                        trainer.getPhone() != null
                                && trainer.getPhone().trim()
                                .equals(normalizedPhone)
                );

        if (phoneExists) {
            throw new ValidationException(
                    "Тренер с таким телефоном уже существует"
            );
        }

        Trainer trainer = new Trainer(
                fullName.trim(),
                normalizedPhone,
                experienceYears
        );

        return trainerRepository.save(trainer);
    }

    @Override
    public Trainer getTrainer(UUID id) {
        if (id == null) {
            throw new ValidationException("ID тренера не может быть null");
        }

        return trainerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Тренер с ID " + id + " не найден"
                ));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }
}