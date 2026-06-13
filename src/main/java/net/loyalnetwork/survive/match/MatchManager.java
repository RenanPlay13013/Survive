package net.loyalnetwork.survive.match;

import net.loyalnetwork.survive.arena.Arena;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface MatchManager {
    Match create(Arena arena);

    void destroy(String matchId);

    Match get(String id);

    Collection<Match> getMatches();

    Match getMatchByPlayer(Player player);
}
