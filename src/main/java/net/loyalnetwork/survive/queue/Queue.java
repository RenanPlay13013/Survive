package net.loyalnetwork.survive.queue;

import java.util.*;

public class Queue {

    private final Set<UUID> players = new LinkedHashSet<>();

    public void add(UUID uuid) {
        players.add(uuid);
    }

    public void remove(UUID uuid) {
        players.remove(uuid);
    }

    public int size() {
        return players.size();
    }

    public boolean contains(UUID uuid) {
        return players.contains(uuid);
    }

    public List<UUID> drain(int amount) {
        List<UUID> list = new ArrayList<>();

        Iterator<UUID> it = players.iterator();

        while (it.hasNext() && list.size() < amount) {
            list.add(it.next());
            it.remove();
        }

        return list;
    }

    public Set<UUID> getAll() {
        return Collections.unmodifiableSet(players);
    }
}