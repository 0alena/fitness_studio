package ru.vsu.cs.emtsevaai.task1.entity;

import ru.vsu.cs.emtsevaai.task1.entity.enums.EnrollmentStatus;

public class Enrollment extends AbstractEntity {
    private Visitor visitor;
    private TrainingSession session;
    private EnrollmentStatus status;

    public Enrollment(Visitor visitor, TrainingSession session) {
        this.visitor = visitor;
        this.session = session;
        this.status = EnrollmentStatus.BOOKED;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public TrainingSession getSession() {
        return session;
    }

    public void setSession(TrainingSession session) {
        this.session = session;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }
}