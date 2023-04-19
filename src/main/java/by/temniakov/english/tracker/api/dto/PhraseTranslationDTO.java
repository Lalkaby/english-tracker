package by.temniakov.english.tracker.api.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhraseTranslationDTO {

    @NonNull
    private String phrase;

    private List<String> translations;

    private List<Map.Entry<String,String>> examples;
}
