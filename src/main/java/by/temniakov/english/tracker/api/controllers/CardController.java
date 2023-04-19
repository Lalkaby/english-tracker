package by.temniakov.english.tracker.api.controllers;

import by.temniakov.english.tracker.api.controllers.helpers.ControllerHelper;
import by.temniakov.english.tracker.api.dto.AckDTO;
import by.temniakov.english.tracker.api.dto.CardDTO;
import by.temniakov.english.tracker.api.exceptions.BadRequestException;
import by.temniakov.english.tracker.api.exceptions.NotFoundException;
import by.temniakov.english.tracker.api.factories.CardDTOFactory;
import by.temniakov.english.tracker.store.entities.CardEntity;
import by.temniakov.english.tracker.store.entities.ProjectEntity;
import by.temniakov.english.tracker.store.entities.TrackerEntity;
import by.temniakov.english.tracker.store.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@RestController
public class CardController {

    private final CardRepository cardRepository;

    private final CardDTOFactory cardDTOFactory;

    private final ControllerHelper controllerHelper;

    public static final String CREATE_CARD ="/api/projects/{project_id}/cards";
    public static final String CHANGE_CARD_POSITION ="/api/cards/{card_id}/position/change";
    public static final String DELETE_CARD ="/api/card/{card_id}";

    @PostMapping(CREATE_CARD)
    public CardDTO createCard(
            @PathVariable("project_id") Long projectId,
            @RequestParam("phrase") String phrase
    ){
        if (phrase.isBlank()){
            throw new BadRequestException("Phrase can't be empty");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        Optional<TrackerEntity> optionalStartTracker = Optional.empty();

        for (TrackerEntity tracker : project.getTrackers()){
            if (tracker.getLeftTracker().isEmpty()){
                optionalStartTracker = Optional.of(tracker);
                break;
            }
        }

        CardEntity card = CardEntity.builder()
                .phrase(phrase)
                .createdAt(Instant.now())
                .startedAt(Instant.now())
                .build();

        optionalStartTracker.ifPresentOrElse(
                card::setTracker,
                () -> {
                    throw new NotFoundException(
                            String.format("No tracker exists in project with \"%s\" id.", projectId)
                    );
                });

        CardEntity savedCard = cardRepository.saveAndFlush(card);

        return cardDTOFactory.mapCardDTO(savedCard);
    }

    @PatchMapping(CHANGE_CARD_POSITION)
    public CardDTO changeCardPosition(
            @PathVariable("card_id") Long cardId,
            @RequestParam("new_tracked_id") Long newTrackerId
    ){
        CardEntity card = controllerHelper.getCardOrThrowException(cardId);

        if (card.getTracker().getId().equals(newTrackerId)){
            return cardDTOFactory.mapCardDTO(card);
        }

        TrackerEntity newTracker = controllerHelper.getTrackerOrThrowException(newTrackerId);

        if (!card.getTracker().getProject().equals(newTracker.getProject().getId())){
            throw  new BadRequestException(
                    String.format(
                            "Tracker with \"%s\" id must be in the same project.",  newTrackerId)
            );
        } else{
            card.setTracker(newTracker);
            card.setStartedAt(Instant.now());
        }

        CardEntity changedCard = cardRepository.saveAndFlush(card);

        return cardDTOFactory.mapCardDTO(changedCard);
    }

    @DeleteMapping(DELETE_CARD)
    public AckDTO deleteCard(
            @PathVariable("card_id") Long cardId
    ){
       controllerHelper.getCardOrThrowException(cardId);

        cardRepository.deleteById(cardId);

        return AckDTO.makeDefault(true);
    }
}
