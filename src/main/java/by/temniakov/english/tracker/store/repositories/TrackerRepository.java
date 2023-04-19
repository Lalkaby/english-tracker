package by.temniakov.english.tracker.store.repositories;

import by.temniakov.english.tracker.store.entities.TrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackerRepository extends JpaRepository<TrackerEntity, Long> {
    Optional<TrackerEntity> findTrackerEntityByProjectIdAndNameContainsIgnoreCase(
            Long projectId, String trackerName);
}
