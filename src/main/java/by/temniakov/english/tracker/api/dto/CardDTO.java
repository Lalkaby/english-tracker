package by.temniakov.english.tracker.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    @NonNull
    private Long id;

    @NonNull
    private String phrase;

    @NonNull
    @JsonProperty("created_at")
    private Instant createdAt;

    @NonNull
    @JsonProperty("started_at")
    private Instant startedAt;
}
