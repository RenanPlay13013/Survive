package net.loyalnetwork.survive.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public interface Region {
    boolean contains(Location location);

    boolean contains(Vector vector);

    Location getCenter();

    World getWorld();
}
