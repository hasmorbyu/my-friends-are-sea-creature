package com.browl.seacreatures.system;

import com.browl.seacreatures.model.Friend;

/** The behavior an {@link EventDefinition} runs when it triggers, given one or two participating friends. */
public interface EventEffect {
    /** @param b may be null for single-friend events. Returns the message to show/log. */
    String apply(GameState state, Friend a, Friend b);
}
