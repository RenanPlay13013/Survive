package net.loyalnetwork.survive.event.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.loyalnetwork.survive.match.Match;

@RequiredArgsConstructor
@Getter
public class MatchCountdownEvent extends MatchEvent {
    private final Match match;
    private final int countdown;

}
