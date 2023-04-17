package by.temniakov.english.tracker.api.factories;

import by.temniakov.english.tracker.api.dto.TrackerDTO;
import by.temniakov.english.tracker.store.entities.TrackerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TrackerDTOFactory {

    private final CardDTOFactory cardDTOFactory;

    public TrackerDTO mapTrackerDTO(TrackerEntity entity){
        return TrackerDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .leftTrackerId(entity.getLeftTracker().map(TrackerEntity::getId).orElse(null))
                .rightTrackerId(entity.getRightTracker().map(TrackerEntity::getId).orElse(null))
                .ordinal(entity.getOrdinal())
                .duration(entity.getDuration())
                .cards(
                        entity
                                .getCards()
                                .stream()
                                .map(cardDTOFactory::mapCardDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }

}
