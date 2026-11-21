package ru.vsu.cs.emtsevaai.task1.entity;

import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;

public class Visitor extends Person {
    private TrainingLevel level;

    public Visitor(String fullName, String phone, TrainingLevel level) {
        super(fullName, phone);
        this.level = level;
    }

    public TrainingLevel getLevel() {
        return level;
    }

    public void setLevel(TrainingLevel level) {
        this.level = level;
    }
}