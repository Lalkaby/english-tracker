package by.temniakov.english.tracker.api.controllers.converters;

import java.time.Duration;
import org.springframework.core.convert.converter.Converter;

public class StringToDurationConverter implements Converter<String, Duration> {

    @Override
    public Duration convert(String source) {
        // Преобразование строки в Duration
        return Duration.parse(source);
    }
}