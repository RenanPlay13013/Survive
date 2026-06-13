package net.loyalnetwork.survive.match;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class MatchTickTask extends BukkitRunnable {
    private final MatchManager matchManager;

    @Override
    public void run() {
        for (Match match : matchManager.getMatches()) {
            match.tick();
        }
    }
}
