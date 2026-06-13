package net.loyalnetwork.survive.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import wueffi.MiniGameCore.api.MiniGameCoreAPI;
import wueffi.MiniGameCore.managers.LobbyManager;
import wueffi.MiniGameCore.utils.Lobby;

import java.util.Set;
import java.util.UUID;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        Lobby lobby = LobbyManager.getLobbyByPlayer(player);
        if (lobby == null) return;

        if (!lobby.getGameName().equalsIgnoreCase("Survive")) return;

        event.getDrops().clear();
        event.setDroppedExp(0);

        MiniGameCoreAPI.playerDeath(player.getUniqueId());

        Set<Player> alivePlayers = getAlivePlayers(lobby, player.getUniqueId());

        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.iterator().next();

            winner.sendMessage(Component.text("Você venceu a partida!", NamedTextColor.GOLD));

            MiniGameCoreAPI.winPlayer(lobby, winner);

        } else if (alivePlayers.isEmpty()) {
            //EMPATE
        }
    }

    private Set<Player> getAlivePlayers(Lobby lobby, UUID eliminated) {
        Set<Player> alive = lobby.getPlayers();
        alive.removeIf(p -> p.getUniqueId().equals(eliminated));
        return alive;
    }
}
