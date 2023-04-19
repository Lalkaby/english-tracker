package by.temniakov.english.tracker.store.repositories;

import by.temniakov.english.tracker.store.entities.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<CardEntity, Long> {
    
}
