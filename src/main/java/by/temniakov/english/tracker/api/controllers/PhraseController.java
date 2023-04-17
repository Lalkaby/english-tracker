package by.temniakov.english.tracker.api.controllers;

import by.temniakov.english.tracker.api.dto.PhraseTranslationDTO;
import by.temniakov.english.tracker.api.services.TranslationService;
import by.temniakov.english.tracker.store.repositories.PhrasalVerbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
@RequiredArgsConstructor
public class PhraseController {

    private final TranslationService translationService;

    private final PhrasalVerbRepository phrasalVerbRepository;

    private static final Integer maxPhrasalVerbResult = 20;

    public static final String GET_PHRASAL_VERBS ="/api/phrases/phrasal-verbs";
    public static final String GET_PHRASE_TRANSLATION ="/api/phrase-translation";
    public static final String GET_PHRASE_TRANSLATION2 ="/api/phrase-translation2";


    @GetMapping(GET_PHRASAL_VERBS)
    public List<String> getPhrasalVerbs(
            @RequestParam(value = "prefix_text", required = false) Optional<String> optionalPrefixText) {

        optionalPrefixText = optionalPrefixText.filter(prefixName -> !prefixName.trim().isEmpty());

        List<String> phrasalVerbs;
        phrasalVerbs = optionalPrefixText
                .map(pt -> phrasalVerbRepository
                        .verbTextStartsWithIgnoreCaseOrderByVerbText(
                                pt,
                                PageRequest.of(0,maxPhrasalVerbResult)
                        )
                )
                .orElseGet(() ->
                        {
                            List<String> result = phrasalVerbRepository
                                    .verbTextOrderByRandom(PageRequest.of(0, maxPhrasalVerbResult));
                            Collections.sort(result);
                            return result;
                        }
                );

        return phrasalVerbs;
    }

    @GetMapping(GET_PHRASE_TRANSLATION)
    public CompletableFuture<PhraseTranslationDTO> getPhraseTranslation(
            @RequestParam(value = "phrase")  String phrase
    ) {
        CompletableFuture<List<String>> translationsFuture =
                translationService.getTranslationsAsync(phrase);
        CompletableFuture<List<Map.Entry<String, String>>> examplesFuture =
                translationService.getExamplesAsync(phrase);

        return translationsFuture.thenCombine(examplesFuture, (translations, examples) ->
                        PhraseTranslationDTO.builder()
                                .phrase(phrase)
                                .translations(translations)
                                .examples(examples)
                                .build())
                .exceptionally(ex -> {
                    log.error("Error while receiving phrase translation:", ex.getMessage());
                    return PhraseTranslationDTO.builder()
                            .phrase(phrase)
                            .build();
                });
    }

    @GetMapping(GET_PHRASE_TRANSLATION2)
    public PhraseTranslationDTO getPhraseTranslation2(
            @RequestParam(value = "phrase") String phrase
    ) {
        List<String> translations;
        List<Map.Entry<String, String>> examples;

        try {
            translations = translationService.getTranslations2(phrase);
        } catch (Exception ex) {
            log.error("Error while receiving phrase translations:", ex.getMessage());
            translations = new ArrayList<>();
        }

        try {
            examples = translationService.getExamples2(phrase);
        } catch (Exception ex) {
            log.error("Error while receiving phrase examples:", ex.getMessage());
            examples = new ArrayList<>();
        }

        return PhraseTranslationDTO.builder()
                .phrase(phrase)
                .translations(translations)
                .examples(examples)
                .build();
    }

}