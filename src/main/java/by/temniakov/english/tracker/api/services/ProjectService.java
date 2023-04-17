package by.temniakov.english.tracker.api.services;

import by.temniakov.english.tracker.store.entities.ProjectEntity;
import by.temniakov.english.tracker.store.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends BaseRepositoryService<ProjectEntity>{
    public ProjectService(ProjectRepository projectRepository) {
        super(projectRepository);
    }
}
