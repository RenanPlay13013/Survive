package net.loyalnetwork.survive.arena;

import java.util.List;

public interface ArenaManager {

    Arena getArena(String id);

    List<Arena> getArenas();

    void register(Arena arena);

    void unregister(String id);
}
