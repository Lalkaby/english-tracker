package by.temniakov.english.tracker.api.services;

import by.temniakov.english.tracker.store.entities.CardEntity;
import by.temniakov.english.tracker.store.repositories.CardRepository;
import org.springframework.stereotype.Service;

@Service
public class CardService extends BaseRepositoryService<CardEntity>{
    public CardService(CardRepository cardRepository) {
        super(cardRepository);
    }
}
