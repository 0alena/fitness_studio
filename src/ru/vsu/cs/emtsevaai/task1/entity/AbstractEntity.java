package ru.vsu.cs.emtsevaai.task1.entity;

import java.util.UUID;

public abstract class AbstractEntity {
    private final UUID id;

    protected AbstractEntity() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
}