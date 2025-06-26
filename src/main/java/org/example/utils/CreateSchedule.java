package org.example.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.Entity.Teacher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class CreateSchedule {

    private InitializationSchedule initializationSchedule = new InitializationSchedule();

    public void generateSchedule(List<Teacher> teachers, String startDate, String endDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        Map<Teacher, Map<String, int[]>> remainingClasses = initializeRemainingClasses(teachers);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (date.getDayOfWeek().getValue() == 7) continue;

            String sheetName = date.format(DateTimeFormatter.ofPattern("EEE_dd.MM"));
            Sheet sheet = workbook.createSheet(sheetName);
            generateDaySchedule(sheet, teachers, date, remainingClasses);
        }

        try (FileOutputStream out = new FileOutputStream("university_schedule.xlsx")) {
            workbook.write(out);
        }
    }

    private static Map<Teacher, Map<String, int[]>> initializeRemainingClasses(List<Teacher> teachers) {
        Map<Teacher, Map<String, int[]>> remainingClasses = new HashMap<>();
        for (Teacher teacher : teachers) {
            Map<String, int[]> subjectMap = new HashMap<>();
            for (String[] subject : teacher.getSubjects()) {
                subjectMap.put(subject[0], new int[]{
                        Integer.parseInt(subject[1]),
                        Integer.parseInt(subject[2])
                });
                System.out.printf("Преподаватель %s: %s - %d лекций, %d практик%n",
                        teacher.getId(), subject[0],
                        Integer.parseInt(subject[1]),
                        Integer.parseInt(subject[2]));
            }
            remainingClasses.put(teacher, subjectMap);
        }
        return remainingClasses;
    }
    public void generateDaySchedule(Sheet sheet, List<Teacher> teachers, LocalDate date,
                                            Map<Teacher, Map<String, int[]>> remainingClasses) {
        System.out.println("\nГенерация расписания на: " + date);

        Map<String, Map<String, String>> daySchedule = initializationSchedule.initializeDaySchedule();
        List<ClassAssignment> assignments = initializationSchedule.collectAssignments(teachers, date, remainingClasses);
        initializationSchedule.distributeAssignments(daySchedule, assignments, date, remainingClasses);
        initializationSchedule.fillExcelSheet(sheet, daySchedule);
    }
}
