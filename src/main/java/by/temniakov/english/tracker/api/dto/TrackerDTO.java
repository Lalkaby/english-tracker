package by.temniakov.english.tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackerDTO {

    @NonNull
    private Long id;

    @NonNull
    private String name;

    private Long ordinal;

    private Duration duration;

    @JsonProperty("left_task_state_id")
    private Long leftTrackerId;

    @JsonProperty("right_task_state_id")
    private Long rightTrackerId;

    @NonNull
    @JsonProperty("created_at")
    private Instant createdAt;

    @NonNull
    private List<CardDTO> cards;
}
