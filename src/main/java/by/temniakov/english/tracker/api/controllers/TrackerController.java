package by.temniakov.english.tracker.api.controllers;

import by.temniakov.english.tracker.api.dto.AckDTO;
import by.temniakov.english.tracker.api.dto.TrackerDTO;
import by.temniakov.english.tracker.api.exceptions.BadRequestException;
import by.temniakov.english.tracker.api.controllers.helpers.ControllerHelper;
import by.temniakov.english.tracker.api.exceptions.NotFoundException;
import by.temniakov.english.tracker.api.factories.TrackerDTOFactory;
import by.temniakov.english.tracker.store.entities.ProjectEntity;
import by.temniakov.english.tracker.store.entities.TrackerEntity;
import by.temniakov.english.tracker.store.repositories.TrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@RestController
public class TrackerController {

    private final TrackerRepository trackerRepository;

    private final TrackerDTOFactory trackerDTOFactory;

    private final ControllerHelper controllerHelper;

    public static final String GET_TRACKERS = "/api/projects/{project_id}/trackers";
    public static final String CREATE_TRACKER = "/api/projects/{project_id}/trackers";
    public static final String UPDATE_TRACKER= "/api/trackers/{tracker_id}";
    public static final String CHANGE_TRACKER_POSITION = "/api/trackers/{tracker_id}/position/change";
    public static final String DELETE_TRACKER = "/api/trackers/{tracker_id}";

    @GetMapping(GET_TRACKERS)
    public List<TrackerDTO> getTrackers(
            @PathVariable(name = "project_id") Long projectId) {
        ProjectEntity projectEntity = controllerHelper.getProjectOrThrowException(projectId);

        return projectEntity
                .getTrackers()
                .stream()
                .map(trackerDTOFactory::mapTrackerDTO)
                .collect(Collectors.toList());
    }

    @PostMapping(CREATE_TRACKER)
    public TrackerDTO createTracker(
            @PathVariable(name = "project_id") Long projectId,
            @RequestParam(value = "tracker_name") String trackerName,
            @RequestParam(value = "tracker_duration") Duration trackerDuration
            ){
        if (trackerName.isBlank()){
            throw new BadRequestException("Tracker name can't be empty");
        }
        
        if (trackerDuration.isNegative()){
            throw new BadRequestException("Duration can't be less than zero ");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        Optional<TrackerEntity> optionalAnotherTracker = Optional.empty();

        for (TrackerEntity tracker : project.getTrackers()){

            if (tracker.getName().equalsIgnoreCase(trackerName)){
                throw new BadRequestException(String.format("Tracker  \"%s\" already exists.", trackerName));
            }

            if (tracker.getRightTracker().isEmpty()){
                optionalAnotherTracker = Optional.of(tracker);
                break;
            }
        }

        TrackerEntity tracker = trackerRepository.saveAndFlush(
                TrackerEntity.builder()
                        .name(trackerName)
                        .duration(trackerDuration)
                        .project(project)
                        .build()) ;

        optionalAnotherTracker
                .ifPresent(anotherTracker -> {
                    tracker.setLeftTracker(anotherTracker);
                    anotherTracker.setRightTracker(tracker);
                    trackerRepository.saveAndFlush(anotherTracker);
                });
        final TrackerEntity savedTracker = trackerRepository.saveAndFlush(tracker);

        return trackerDTOFactory.mapTrackerDTO(savedTracker);
    }

    @PatchMapping(UPDATE_TRACKER)
    public TrackerDTO updateTracker(
            @PathVariable(name = "tracker_id") Long trackerId,
            @RequestParam(value = "tracker_name",required = false) Optional<String> optionalTrackerName,
            @RequestParam(value = "tracker_duration",required = false) Optional<Duration> optionalTrackerDuration) {

        optionalTrackerName.ifPresent(name -> {
            if (name.isBlank()){
                throw new BadRequestException("Tracker name can't be empty");
            }
        });

        optionalTrackerDuration.ifPresent(duration -> {
            if (duration.isNegative()){
                throw new BadRequestException("Duration can't be less than zero");
            }
        });


        TrackerEntity tracker = controllerHelper.getTrackerOrThrowException(trackerId);

        if (!optionalTrackerName.isEmpty())
        {
            trackerRepository
                    .findTrackerEntityByProjectIdAndNameContainsIgnoreCase(
                            tracker.getProject().getId(),
                            optionalTrackerName.get())
                    .filter(anotherTracker -> !anotherTracker.getId().equals(trackerId))
                    .ifPresent(anotherTracker -> {
                        throw new BadRequestException(String.format("Tracker \"%s\" already exists", optionalTrackerName.get()));
                    });
            tracker.setName(optionalTrackerName.get());
        }

        if (optionalTrackerDuration.isPresent()){
             tracker.setDuration(optionalTrackerDuration.get());
        }

        tracker = trackerRepository.saveAndFlush(tracker);
        return trackerDTOFactory.mapTrackerDTO(tracker);
    }

    @PatchMapping(CHANGE_TRACKER_POSITION)
    public TrackerDTO changeTrackerPosition(
            @PathVariable(name = "tracker_id") Long trackerId,
            @RequestParam(value = "left_tracker_id") Optional<Long> optionalLeftTrackerId) {

        TrackerEntity changeTracker = controllerHelper.getTrackerOrThrowException(trackerId);
        ProjectEntity project = changeTracker.getProject();

       Optional<Long> optionalOldLeftTrackerId = changeTracker
                .getLeftTracker()
               .map(TrackerEntity::getId);

       if (optionalOldLeftTrackerId.equals(optionalLeftTrackerId)){
                return trackerDTOFactory.mapTrackerDTO(changeTracker);}

            Optional<TrackerEntity>optionalNewLeftTracker = optionalLeftTrackerId
                .map(leftTrackerId ->{
                    if (trackerId.equals(leftTrackerId)){
                        throw new BadRequestException("Left tracker id equals changed tracker id.");
                    }

                    TrackerEntity leftTrackerEntity = controllerHelper.getTrackerOrThrowException(leftTrackerId);

                    if (!project.getId().equals(leftTrackerEntity.getProject().getId())){
                        throw new BadRequestException("Tracker position can be changed within the same project.");
                    }

                    return leftTrackerEntity;
                });

       Optional<TrackerEntity> optionalNewRightTracker;
       if (optionalNewLeftTracker.isEmpty()){
           optionalNewRightTracker = project
                    .getTrackers()
                    .stream()
                    .filter(anotherTracker -> anotherTracker.getLeftTracker().isEmpty())
                    .findAny() ;
       }else{
           optionalNewRightTracker = optionalNewLeftTracker
                   .get()
                   .getRightTracker();
       }

        replaceOldTrackerPosition(changeTracker);

        if ( optionalNewLeftTracker.isPresent()){
            TrackerEntity newLeftTracker = optionalNewLeftTracker.get();

            newLeftTracker.setRightTracker(changeTracker);

            changeTracker.setLeftTracker(newLeftTracker);
        } else {
            changeTracker.setLeftTracker(null);
        }
        
        if ( optionalNewRightTracker.isPresent()){
            TrackerEntity newRightTracker = optionalNewRightTracker.get();

            newRightTracker.setLeftTracker(changeTracker);

            changeTracker.setRightTracker(newRightTracker);
        } else {
            changeTracker.setRightTracker(null);
        }

       changeTracker = trackerRepository.saveAndFlush(changeTracker);

        optionalNewRightTracker
                .ifPresent(trackerRepository::saveAndFlush);
     optionalNewLeftTracker
                .ifPresent(trackerRepository::saveAndFlush);

        return trackerDTOFactory.mapTrackerDTO(changeTracker);
    }

    @DeleteMapping(DELETE_TRACKER)
    public AckDTO deleteTracker(
            @PathVariable(name = "tracker_id") Long trackerId) {
        TrackerEntity changeTracker = controllerHelper.getTrackerOrThrowException(trackerId);

        replaceOldTrackerPosition(changeTracker);

        changeTracker.setLeftTracker(null);
        changeTracker.setRightTracker(null);

        changeTracker = trackerRepository.saveAndFlush(changeTracker);

        trackerRepository.delete(changeTracker);

        return AckDTO.builder().answer(true).build();
    }

    private void replaceOldTrackerPosition(TrackerEntity changeTracker) {
        Optional<TrackerEntity> optionalOldLeftTracker = changeTracker.getLeftTracker();
        Optional<TrackerEntity> optionalOldRightTracker = changeTracker.getRightTracker();

        optionalOldLeftTracker
                .ifPresent(it ->{
                    it.setRightTracker(optionalOldRightTracker.orElse(null));
                    trackerRepository.saveAndFlush(it);
                });

        optionalOldRightTracker
                .ifPresent(it ->
                {
                    it.setLeftTracker(optionalOldLeftTracker.orElse(null));
                    trackerRepository.saveAndFlush(it);
                });
    }
}
