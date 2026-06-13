package net.loyalnetwork.survive.event.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.loyalnetwork.survive.match.Match;

@RequiredArgsConstructor
@Getter
public class MatchStartEvent extends MatchEvent {
    private final Match match;

}
