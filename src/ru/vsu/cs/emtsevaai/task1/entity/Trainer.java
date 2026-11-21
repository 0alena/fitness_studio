package ru.vsu.cs.emtsevaai.task1.entity;

public class Trainer extends Person {
    private int experienceYears;

    public Trainer(String fullName, String phone, int experienceYears) {
        super(fullName, phone);
        this.experienceYears = experienceYears;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
}