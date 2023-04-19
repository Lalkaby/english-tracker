package by.temniakov.english.tracker.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "card")
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String phrase;

    @Builder.Default
    @Column(
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            name = "created_at")
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column(
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            name = "started_at")
    private Instant startedAt = Instant.now();

    @ManyToOne(targetEntity = TrackerEntity.class, cascade =  CascadeType.MERGE)
    @JoinColumn(name = "tracker_id")
    private TrackerEntity tracker;
}
