package net.loyalnetwork.survive.arena;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

@Getter
public final class CuboidRegion implements Region {

    private final World world;
    private final BoundingBox boundingBox;

    public CuboidRegion(Location pos1, Location pos2) {
        if (pos1 == null || pos2 == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

        if (!pos1.getWorld().equals(pos2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        this.world = pos1.getWorld();
        this.boundingBox = BoundingBox.of(pos1, pos2);
    }

    @Override
    public boolean contains(Location location) {
        return location.getWorld().equals(world)
                && boundingBox.contains(location.toVector());
    }

    @Override
    public boolean contains(Vector vector) {
        return boundingBox.contains(vector);
    }

    @Override
    public Location getCenter() {
        return new Location(
                world,
                boundingBox.getCenterX(),
                boundingBox.getCenterY(),
                boundingBox.getCenterZ()
        );
    }
}