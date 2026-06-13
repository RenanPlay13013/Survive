package net.loyalnetwork.survive.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import wueffi.MiniGameCore.api.GameOverEvent;
import wueffi.MiniGameCore.api.GameStartEvent;
import wueffi.MiniGameCore.api.MiniGameCoreAPI;
import wueffi.MiniGameCore.utils.Lobby;

import java.util.Set;

public class GameListener implements Listener {

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        if (!event.getGameName().equalsIgnoreCase("Survive")) return;

        Lobby lobby = event.getLobby();

        for (Player player : lobby.getPlayers()) {
            player.sendMessage(Component.text(
                    "A partida começou! Seja o último sobrevivente!",
                    NamedTextColor.GREEN
            ));
        }
    }

    @EventHandler
    public void onGameOver(GameOverEvent event) {
        Lobby lobby = event.getLobby();

        Bukkit.broadcast(Component.text(
                "[Survive] Partida " + lobby.getLobbyId() + " encerrada!",
                NamedTextColor.GOLD
        ));
    }
}
