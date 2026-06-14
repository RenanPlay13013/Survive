package net.loyalnetwork.survive.persistance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.loyalnetwork.survive.persistance.entity.MatchEntity;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "match_players",
        uniqueConstraints = @UniqueConstraint(columnNames = {"match_id", "player_uuid"}))
public class MatchPlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_players_seq")
    @SequenceGenerator(name = "match_players_seq", sequenceName = "match_players_id_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchEntity match;

    @Column(name = "player_uuid", nullable = false, length = 36)
    private String playerUuid;

    @Column(name = "player_name", nullable = false)
    private String playerName;

    // ALIVE / DEAD / QUIT / WON
    @Column(name = "status", nullable = false)
    private String status = "ALIVE";

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    @Column(name = "eliminated_at")
    private Instant eliminatedAt;

    @Column(name = "elimination_reason")
    private String eliminationReason;

    public MatchPlayerEntity(MatchEntity match, String playerUuid, String playerName) {
        this.match = match;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
    }
}