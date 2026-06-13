package net.loyalnetwork.survive.match;

import net.kyori.adventure.text.Component;
import net.loyalnetwork.survive.arena.Arena;
import net.loyalnetwork.survive.event.match.MatchCountdownEvent;
import net.loyalnetwork.survive.event.match.MatchEndEvent;
import net.loyalnetwork.survive.event.match.MatchStartEvent;
import net.loyalnetwork.survive.event.player.PlayerEliminatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SimpleMatch implements Match {
    private final String id;
    private final Arena arena;
    private final Set<UUID> players = new HashSet<>();
    private final Set<UUID> spectators = new HashSet<>();
    private int countdown = 30;
    private MatchState state = MatchState.WAITING;

    public SimpleMatch(String id, Arena arena) {
        this.id = id;
        this.arena = arena;
    }

    @Override
    public void addPlayer(Player player) {
        if (state != MatchState.WAITING && state != MatchState.STARTING) return;

        players.add(player.getUniqueId());

        player.teleport(arena.lobbySpawn());
    }

    @Override
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());
    }

    @Override
    public void start() {
        if (state != MatchState.WAITING) return;

        state = MatchState.IN_GAME;

        Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));
    }

    @Override
    public void end() {
        state = MatchState.ENDING;

        Bukkit.getPluginManager().callEvent(new MatchEndEvent(this));

        Location lobby = arena.lobbySpawn();

        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.teleport(lobby);
            }
        }

        players.clear();
        spectators.clear();

        state = MatchState.WAITING;
    }

    @Override
    public boolean isJoinable() {
        return state == MatchState.WAITING || state == MatchState.STARTING;
    }

    @Override
    public void tick() {
        if (state != MatchState.STARTING) return;

        if (players.isEmpty()) return;

        if (players.size() < arena.minPlayers()) {
            countdown = 30;
            return;
        }

        countdown--;

        Bukkit.getPluginManager().callEvent(new MatchCountdownEvent(this, countdown));

        if (countdown <= 0) {
            start();
        }
    }

    @Override
    public void eliminate(Player player, String reason) {
        UUID uuid = player.getUniqueId();

        if (!players.contains(uuid)) return;

        players.remove(uuid);
        spectators.remove(uuid);

        player.setGameMode(GameMode.SPECTATOR);

        Bukkit.getPluginManager().callEvent(new PlayerEliminatedEvent(player, this, reason));

        checkWinCondition();
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public Arena getArena() {
        return arena;
    }

    @Override
    public MatchState getState() {
        return state;
    }

    @Override
    public Set<UUID> getPlayers() {
        return players;
    }

    @Override
    public Set<UUID> getSpectators() {
        return spectators;
    }

    @Override
    public int getCountdown() {
        return countdown;
    }

    private void checkWinCondition() {
        if (state != MatchState.IN_GAME) return;

        if (players.size() <= 1) {
            UUID winner = players.stream().findFirst().orElse(null);

            if (winner != null) {
                Player p = Bukkit.getPlayer(winner);

                if (p != null) {
                    Bukkit.broadcast(Component.text(p.getName() + " venceu a partida!"));
                }
            }
            end();
        }
    }
}
