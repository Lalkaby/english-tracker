package by.temniakov.english.tracker.api.controllers;

import by.temniakov.english.tracker.api.dto.AckDTO;
import by.temniakov.english.tracker.store.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Transactional
@RestController
public class CardController {

    private  CardRepository cardRepository;

    public static final String CREATE_CARD ="/api/card";
    public static final String DELETE_CARD ="/api/card/{card_id}";
    public static final String CHANGE_CARD_POSITION ="/api/card/{card_id}/position";

    @DeleteMapping(DELETE_CARD)
    public AckDTO deleteCard(
            @PathVariable("card_id") Long cardId
    ){
        return null;
    }

}
