package org.example.utils;

import org.example.Entity.Teacher;

import java.util.List;

public class ClassAssignment {
    Teacher teacher;
    String subject;
    String type;
    List<String> preferredTimes;
    boolean hasFixedRoom;
    boolean isNewAssignment;

    public ClassAssignment(Teacher teacher, String subject, String type,
                    List<String> preferredTimes, boolean hasFixedRoom, boolean isNewAssignment) {
        this.teacher = teacher;
        this.subject = subject;
        this.type = type;
        this.preferredTimes = preferredTimes;
        this.hasFixedRoom = hasFixedRoom;
        this.isNewAssignment = isNewAssignment;
    }
}