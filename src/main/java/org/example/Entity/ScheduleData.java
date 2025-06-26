package org.example.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Класс-обёртка для всего JSON
@Getter
@Setter
public class ScheduleData {
    private String updated_at;

    private String semesterStart;

    private String semesterEnd;

    private List<Teacher> teachers;
}

// Класс Teacher (уже у вас есть)
