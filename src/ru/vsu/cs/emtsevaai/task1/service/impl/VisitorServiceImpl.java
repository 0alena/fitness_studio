package ru.vsu.cs.emtsevaai.task1.service.impl;

import ru.vsu.cs.emtsevaai.task1.entity.Visitor;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.ex.EntityNotFoundException;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.AbstractRepository;
import ru.vsu.cs.emtsevaai.task1.service.VisitorService;
import ru.vsu.cs.emtsevaai.task1.util.StringUtils;

import java.util.List;
import java.util.UUID;

public class VisitorServiceImpl implements VisitorService {

    private final AbstractRepository<Visitor> visitorRepository;

    public VisitorServiceImpl(AbstractRepository<Visitor> visitorRepository) {
        if (visitorRepository == null) {
            throw new IllegalArgumentException(
                    "Репозиторий посетителей не может быть null"
            );
        }

        this.visitorRepository = visitorRepository;
    }

    @Override
    public Visitor createVisitor(
            String fullName,
            String phone,
            TrainingLevel level
    ) {
        validateVisitor(fullName, phone, level);

        String normalizedPhone = phone.trim();

        boolean phoneExists = visitorRepository.findAll().stream()
                .anyMatch(visitor ->
                        visitor.getPhone() != null
                                && visitor.getPhone().trim()
                                .equals(normalizedPhone)
                );

        if (phoneExists) {
            throw new ValidationException(
                    "Посетитель с таким телефоном уже существует"
            );
        }

        Visitor visitor = new Visitor(
                fullName.trim(),
                normalizedPhone,
                level
        );

        return visitorRepository.save(visitor);
    }

    @Override
    public Visitor getVisitor(UUID id) {
        if (id == null) {
            throw new ValidationException("ID посетителя не может быть null");
        }

        return visitorRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Посетитель с ID " + id + " не найден"
                        )
                );
    }

    @Override
    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    @Override
    public void deleteVisitor(UUID id) {
        if (id == null) {
            throw new ValidationException("ID посетителя не может быть null");
        }

        boolean deleted = visitorRepository.deleteById(id);

        if (!deleted) {
            throw new EntityNotFoundException(
                    "Посетитель с ID " + id + " не найден"
            );
        }
    }

    private void validateVisitor(
            String fullName,
            String phone,
            TrainingLevel level
    ) {
        if (StringUtils.isNullOrBlank(fullName)) {
            throw new ValidationException(
                    "Имя посетителя не может быть пустым"
            );
        }

        if (StringUtils.isNullOrBlank(phone)) {
            throw new ValidationException(
                    "Телефон посетителя не может быть пустым"
            );
        }

        if (level == null) {
            throw new ValidationException(
                    "Уровень подготовки не может быть пустым"
            );
        }
    }
}