package org.example.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Preferences {
    private SchedulePreferences schedule;
    private ClassroomPreferences classrooms;
}
