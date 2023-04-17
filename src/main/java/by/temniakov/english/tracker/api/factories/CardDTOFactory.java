package by.temniakov.english.tracker.api.factories;

import by.temniakov.english.tracker.api.dto.CardDTO;
import by.temniakov.english.tracker.store.entities.CardEntity;
import org.springframework.stereotype.Component;

@Component
public class CardDTOFactory {

    public CardDTO mapCardDTO(CardEntity entity){
        return CardDTO.builder()
                .id(entity.getId())
                .phrase(entity.getPhrase())
                .createdAt(entity.getCreatedAt())
                .startedAt(entity.getStartedAt())
                .build();
    }

}
