package net.loyalnetwork.survive.arena;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public record ArenaConfig(String id, String displayName, World world, Region playableRegion, Location lobbySpawn,
                          List<Location> spawnPoints, int minPlayers, int maxPlayers) implements Arena {

}
