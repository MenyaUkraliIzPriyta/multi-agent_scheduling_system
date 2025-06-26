package org.example.utils;

import org.example.Entity.Teacher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.example.utils.InitializationSchedule.CLASSROOMS;

public class Assignments {
    private final Checks checks;

    public Assignments(Checks checks) {
        this.checks = checks;
    }

    public long countCurrentAssignments(List<ClassAssignment> assignments, Teacher teacher, String type) {
        return assignments.stream()
                .filter(a -> a.teacher.equals(teacher) && a.type.equals(type))
                .count();
    }

    public ClassAssignment createAssignment(Teacher teacher, String subject, String type) {
        return new ClassAssignment(
                teacher,
                subject,
                type,
                teacher.getPreferences().getSchedule().getPreferredTime(),
                type.equals("Lect")
                        ? teacher.getPreferences().getClassrooms().getLecture().getFixedRoom() != null
                        : teacher.getPreferences().getClassrooms().getPractice().getFixedRoom() != null,
                true
        );
    }

    public boolean assignClassToSchedule(Map<String, Map<String, String>> daySchedule,
                                         ClassAssignment assignment,
                                         LocalDate date) {
        List<String> possibleSlots = getPossibleSlots(assignment);
        possibleSlots.sort(Comparator.comparing(slot -> !checks.isSlotPreferred(assignment, slot)));

        for (String slot : possibleSlots) {
            if (!checks.isTeacherAvailable(assignment.teacher, date, slot)) {
                continue;
            }
            if (checks.isTeacherAlreadyAssigned(daySchedule, assignment.teacher, slot)) {
                continue;
            }

            String fixedRoom = getFixedRoom(assignment);
            if (tryAssignToFixedRoom(daySchedule, assignment, slot, fixedRoom)) {
                return true;
            }

            if (checks.tryAssignToAnyRoom(daySchedule, assignment, slot)) {
                return true;
            }
        }

        if (!assignment.type.equals("Lect")) {
            return checks.tryAlternativeSlots(daySchedule, assignment, date, possibleSlots);
        }

        return false;
    }

    public void updateRemainingClasses(Map<Teacher, Map<String, int[]>> remainingClasses,
                                       ClassAssignment assignment) {
        Map<String, int[]> teacherSubjects = remainingClasses.get(assignment.teacher);
        int[] counts = teacherSubjects.get(assignment.subject);
        if (assignment.type.equals("Lect")) {
            counts[0]--;
        } else {
            counts[1]--;
        }
    }

    private List<String> getPossibleSlots(ClassAssignment assignment) {
        return new ArrayList<>(assignment.type.equals("Lect")
                ? List.of("P")
                : List.of("F", "G", "E"));
    }

    private String getFixedRoom(ClassAssignment assignment) {
        return assignment.type.equals("Lect")
                ? assignment.teacher.getPreferences().getClassrooms().getLecture().getFixedRoom()
                : assignment.teacher.getPreferences().getClassrooms().getPractice().getFixedRoom();
    }

    public boolean tryAssignToFixedRoom(Map<String, Map<String, String>> daySchedule,
                                        ClassAssignment assignment,
                                        String slot,
                                        String fixedRoom) {
        if (fixedRoom != null && CLASSROOMS.contains(fixedRoom)) {
            if ("Available".equals(daySchedule.get(slot).get(fixedRoom))) {
                daySchedule.get(slot).put(fixedRoom,
                        checks.formatClass(assignment.subject, assignment.type, assignment.teacher.getFullName()));
                return true;
            }
        }
        return false;
    }

    public List<ClassAssignment> collectAssignments(List<Teacher> teachers, LocalDate date,
                                                    Map<Teacher, Map<String, int[]>> remainingClasses) {
        List<ClassAssignment> assignments = new ArrayList<>();
        for (Teacher teacher : teachers) {
            if (!checks.isTeacherAvailable_(teacher, date)) {
                System.out.println("Преподаватель " + teacher.getId() + " недоступен " + date);
                continue;
            }

            Map<String, int[]> teacherSubjects = remainingClasses.get(teacher);
            for (Map.Entry<String, int[]> entry : teacherSubjects.entrySet()) {
                String subject = entry.getKey();
                int lecturesLeft = entry.getValue()[0];
                int practicesLeft = entry.getValue()[1];

                long currentLectures = countCurrentAssignments(assignments, teacher, "Lect");
                long currentPractices = countCurrentAssignments(assignments, teacher, "Pract");

                if (lecturesLeft > 0 && currentLectures < teacher.getPreferences().getSchedule().getMaxLecturesPerDay()) {
                    assignments.add(createAssignment(teacher, subject, "Lect"));
                }
                if (practicesLeft > 0 && currentPractices < teacher.getPreferences().getSchedule().getMaxPracticePerDay()) {
                    assignments.add(createAssignment(teacher, subject, "Pract"));
                }
            }
        }
        return assignments;
    }
}