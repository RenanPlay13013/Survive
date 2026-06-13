package net.loyalnetwork.survive.event.player;

import lombok.Getter;
import net.loyalnetwork.survive.match.Match;
import org.bukkit.entity.Player;

@Getter
public class PlayerEliminatedEvent extends PlayerEvent {
    private final Match match;
    private final String reason;

    public PlayerEliminatedEvent(Player player, Match match, String reason) {
        super(player);
        this.match = match;
        this.reason = reason;
    }
}
