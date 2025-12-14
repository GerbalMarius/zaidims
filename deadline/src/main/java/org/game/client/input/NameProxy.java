package org.game.client.input;

import org.game.utils.profanity.ProfanityFilter;

public final class NameProxy implements NameService {
    private final NameService realService;
    public NameProxy(NameService realService) {
        this.realService = realService;
    }

    @Override
    public void submitName(String name) {
        if (name == null || name.length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters.");
        }

        if (name.length() > 16) {
            throw new IllegalArgumentException("Name is too long (Max 16).");
        }

        String check = ProfanityFilter.normalizeForMatch(name);

        if (ProfanityFilter.containsProfanity(check)) {
            throw new IllegalArgumentException("Name contains forbidden characters/symbols: '" + check + "'");
        }
        realService.submitName(name);
    }
}