package net.loyalnetwork.survive.persistance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "matches", indexes = {
        @Index(name = "idx_matches_lobby_id", columnList = "lobby_id"),
        @Index(name = "idx_matches_status", columnList = "status")
})
public class MatchEntity {

    @Id
    @Column(name = "match_id", nullable = false, updatable = false, length = 36)
    private String matchId;

    @Column(name = "lobby_id", nullable = false)
    private String lobbyId;

    @Column(name = "game_name", nullable = false)
    private String gameName;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "winner_uuid", length = 36)
    private String winnerUuid;

    @Column(name = "winner_name")
    private String winnerName;

    // Estado atual: IN_GAME, FINISHED
    @Column(name = "status", nullable = false)
    private String status = "IN_GAME";

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchPlayerEntity> players = new ArrayList<>();

    public MatchEntity(String lobbyId, String gameName) {
        this.matchId = UUID.randomUUID().toString();
        this.lobbyId = lobbyId;
        this.gameName = gameName;
    }
}