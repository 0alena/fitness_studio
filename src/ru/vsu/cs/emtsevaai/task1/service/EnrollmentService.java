package ru.vsu.cs.emtsevaai.task1.service;

import ru.vsu.cs.emtsevaai.task1.entity.Enrollment;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {

    Enrollment createEnrollment(UUID visitorId, UUID sessionId);

    Enrollment getEnrollment(UUID id);

    List<Enrollment> getAllEnrollments();

    List<Enrollment> getEnrollmentsByVisitor(UUID visitorId);

    void cancelEnrollment(UUID id);

    void markAttended(UUID id);

    void markMissed(UUID id);
}