package net.loyalnetwork.survive.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "player_stats")
public class PlayerStatsEntity {
    @Id
    @Column(name = "uuid", nullable = false, updatable = false, length = 36)
    private String uuid;

    @Column(name = "last_name", nullable = false)
    private String lastKnownName;

    @Column(name = "matches_played", nullable = false)
    private int matchesPlayed = 0;

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "deaths", nullable = false)
    private int deaths = 0;

    @Column(name = "quits", nullable = false)
    private int quits = 0;

    public PlayerStatsEntity(UUID uuid, String name) {
        this.uuid = uuid.toString();
        this.lastKnownName = name;
    }

    public void incrementMatchesPlayed() {
        this.matchesPlayed++;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public void incrementQuits() {
        this.quits++;
    }
}
