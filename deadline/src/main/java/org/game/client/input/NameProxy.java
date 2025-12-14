package org.game.client.input;

import java.util.HashSet;
import java.util.Set;

public class NameProxy implements NameService {
    private final NameService realService;
    private final Set<String> bannedWords;

    public NameProxy(NameService realService) {
        this.realService = realService;
        this.bannedWords = new HashSet<>();

        bannedWords.add("admin");
        bannedWords.add("hitler");
        bannedWords.add("stalin");
        bannedWords.add("nazi");
        bannedWords.add("terrorist");
        bannedWords.add("isis");
        bannedWords.add("slave");
        bannedWords.add("murder");
        bannedWords.add("killer");
        bannedWords.add("suicide");
        bannedWords.add("rape");
        bannedWords.add("pedophile");
        bannedWords.add("sex");
        bannedWords.add("porn");
        bannedWords.add("xxx");
        bannedWords.add("fucker");
        bannedWords.add("bitch");
        bannedWords.add("whore");
        bannedWords.add("asshole");
        bannedWords.add("cunt");
        bannedWords.add("dick");
        bannedWords.add("cock");
        bannedWords.add("pussy");
        bannedWords.add("shit");
        bannedWords.add("faggot");
        bannedWords.add("retard");
        bannedWords.add("nigger");
        bannedWords.add("nigga");
        bannedWords.add("niga");
        bannedWords.add("ssrs");
        bannedWords.add("nyga");
        bannedWords.add("nygger");
    }

    @Override
    public void submitName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException("Name must be at least 3 characters.");
        }

        if (name.trim().length() > 16) {
            throw new IllegalArgumentException("Name is too long (Max 16).");
        }

        String check = name.toLowerCase();

        for (String badWord : bannedWords) {
            if (check.contains(badWord)) {
                throw new IllegalArgumentException("Name contains forbidden word: '" + badWord + "'");
            }
        }
        realService.submitName(name);
    }
}