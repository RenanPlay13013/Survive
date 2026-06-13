package net.loyalnetwork.survive.match;

import net.loyalnetwork.survive.arena.Arena;
import net.loyalnetwork.survive.event.match.MatchCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class SimpleMatchManager implements MatchManager {
    private final Map<String, Match> matches = new HashMap<>();

    @Override
    public Match create(Arena arena) {
        String id = UUID.randomUUID().toString();

        Match match = new SimpleMatch(id, arena);

        Bukkit.getPluginManager().callEvent(new MatchCreateEvent(match));

        matches.put(id, match);

        return match;
    }

    @Override
    public Match getMatchByPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        for (Match match : matches.values()) {
            if (match.getPlayers().contains(uuid)) return match;
            if (match.getSpectators().contains(uuid)) return match;
        }
        return null;
    }

    @Override
    public void destroy(String matchId) {
        Match match = matches.remove(matchId);

        if (matchId != null) match.end();
    }

    @Override
    public Match get(String id) {
        return matches.get(id);
    }

    @Override
    public Collection<Match> getMatches() {
        return Collections.unmodifiableCollection(matches.values());
    }
}
