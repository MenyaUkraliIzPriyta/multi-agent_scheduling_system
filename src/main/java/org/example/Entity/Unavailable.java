package org.example.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Unavailable {
    private List<String> dates;
    private List<Integer> weekdays;
    @JsonProperty("time_ranges")
    private List<String> timeRanges;
}
