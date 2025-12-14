package org.game.client.components;

import lombok.extern.slf4j.Slf4j;

import static org.game.utils.profanity.ProfanityFilter.censorMessage;

@Slf4j
public final class CensoringChatProxy implements ChatService {
    private final RealChatService realService;

    public CensoringChatProxy(RealChatService realService) {
        this.realService = realService;
    }
    @Override
    public void sendMessage(String message) {
        realService.sendMessage(censorMessage(message));
    }

    @Override
    public void addMessage(String playerName, String message) {
        realService.addMessage(playerName, censorMessage(message));
    }
}
