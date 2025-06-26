package org.example.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.example.Entity.Teacher;

import java.time.LocalDate;
import java.util.*;

import static org.example.App.*;

public class InitializationSchedule {
    public static final Map<String, String> TIME_SLOTS = new LinkedHashMap<>() {{
        put("A", "8:05~9:40 (1,2)");
        put("P", "10:00~12:25 (3,4,5)");
        put("F", "13:30~15:05 (6,7)");
        put("G", "15:15~16:50 (8,9)");
        put("E", "18:30~20:55 (10,11,12)");
    }};

    public static final List<String> CLASSROOMS = List.of(
            "#106 (209 seats)", "#108 (209 seats)", "#201 (56 seats)", "#203 (56 seats)",
            "#301 (56 seats)", "#303 (56 seats)", "#305 (56 seats)", "#307 (56 seats)"
    );

    private final Styles styles = new Styles();
    private final Checks checks = new Checks();
    private final Assignments assignments = new Assignments(checks);

    public Map<String, Map<String, String>> initializeDaySchedule() {
        Map<String, Map<String, String>> daySchedule = new LinkedHashMap<>();
        TIME_SLOTS.keySet().forEach(slot -> {
            daySchedule.put(slot, new LinkedHashMap<>());
            CLASSROOMS.forEach(room -> daySchedule.get(slot).put(room, "Available"));
        });
        return daySchedule;
    }

    public List<ClassAssignment> collectAssignments(List<Teacher> teachers, LocalDate date,
                                                                Map<Teacher, Map<String, int[]>> remainingClasses) {
        List<ClassAssignment> assignments = new ArrayList<>();
        for (Teacher teacher : teachers) {
            if (!this.checks.isTeacherAvailable_(teacher, date)) {
                System.out.println("Преподаватель " + teacher.getId() + " недоступен " + date);
                continue;
            }

            Map<String, int[]> teacherSubjects = remainingClasses.get(teacher);
            for (Map.Entry<String, int[]> entry : teacherSubjects.entrySet()) {
                String subject = entry.getKey();
                int lecturesLeft = entry.getValue()[0];
                int practicesLeft = entry.getValue()[1];

                long currentLectures = this.assignments.countCurrentAssignments(assignments, teacher, "Lect");
                long currentPractices = this.assignments.countCurrentAssignments(assignments, teacher, "Pract");

                if (lecturesLeft > 0 && currentLectures < teacher.getPreferences().getSchedule().getMaxLecturesPerDay()) {
                    assignments.add(this.assignments.createAssignment(teacher, subject, "Lect"));
                }
                if (practicesLeft > 0 && currentPractices < teacher.getPreferences().getSchedule().getMaxPracticePerDay()) {
                    assignments.add(this.assignments.createAssignment(teacher, subject, "Pract"));
                }
            }
        }
        return assignments;
    }

    public void distributeAssignments(Map<String, Map<String, String>> daySchedule,
                                              List<ClassAssignment> assignments,
                                              LocalDate date,
                                              Map<Teacher, Map<String, int[]>> remainingClasses) {
        assignments.sort(Comparator
                .comparing((ClassAssignment a) -> !a.hasFixedRoom)
                .thenComparing(a -> a.teacher.getId())
        );

        for (ClassAssignment assignment : assignments) {
            if (this.assignments.assignClassToSchedule(daySchedule, assignment, date)) {
                this.assignments.updateRemainingClasses(remainingClasses, assignment);
                System.out.printf("Распределено: %s - %s (%s) на %s%n",
                        assignment.teacher.getId(),
                        assignment.subject,
                        assignment.type,
                        date);
            }
        }
    }

    public void fillExcelSheet(Sheet sheet, Map<String, Map<String, String>> daySchedule) {
        // Создаем стили
        CellStyle headerStyle = this.styles.createHeaderStyle(sheet.getWorkbook());
        CellStyle timeStyle = this.styles.createTimeSlotStyle(sheet.getWorkbook());
        CellStyle lectureStyle = this.styles.createLectureStyle(sheet.getWorkbook());
        CellStyle practiceStyle = this.styles.createPracticeStyle(sheet.getWorkbook());
        CellStyle availableStyle = this.styles.createAvailableStyle(sheet.getWorkbook());

        // Настраиваем размеры
        sheet.setDefaultRowHeightInPoints(40);
        sheet.setColumnWidth(0, 20 * 256);
        for (int i = 1; i <= CLASSROOMS.size(); i++) {
            sheet.setColumnWidth(i, 25 * 256);
        }

        // Заголовок
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(50);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Period of time");
        headerCell.setCellStyle(headerStyle);

        for (int i = 0; i < CLASSROOMS.size(); i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(CLASSROOMS.get(i));
            cell.setCellStyle(headerStyle);
        }

        // Данные
        int rowNum = 1;
        for (Map.Entry<String, Map<String, String>> slotEntry : daySchedule.entrySet()) {
            Row row = sheet.createRow(rowNum++);

            // Временной слот
            Cell timeCell = row.createCell(0);
            timeCell.setCellValue(slotEntry.getKey() + ". " + TIME_SLOTS.get(slotEntry.getKey()));
            timeCell.setCellStyle(timeStyle);

            // Аудитории
            for (int i = 0; i < CLASSROOMS.size(); i++) {
                Cell cell = row.createCell(i + 1);
                String value = slotEntry.getValue().get(CLASSROOMS.get(i));
                cell.setCellValue(value != null ? value : "Available");

                if ("Available".equals(value)) {
                    cell.setCellStyle(availableStyle);
                } else if (value.contains("(Lect)")) {
                    cell.setCellStyle(lectureStyle);
                } else if (value.contains("(Pract)")) {
                    cell.setCellStyle(practiceStyle);
                }
            }
        }
    }
}
