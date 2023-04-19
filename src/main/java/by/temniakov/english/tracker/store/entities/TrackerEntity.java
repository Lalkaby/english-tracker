package by.temniakov.english.tracker.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tracker")
public class TrackerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @Column(columnDefinition = "interval")
    private Duration duration;

    @Column
    private Long ordinal;

    @OneToOne
    @JoinColumn(name = "left_tracker_id")
    private TrackerEntity leftTracker;

    @OneToOne
    @JoinColumn(name = "right_tracker_id")
    private TrackerEntity rightTracker;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @ManyToOne(targetEntity = ProjectEntity.class)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column
    @OneToMany(mappedBy = "tracker", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<CardEntity> cards = new ArrayList<>();

    public Optional<TrackerEntity> getRightTracker() {
        return Optional.ofNullable(rightTracker);
    }

    public Optional<TrackerEntity> getLeftTracker() {
        return Optional.ofNullable(leftTracker);
    }
}
