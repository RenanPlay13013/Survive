package net.loyalnetwork.survive.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        Lobby lobby = LobbyManager.getLobbyByPlayer(player);
        if (lobby == null) return;
        if (!lobby.getGameName().equalsIgnoreCase("Survive")) return;

    }
}