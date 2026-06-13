package net.loyalnetwork.survive.listener;

import lombok.RequiredArgsConstructor;
import net.loyalnetwork.survive.match.Match;
import net.loyalnetwork.survive.match.MatchManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class QuitListener implements Listener {
    private final MatchManager matchManager;

    @EventHandler
    public void onDeath(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Match match = matchManager.getMatchByPlayer(player);

        if (match == null) return;

        match.eliminate(player, "QUIT");
    }
}
