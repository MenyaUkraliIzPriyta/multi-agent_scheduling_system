package org.example.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassroomPreferences {
    private RoomRequirements lecture;
    private RoomRequirements practice;
}
