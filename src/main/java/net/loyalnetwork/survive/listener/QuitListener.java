package net.loyalnetwork.survive.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import wueffi.MiniGameCore.api.MiniGameCoreAPI;
import wueffi.MiniGameCore.managers.GameManager;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

import java.util.Collection;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Lobby lobby = LobbyManager.getLobbyByPlayer(player);
        if (lobby == null) return;
        if (!lobby.getGameName().equalsIgnoreCase("Survive")) return;

        MiniGameCoreAPI.playerDeath(player.getUniqueId());

        Collection<Player> alivePlayers = GameManager.getAlivePlayersByLobby(lobby);
        if (alivePlayers == null) return;

        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.iterator().next();
            MiniGameCoreAPI.winPlayer(lobby, winner);
        }
    }
}