package net.loyalnetwork.survive.arena;

import java.util.ArrayList;
import java.util.List;

public class SimpleArenaManager implements ArenaManager {

    private final List<Arena> arenas = new ArrayList<>();

    public void register(Arena arena) {
        arenas.add(arena);
    }

    @Override
    public void unregister(String id) {
        arenas.removeIf(arena -> arena.id() == id);
}

@Override
public Arena getArena(String id) {
        for (Arena arena : arenas) {
            if (arena.id().equals(id)) return arena;
        }

        return null;
}

public List<Arena> getArenas() {
    return arenas;
}
}