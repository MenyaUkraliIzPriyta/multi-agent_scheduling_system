package org.example.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SchedulePreferences {
    @JsonProperty("max_lectures_per_day")
    private int maxLecturesPerDay;
    @JsonProperty("max_practice_per_day")
    private int maxPracticePerDay;
    @JsonProperty("min_break_minutes")
    private int minBreakMinutes;
    @JsonProperty("preferred_time")
    private List<String> preferredTime;
}
