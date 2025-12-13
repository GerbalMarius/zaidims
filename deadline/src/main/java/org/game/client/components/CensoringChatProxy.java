package org.game.client.components;

import java.util.Set;
import java.util.regex.Pattern;

public class CensoringChatProxy implements ChatService {
    private final RealChatService realService;
    private final Set<String> bannedWords;
    private final Pattern censorPattern;

    public CensoringChatProxy(RealChatService realService) {
        this.realService = realService;
        this.bannedWords = Set.of(
                "Nigger", "Mode", "TeemoIsUgly",
                "LOL", "Marius","Saras"
        );
        this.censorPattern = buildCensorPattern();
    }

    private Pattern buildCensorPattern() {
        String regex = "\\b(" + String.join("|", bannedWords) + ")\\b";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void sendMessage(String message) {
        String censored = censorMessage(message);
        realService.sendMessage(censored);
    }

    @Override
    public void addMessage(String playerName, String message) {
        String censored = censorMessage(message);
        realService.addMessage(playerName, censored);
    }

    private String censorMessage(String message) {
        return censorPattern.matcher(message)
                .replaceAll(match -> "*".repeat(match.group().length()));
    }
}