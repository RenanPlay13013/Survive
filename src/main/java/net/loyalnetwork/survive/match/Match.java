package net.loyalnetwork.survive.match;

import net.loyalnetwork.survive.arena.Arena;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface Match {

    String getId();

    Arena getArena();

    MatchState getState();

    Set<UUID> getPlayers();

    Set<UUID> getSpectators();

    void addPlayer(Player player);

    void removePlayer(Player player);

    void start();

    void end();

    boolean isJoinable();

    void tick();

    int getCountdown();

    void eliminate(Player player, String reason);
}
