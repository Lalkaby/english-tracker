package by.temniakov.english.tracker.api.services;

import by.temniakov.english.tracker.store.entities.TrackerEntity;
import by.temniakov.english.tracker.store.repositories.TrackerRepository;
import org.springframework.stereotype.Service;

@Service
public class TrackerService extends BaseRepositoryService<TrackerEntity> {
    public TrackerService(TrackerRepository trackerRepository) {
        super(trackerRepository);
    }

}
