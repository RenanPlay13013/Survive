package net.loyalnetwork.survive.event.match;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatchEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public MatchEvent() {
        super(true);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
