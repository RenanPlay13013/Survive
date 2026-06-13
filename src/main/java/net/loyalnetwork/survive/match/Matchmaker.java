package net.loyalnetwork.survive.match;

import net.loyalnetwork.survive.arena.Arena;
import net.loyalnetwork.survive.arena.ArenaManager;
import net.loyalnetwork.survive.queue.Queue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class Matchmaker extends BukkitRunnable {

    private final Queue queue;
    private final MatchManager matchManager;
    private final ArenaManager arenaManager;

    public Matchmaker(Queue queue, MatchManager matchManager, ArenaManager arenaManager) {
        this.queue = queue;
        this.matchManager = matchManager;
        this.arenaManager = arenaManager;
    }

    @Override
    public void run() {

        if (queue.size() < 2) return;

        for (Arena arena : arenaManager.getArenas()) {

            int max = arena.maxPlayers();
            int min = arena.minPlayers();

            long currentMatchesInArena = matchManager.getMatches().stream()
                    .filter(m -> m.getArena().equals(arena))
                    .count();

            if (currentMatchesInArena >= 3) continue;

            if (queue.size() < min) continue;

            List<UUID> players = queue.drain(max);

            if (players.size() < min) {
                players.forEach(queue::add);
                return;
            }

            createMatch(arena, players);
        }
    }

    private void createMatch(Arena arena, List<UUID> players) {

        Match match = matchManager.create(arena);

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                match.addPlayer(player);
            }
        }

        match.start();
    }
}
