package ru.vsu.cs.emtsevaai.task1.service;

import ru.vsu.cs.emtsevaai.task1.entity.Visitor;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;

import java.util.List;
import java.util.UUID;

public interface VisitorService {

    Visitor createVisitor(String fullName, String phone, TrainingLevel level);

    Visitor getVisitor(UUID id);

    List<Visitor> getAllVisitors();

    void deleteVisitor(UUID id);
}