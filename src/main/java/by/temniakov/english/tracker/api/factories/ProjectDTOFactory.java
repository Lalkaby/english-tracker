package by.temniakov.english.tracker.api.factories;

import by.temniakov.english.tracker.api.dto.ProjectDTO;
import by.temniakov.english.tracker.store.entities.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectDTOFactory {

    public ProjectDTO mapProjectDTO(ProjectEntity entity){
        return ProjectDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
