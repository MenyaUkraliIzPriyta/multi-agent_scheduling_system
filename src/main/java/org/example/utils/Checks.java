package org.example.utils;

import org.example.Entity.Teacher;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.utils.InitializationSchedule.CLASSROOMS;
import static org.example.utils.InitializationSchedule.TIME_SLOTS;

public class Checks {

    public boolean isSlotPreferred(ClassAssignment assignment, String slot) {
        if (assignment.preferredTimes == null || assignment.preferredTimes.isEmpty()) {
            return false;
        }
        String slotTime = TIME_SLOTS.get(slot).split("~")[0].trim();
        return assignment.preferredTimes.stream()
                .anyMatch(pref -> pref.startsWith(slotTime));
    }

    public boolean isTeacherAvailable_(Teacher teacher, LocalDate date) {
        if (teacher.getUnavailable().getDates().contains(date.format(DateTimeFormatter.ISO_DATE))) {
            return false;
        }
        return !teacher.getUnavailable().getWeekdays().contains(date.getDayOfWeek().getValue());
    }

    public boolean isTeacherAvailable(Teacher teacher, LocalDate date, String slot) {
        if (!isTeacherAvailable_(teacher, date)) return false;

        String slotTime = TIME_SLOTS.get(slot).split("~")[0].trim();
        return teacher.getUnavailable().getTimeRanges().stream()
                .noneMatch(bannedTime -> bannedTime.startsWith(slotTime));
    }

    public boolean isTeacherAlreadyAssigned(Map<String, Map<String, String>> daySchedule,
                                            Teacher teacher, String slot) {
        return daySchedule.get(slot).values().stream()
                .anyMatch(v -> v.contains(teacher.getFullName()));
    }

    public boolean tryAssignToAnyRoom(Map<String, Map<String, String>> daySchedule,
                                      ClassAssignment assignment, String slot) {
        String formattedClass = formatClass(assignment.subject, assignment.type, assignment.teacher.getFullName());
        for (String room : CLASSROOMS) {
            if ("Available".equals(daySchedule.get(slot).get(room))) {
                daySchedule.get(slot).put(room, formattedClass);
                return true;
            }
        }
        return false;
    }

    public boolean tryAlternativeSlots(Map<String, Map<String, String>> daySchedule,
                                       ClassAssignment assignment, LocalDate date,
                                       List<String> possibleSlots) {
        List<String> alternativeSlots = new ArrayList<>(TIME_SLOTS.keySet());
        alternativeSlots.removeAll(possibleSlots);

        String formattedClass = formatClass(assignment.subject, assignment.type, assignment.teacher.getFullName());

        for (String slot : alternativeSlots) {
            if (!isTeacherAvailable(assignment.teacher, date, slot)) continue;
            if (isTeacherAlreadyAssigned(daySchedule, assignment.teacher, slot)) continue;

            for (String room : CLASSROOMS) {
                if ("Available".equals(daySchedule.get(slot).get(room))) {
                    daySchedule.get(slot).put(room, formattedClass);
                    return true;
                }
            }
        }
        return false;
    }

    public String formatClass(String subject, String type, String teacher) {
        return String.format("%s (%s) %s", subject, type, teacher);
    }
}