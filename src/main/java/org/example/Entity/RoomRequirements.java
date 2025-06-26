package org.example.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomRequirements {
    @JsonProperty("must_have")
    private List<String> mustHave;

    @JsonProperty("nice_to_have")
    private List<String> niceToHave;

    private List<String> blacklist;

    @JsonProperty("fixed_room")
    private String fixedRoom;
}
