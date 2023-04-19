package by.temniakov.english.tracker.api.services;

import by.temniakov.english.tracker.api.exceptions.TranslationServiceException;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TranslationService {
    private static final String TRANSLATIONS_URL = "https://context.reverso.net/translation/english-russian/";
    private static final String TRANSLATIONS_SELECTOR = "div#translations-content *.translation.ltr.dict *.display-term";
    private static final String EXAMPLES_SELECTOR = "section#examples-content div.example";
    private static final String SOURCE_SELECTOR = "div.src.ltr";
    private static final String TARGET_SELECTOR = "div.trg.ltr";


    @Async
    public CompletableFuture<List<String>> getTranslationsAsync(String phrase) throws TranslationServiceException {
        return CompletableFuture.supplyAsync(() -> {
            String url = TRANSLATIONS_URL + phrase;
            try {
                Document doc = Jsoup.connect(url).get();
                Elements translations = doc.select(TRANSLATIONS_SELECTOR);
                return translations.stream()
                        .map(Element::text)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Translation service error" , e);
                throw new TranslationServiceException(String.format("Failed to get translations for phrase: %s",phrase));
            }
        });
    }

    @Async
    public CompletableFuture<List<Map.Entry<String, String>>> getExamplesAsync(String phrase) throws TranslationServiceException {
        return CompletableFuture.supplyAsync(() -> {
            String url = TRANSLATIONS_URL + phrase;
            try {
                Document doc = Jsoup.connect(url).get();
                Elements examplesDiv = doc.select(EXAMPLES_SELECTOR);
                return examplesDiv.stream().map(example -> new AbstractMap.SimpleEntry<>(
                        example.select(SOURCE_SELECTOR).text(),
                        example.select(TARGET_SELECTOR).text()
                )).collect(Collectors.toList());
            } catch (IOException e) {
                log.error("Translation service error", e);
                throw new TranslationServiceException(String.format("Failed to get examples for phrase: %s", phrase));
            }
        });
    }

    public List<String> getTranslations2(String phrase) throws TranslationServiceException {
        String url = TRANSLATIONS_URL + phrase;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements translations = doc.select(TRANSLATIONS_SELECTOR);
            return translations.stream()
                    .map(Element::text)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Translation service error", e);
            throw new TranslationServiceException("Failed to get translations for phrase: " + phrase);
        }
    }

    public List<Map.Entry<String, String>> getExamples2(String phrase) throws TranslationServiceException {
        String url = TRANSLATIONS_URL + phrase;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements examplesDiv = doc.select(EXAMPLES_SELECTOR);
            return examplesDiv.stream().map(example -> new AbstractMap.SimpleEntry<>(
                    example.select(SOURCE_SELECTOR).text(),
                    example.select(TARGET_SELECTOR).text()
            )).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Translation service error", e);
            throw new TranslationServiceException(String.format("Failed to get examples for phrase: %s.", phrase));
        }
    }

}