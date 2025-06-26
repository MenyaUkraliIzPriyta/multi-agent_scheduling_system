package org.example.Entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Teacher {
    private String id;

    @JsonProperty("full_name")
    private String fullName;

    private String contact;

    private List<String[]> subjects; // ["ML-101", 16, 7]

    private Unavailable unavailable;

    private Preferences preferences;
}