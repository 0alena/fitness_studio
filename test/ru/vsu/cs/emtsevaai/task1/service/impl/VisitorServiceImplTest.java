package ru.vsu.cs.emtsevaai.task1.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vsu.cs.emtsevaai.task1.entity.Visitor;
import ru.vsu.cs.emtsevaai.task1.entity.enums.TrainingLevel;
import ru.vsu.cs.emtsevaai.task1.ex.ValidationException;
import ru.vsu.cs.emtsevaai.task1.repository.impl.InMemoryVisitorRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VisitorServiceImplTest {

    private InMemoryVisitorRepository visitorRepository;
    private VisitorServiceImpl visitorService;

    @BeforeEach
    void setUp() {
        visitorRepository = new InMemoryVisitorRepository();
        visitorService = new VisitorServiceImpl(visitorRepository);
    }

    @Test
    void shouldCreateVisitorWithValidData() {
        Visitor visitor = visitorService.createVisitor(
                "Иван Иванов",
                "+79991234567",
                TrainingLevel.BEGINNER
        );

        assertNotNull(visitor);
        assertEquals("Иван Иванов", visitor.getFullName());
        assertEquals("+79991234567", visitor.getPhone());
        assertEquals(TrainingLevel.BEGINNER, visitor.getLevel());
        assertEquals(1, visitorService.getAllVisitors().size());
    }

    @Test
    void shouldRejectBlankVisitorName() {
        assertThrows(
                ValidationException.class,
                () -> visitorService.createVisitor(
                        " ",
                        "+79991234567",
                        TrainingLevel.BEGINNER
                )
        );
    }

    @Test
    void shouldRejectBlankVisitorPhone() {
        assertThrows(
                ValidationException.class,
                () -> visitorService.createVisitor(
                        "Иван Иванов",
                        " ",
                        TrainingLevel.BEGINNER
                )
        );
    }

    @Test
    void shouldRejectMissingTrainingLevel() {
        assertThrows(
                ValidationException.class,
                () -> visitorService.createVisitor(
                        "Иван Иванов",
                        "+79991234567",
                        null
                )
        );
    }

    @Test
    void shouldRejectDuplicatePhone() {
        visitorService.createVisitor(
                "Иван Иванов",
                "+79991234567",
                TrainingLevel.BEGINNER
        );

        assertThrows(
                ValidationException.class,
                () -> visitorService.createVisitor(
                        "Пётр Петров",
                        "+79991234567",
                        TrainingLevel.INTERMEDIATE
                )
        );
    }
}