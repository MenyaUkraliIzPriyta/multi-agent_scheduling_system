package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Entity.*;
import org.example.utils.CreateSchedule;

import java.io.File;
import java.io.IOException;

public class App {
//    private static final Map<String, String> TIME_SLOTS = new LinkedHashMap<>() {{
//        put("A", "8:05~9:40 (1,2)");
//        put("P", "10:00~12:25 (3,4,5)");
//        put("F", "13:30~15:05 (6,7)");
//        put("G", "15:15~16:50 (8,9)");
//        put("E", "18:30~20:55 (10,11,12)");
//    }};

//    private static final List<String> CLASSROOMS = List.of(
//            "#106 (209 seats)", "#108 (209 seats)", "#201 (56 seats)", "#203 (56 seats)",
//            "#301 (56 seats)", "#303 (56 seats)", "#305 (56 seats)", "#307 (56 seats)"
//    );
    private static final CreateSchedule createSchedule = new CreateSchedule();
    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File("teachers.json");

            if (!jsonFile.exists()) {
                System.err.println("Ошибка: Файл teachers.json не найден!");
                return;
            }

            ScheduleData data = mapper.readValue(jsonFile, ScheduleData.class);
            if (data.getTeachers() == null || data.getTeachers().isEmpty()) {
                System.err.println("Ошибка: Нет данных о преподавателях!");
                return;
            }

            System.out.println("Найдено преподавателей: " + data.getTeachers().size());
            createSchedule.generateSchedule(data.getTeachers(), data.getSemesterStart(), data.getSemesterEnd());
            System.out.println("Расписание успешно сгенерировано в файле university_schedule.xlsx");
        } catch (IOException e) {
            System.err.println("Ошибка при генерации расписания:");
            e.printStackTrace();
        }
    }

//    private static void generateSchedule(List<Teacher> teachers, String startDate, String endDate) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate start = LocalDate.parse(startDate, formatter);
//        LocalDate end = LocalDate.parse(endDate, formatter);
//
//        Map<Teacher, Map<String, int[]>> remainingClasses = initializeRemainingClasses(teachers);
//
//        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
//            if (date.getDayOfWeek().getValue() == 7) continue;
//
//            String sheetName = date.format(DateTimeFormatter.ofPattern("EEE_dd.MM"));
//            Sheet sheet = workbook.createSheet(sheetName);
//            generateDaySchedule(sheet, teachers, date, remainingClasses);
//        }
//
//        try (FileOutputStream out = new FileOutputStream("university_schedule.xlsx")) {
//            workbook.write(out);
//        }
//    }

//    private static Map<Teacher, Map<String, int[]>> initializeRemainingClasses(List<Teacher> teachers) {
//        Map<Teacher, Map<String, int[]>> remainingClasses = new HashMap<>();
//        for (Teacher teacher : teachers) {
//            Map<String, int[]> subjectMap = new HashMap<>();
//            for (String[] subject : teacher.getSubjects()) {
//                subjectMap.put(subject[0], new int[]{
//                        Integer.parseInt(subject[1]),
//                        Integer.parseInt(subject[2])
//                });
//                System.out.printf("Преподаватель %s: %s - %d лекций, %d практик%n",
//                        teacher.getId(), subject[0],
//                        Integer.parseInt(subject[1]),
//                        Integer.parseInt(subject[2]));
//            }
//            remainingClasses.put(teacher, subjectMap);
//        }
//        return remainingClasses;
//    }

//    private static void generateDaySchedule(Sheet sheet, List<Teacher> teachers, LocalDate date,
//                                            Map<Teacher, Map<String, int[]>> remainingClasses) {
//        System.out.println("\nГенерация расписания на: " + date);
//
//        Map<String, Map<String, String>> daySchedule = initializeDaySchedule();
//        List<ClassAssignment> assignments = collectAssignments(teachers, date, remainingClasses);
//        distributeAssignments(daySchedule, assignments, date, remainingClasses);
//        fillExcelSheet(sheet, daySchedule);
//    }

//    private static Map<String, Map<String, String>> initializeDaySchedule() {
//        Map<String, Map<String, String>> daySchedule = new LinkedHashMap<>();
//        TIME_SLOTS.keySet().forEach(slot -> {
//            daySchedule.put(slot, new LinkedHashMap<>());
//            CLASSROOMS.forEach(room -> daySchedule.get(slot).put(room, "Available"));
//        });
//        return daySchedule;
//    }

//    private static List<ClassAssignment> collectAssignments(List<Teacher> teachers, LocalDate date,
//                                                            Map<Teacher, Map<String, int[]>> remainingClasses) {
//        List<ClassAssignment> assignments = new ArrayList<>();
//        for (Teacher teacher : teachers) {
//            if (!isTeacherAvailable(teacher, date)) {
//                System.out.println("Преподаватель " + teacher.getId() + " недоступен " + date);
//                continue;
//            }
//
//            Map<String, int[]> teacherSubjects = remainingClasses.get(teacher);
//            for (Map.Entry<String, int[]> entry : teacherSubjects.entrySet()) {
//                String subject = entry.getKey();
//                int lecturesLeft = entry.getValue()[0];
//                int practicesLeft = entry.getValue()[1];
//
//                long currentLectures = countCurrentAssignments(assignments, teacher, "Lect");
//                long currentPractices = countCurrentAssignments(assignments, teacher, "Pract");
//
//                if (lecturesLeft > 0 && currentLectures < teacher.getPreferences().getSchedule().getMaxLecturesPerDay()) {
//                    assignments.add(createAssignment(teacher, subject, "Lect"));
//                }
//                if (practicesLeft > 0 && currentPractices < teacher.getPreferences().getSchedule().getMaxPracticePerDay()) {
//                    assignments.add(createAssignment(teacher, subject, "Pract"));
//                }
//            }
//        }
//        return assignments;
//    }

//    private static ClassAssignment createAssignment(Teacher teacher, String subject, String type) {
//        return new ClassAssignment(
//                teacher,
//                subject,
//                type,
//                teacher.getPreferences().getSchedule().getPreferredTime(),
//                type.equals("Lect")
//                        ? teacher.getPreferences().getClassrooms().getLecture().getFixedRoom() != null
//                        : teacher.getPreferences().getClassrooms().getPractice().getFixedRoom() != null,
//                true
//        );
//    }

//    private static long countCurrentAssignments(List<ClassAssignment> assignments, Teacher teacher, String type) {
//        return assignments.stream()
//                .filter(a -> a.teacher.equals(teacher) && a.type.equals(type))
//                .count();
//    }

//    private static void distributeAssignments(Map<String, Map<String, String>> daySchedule,
//                                              List<ClassAssignment> assignments,
//                                              LocalDate date,
//                                              Map<Teacher, Map<String, int[]>> remainingClasses) {
//        assignments.sort(Comparator
//                .comparing((ClassAssignment a) -> !a.hasFixedRoom)
//                .thenComparing(a -> a.teacher.getId())
//        );
//
//        for (ClassAssignment assignment : assignments) {
//            if (assignClassToSchedule(daySchedule, assignment, date)) {
//                updateRemainingClasses(remainingClasses, assignment);
//                System.out.printf("Распределено: %s - %s (%s) на %s%n",
//                        assignment.teacher.getId(),
//                        assignment.subject,
//                        assignment.type,
//                        date);
//            }
//        }
//    }

//    private static boolean assignClassToSchedule(Map<String, Map<String, String>> daySchedule,
//                                                 ClassAssignment assignment,
//                                                 LocalDate date) {
//        List<String> possibleSlots = getPossibleSlots(assignment);
//        possibleSlots.sort(Comparator.comparing(slot -> !isSlotPreferred(assignment, slot)));
//
//        for (String slot : possibleSlots) {
//            if (!isTeacherAvailable(assignment.teacher, date, slot)) {
//                continue;
//            }
//            if (isTeacherAlreadyAssigned(daySchedule, assignment.teacher, slot)) {
//                continue;
//            }
//
//            String fixedRoom = getFixedRoom(assignment);
//            if (tryAssignToFixedRoom(daySchedule, assignment, slot, fixedRoom)) {
//                return true;
//            }
//
//            if (tryAssignToAnyRoom(daySchedule, assignment, slot)) {
//                return true;
//            }
//        }
//
//        if (!assignment.type.equals("Lect")) {
//            return tryAlternativeSlots(daySchedule, assignment, date, possibleSlots);
//        }
//
//        return false;
//    }

//    private static List<String> getPossibleSlots(ClassAssignment assignment) {
//        return new ArrayList<>(assignment.type.equals("Lect")
//                ? List.of("P")
//                : List.of("F", "G", "E"));
//    }

//    private static String getFixedRoom(ClassAssignment assignment) {
//        return assignment.type.equals("Lect")
//                ? assignment.teacher.getPreferences().getClassrooms().getLecture().getFixedRoom()
//                : assignment.teacher.getPreferences().getClassrooms().getPractice().getFixedRoom();
//    }

//    private static boolean tryAssignToFixedRoom(Map<String, Map<String, String>> daySchedule,
//                                                ClassAssignment assignment,
//                                                String slot,
//                                                String fixedRoom) {
//        if (fixedRoom != null && CLASSROOMS.contains(fixedRoom)) {
//            if ("Available".equals(daySchedule.get(slot).get(fixedRoom))) {
//                daySchedule.get(slot).put(fixedRoom,
//                        formatClass(assignment.subject, assignment.type, assignment.teacher.getFullName()));
//                return true;
//            }
//        }
//        return false;
//    }

//    private static boolean tryAssignToAnyRoom(Map<String, Map<String, String>> daySchedule,
//                                              ClassAssignment assignment,
//                                              String slot) {
//        for (String room : CLASSROOMS) {
//            if ("Available".equals(daySchedule.get(slot).get(room))) {
//                daySchedule.get(slot).put(room,
//                        formatClass(assignment.subject, assignment.type, assignment.teacher.getFullName()));
//                return true;
//            }
//        }
//        return false;
//    }

//    private static boolean tryAlternativeSlots(Map<String, Map<String, String>> daySchedule,
//                                               ClassAssignment assignment,
//                                               LocalDate date,
//                                               List<String> possibleSlots) {
//        List<String> alternativeSlots = new ArrayList<>(TIME_SLOTS.keySet());
//        alternativeSlots.removeAll(possibleSlots);
//
//        for (String slot : alternativeSlots) {
//            if (!isTeacherAvailable(assignment.teacher, date, slot)) continue;
//            if (isTeacherAlreadyAssigned(daySchedule, assignment.teacher, slot)) continue;
//
//            for (String room : CLASSROOMS) {
//                if ("Available".equals(daySchedule.get(slot).get(room))) {
//                    daySchedule.get(slot).put(room,
//                            formatClass(assignment.subject, assignment.type, assignment.teacher.getFullName()));
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    private static void updateRemainingClasses(Map<Teacher, Map<String, int[]>> remainingClasses,
//                                               ClassAssignment assignment) {
//        Map<String, int[]> teacherSubjects = remainingClasses.get(assignment.teacher);
//        int[] counts = teacherSubjects.get(assignment.subject);
//        if (assignment.type.equals("Lect")) {
//            counts[0]--;
//        } else {
//            counts[1]--;
//        }
//    }

//    private static void fillExcelSheet(Sheet sheet, Map<String, Map<String, String>> daySchedule) {
//        // Создаем стили
//        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
//        CellStyle timeStyle = createTimeSlotStyle(sheet.getWorkbook());
//        CellStyle lectureStyle = createLectureStyle(sheet.getWorkbook());
//        CellStyle practiceStyle = createPracticeStyle(sheet.getWorkbook());
//        CellStyle availableStyle = createAvailableStyle(sheet.getWorkbook());
//
//        // Настраиваем размеры
//        sheet.setDefaultRowHeightInPoints(40);
//        sheet.setColumnWidth(0, 20 * 256);
//        for (int i = 1; i <= CLASSROOMS.size(); i++) {
//            sheet.setColumnWidth(i, 25 * 256);
//        }
//
//        // Заголовок
//        Row headerRow = sheet.createRow(0);
//        headerRow.setHeightInPoints(50);
//        Cell headerCell = headerRow.createCell(0);
//        headerCell.setCellValue("Period of time");
//        headerCell.setCellStyle(headerStyle);
//
//        for (int i = 0; i < CLASSROOMS.size(); i++) {
//            Cell cell = headerRow.createCell(i + 1);
//            cell.setCellValue(CLASSROOMS.get(i));
//            cell.setCellStyle(headerStyle);
//        }
//
//        // Данные
//        int rowNum = 1;
//        for (Map.Entry<String, Map<String, String>> slotEntry : daySchedule.entrySet()) {
//            Row row = sheet.createRow(rowNum++);
//
//            // Временной слот
//            Cell timeCell = row.createCell(0);
//            timeCell.setCellValue(slotEntry.getKey() + ". " + TIME_SLOTS.get(slotEntry.getKey()));
//            timeCell.setCellStyle(timeStyle);
//
//            // Аудитории
//            for (int i = 0; i < CLASSROOMS.size(); i++) {
//                Cell cell = row.createCell(i + 1);
//                String value = slotEntry.getValue().get(CLASSROOMS.get(i));
//                cell.setCellValue(value != null ? value : "Available");
//
//                if ("Available".equals(value)) {
//                    cell.setCellStyle(availableStyle);
//                } else if (value.contains("(Lect)")) {
//                    cell.setCellStyle(lectureStyle);
//                } else if (value.contains("(Pract)")) {
//                    cell.setCellStyle(practiceStyle);
//                }
//            }
//        }
//    }

//    private static CellStyle createHeaderStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setBold(true);
//        font.setFontHeightInPoints((short) 14);
//        style.setFont(font);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setBorderTop(BorderStyle.MEDIUM);
//        style.setBorderBottom(BorderStyle.MEDIUM);
//        style.setBorderLeft(BorderStyle.MEDIUM);
//        style.setBorderRight(BorderStyle.MEDIUM);
//        style.setWrapText(true);
//        return style;
//    }

//    private static CellStyle createTimeSlotStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setBold(true);
//        font.setFontHeightInPoints((short) 12);
//        style.setFont(font);
//        style.setAlignment(HorizontalAlignment.LEFT);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setWrapText(true);
//        return style;
//    }

//    private static CellStyle createLectureStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setFontHeightInPoints((short) 12);
//        style.setFont(font);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setWrapText(true);
//        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setBorderTop(BorderStyle.THIN);
//        style.setBorderBottom(BorderStyle.THIN);
//        style.setBorderLeft(BorderStyle.THIN);
//        style.setBorderRight(BorderStyle.THIN);
//        return style;
//    }

//    private static CellStyle createPracticeStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setFontHeightInPoints((short) 12);
//        style.setFont(font);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setWrapText(true);
//        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setBorderTop(BorderStyle.THIN);
//        style.setBorderBottom(BorderStyle.THIN);
//        style.setBorderLeft(BorderStyle.THIN);
//        style.setBorderRight(BorderStyle.THIN);
//        return style;
//    }

//    private static CellStyle createAvailableStyle(Workbook workbook) {
//        CellStyle style = workbook.createCellStyle();
//        style.setAlignment(HorizontalAlignment.CENTER);
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setWrapText(true);
//        return style;
//    }

//    private static boolean isSlotPreferred(ClassAssignment assignment, String slot) {
//        if (assignment.preferredTimes == null || assignment.preferredTimes.isEmpty()) {
//            return false;
//        }
//        String slotTime = TIME_SLOTS.get(slot).split("~")[0].trim();
//        return assignment.preferredTimes.stream()
//                .anyMatch(pref -> pref.startsWith(slotTime));
//    }

//    private static boolean isTeacherAlreadyAssigned(Map<String, Map<String, String>> daySchedule,
//                                                    Teacher teacher,
//                                                    String slot) {
//        return daySchedule.get(slot).values().stream()
//                .anyMatch(v -> v.contains(teacher.getFullName()));
//    }

//    private static boolean isTeacherAvailable(Teacher teacher, LocalDate date) {
//        if (teacher.getUnavailable().getDates().contains(date.format(DateTimeFormatter.ISO_DATE))) {
//            return false;
//        }
//        return !teacher.getUnavailable().getWeekdays().contains(date.getDayOfWeek().getValue());
//    }
//
//    private static boolean isTeacherAvailable(Teacher teacher, LocalDate date, String slot) {
//        if (!isTeacherAvailable(teacher, date)) return false;
//
//        String slotTime = TIME_SLOTS.get(slot).split("~")[0].trim();
//        return teacher.getUnavailable().getTimeRanges().stream()
//                .noneMatch(bannedTime -> bannedTime.startsWith(slotTime));
//    }

//    private static String formatClass(String subject, String type, String teacher) {
//        return String.format("%s (%s) %s", subject, type, teacher);
//    }

//    static class ClassAssignment {
//        Teacher teacher;
//        String subject;
//        String type;
//        List<String> preferredTimes;
//        boolean hasFixedRoom;
//        boolean isNewAssignment;
//
//        ClassAssignment(Teacher teacher, String subject, String type,
//                        List<String> preferredTimes, boolean hasFixedRoom, boolean isNewAssignment) {
//            this.teacher = teacher;
//            this.subject = subject;
//            this.type = type;
//            this.preferredTimes = preferredTimes;
//            this.hasFixedRoom = hasFixedRoom;
//            this.isNewAssignment = isNewAssignment;
//        }
//    }
}