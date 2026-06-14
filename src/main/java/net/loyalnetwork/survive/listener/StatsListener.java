package net.loyalnetwork.survive.listener;

import lombok.RequiredArgsConstructor;
import net.loyalnetwork.survive.persistance.repository.MatchRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import wueffi.MiniGameCore.api.GameOverEvent;
import wueffi.MiniGameCore.api.GameStartEvent;
import wueffi.MiniGameCore.managers.GameManager;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

import java.util.Collection;
import java.util.UUID;


@RequiredArgsConstructor
public class StatsListener implements Listener {

    private final MatchRepository repository;
    private final Plugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameStart(GameStartEvent event) {
        if (!event.getGameName().equalsIgnoreCase("Survive")) return;
        if (event.isCancelled()) return;

        Lobby lobby = event.getLobby();
        String lobbyId = lobby.getLobbyId();
        String gameName = lobby.getGameName();

        var players = lobby.getPlayers().stream().toList();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            repository.createMatch(lobbyId, gameName);

            for (Player player : players) {
                repository.addPlayerToMatch(lobbyId, player);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameOver(GameOverEvent event) {
        Lobby lobby = event.getLobby();
        String lobbyId = lobby.getLobbyId();

        Collection<Player> alivePlayers = GameManager.getAlivePlayersByLobby(lobby);

        UUID winnerUuid = null;
        String winnerName = null;

        if (alivePlayers != null && alivePlayers.size() == 1) {
            Player winner = alivePlayers.iterator().next();
            winnerUuid = winner.getUniqueId();
            winnerName = winner.getName();
        }

        final UUID finalWinnerUuid = winnerUuid;
        final String finalWinnerName = winnerName;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            repository.finishMatch(lobbyId, finalWinnerUuid, finalWinnerName);

            if (finalWinnerUuid != null) {
                repository.registerWin(finalWinnerUuid);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Lobby lobby = LobbyManager.getLobbyByPlayer(player);
        if (lobby == null) return;
        if (!lobby.getGameName().equalsIgnoreCase("Survive")) return;

        String lobbyId = lobby.getLobbyId();
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                repository.eliminateByDeath(lobbyId, uuid)
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Lobby lobby = LobbyManager.getLobbyByPlayer(player);
        if (lobby == null) return;
        if (!lobby.getGameName().equalsIgnoreCase("Survive")) return;

        String lobbyId = lobby.getLobbyId();
        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                repository.eliminateByQuit(lobbyId, uuid)
        );
    }
}