package by.temniakov.english.tracker.api.controllers.helpers;

import by.temniakov.english.tracker.api.exceptions.NotFoundException;
import by.temniakov.english.tracker.store.entities.CardEntity;
import by.temniakov.english.tracker.store.entities.ProjectEntity;
import by.temniakov.english.tracker.store.entities.TrackerEntity;
import by.temniakov.english.tracker.store.repositories.CardRepository;
import by.temniakov.english.tracker.store.repositories.ProjectRepository;
import by.temniakov.english.tracker.store.repositories.TrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional
public class ControllerHelper {

    private final ProjectRepository projectRepository;

    private final TrackerRepository trackerRepository;

    private final CardRepository cardRepository;

    public CardEntity getCardOrThrowException(Long cardId){
        return cardRepository
                .findById(cardId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Card with \"%s\" id doesn't exist.", cardId
                                )
                        )
                );
    }

    public TrackerEntity getTrackerOrThrowException(Long trackerId){
        return trackerRepository
                .findById(trackerId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Tracker with \"%s\" id doesn't exist.", trackerId
                                )
                        )
                );
    }

    public ProjectEntity getProjectOrThrowException(Long projectId) {
        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with \"%s\" doesn't exists.", projectId
                                )
                        )
                );
    }
}
