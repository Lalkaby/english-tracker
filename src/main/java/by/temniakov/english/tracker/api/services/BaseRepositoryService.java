package by.temniakov.english.tracker.api.services;

import by.temniakov.english.tracker.api.exceptions.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseRepositoryService<T> {
    protected final JpaRepository<T, Long> repository;

    public BaseRepositoryService(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public T getEntityOrThrowException(Long entityId, String entityName) {
        return repository
                .findById(entityId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "%s with \"%s\" id doesn't exist.", entityName, entityId
                                )
                        )
                );
    }
}
