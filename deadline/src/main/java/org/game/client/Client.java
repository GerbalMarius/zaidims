package org.game.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.components.ChatUI;
import org.game.client.input.*;
import org.game.client.mediator.ClientMediator;
import org.game.client.mediator.Mediator;
import org.game.entity.ClassType;
import org.game.utils.Panels;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

@Slf4j
@Getter
public final class Client {

    private final UUID clientId = UUID.randomUUID();
    private String playerName = "";
    private ClassType chosenClass;

    private final GameState gameState = new GameState();
    private final ChatUI chatUI = new ChatUI();
    private final KeyboardHandler keyboardHandler = new KeyboardHandler(chatUI,this);
    private final MouseHandler mouseHandler = new MouseHandler();

    private Mediator mediator;

    static void main() {
        new Client().createClientGui();
    }

    private void createClientGui() {
        ControllerAdapter adapter = new ControllerAdapter(new Controller());
        if (showCharacterWindow()) {
            adapter.shutdown();
            return;
        }

        GamePanel gamePanel = new GamePanel(
                clientId,
                gameState,
                keyboardHandler,
                mouseHandler,
                adapter,
                chatUI);

         this.mediator = new ClientMediator(this, gameState, gamePanel);

        gamePanel.setMediator(mediator);

        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setSize(600, 400);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        gamePanel.startGameLoop();
        mediator.start();
    }
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    private boolean showCharacterWindow() {
        JPanel classPanelWrapper = new JPanel(new BorderLayout());
        classPanelWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final ClassType[] selectedClass = {null};
        JPanel panel = Panels.drawClassPanel(ct -> selectedClass[0] = ct);
        classPanelWrapper.add(panel, BorderLayout.CENTER);

        JTextField nameField = new JTextField();
        Panels.allowOnlyLetterOrDigit(nameField, 30);

        Object[] message = {
                "Enter player name:", nameField,
                "Select class:", classPanelWrapper
        };

        // Setup the Proxy Chain
        NameService realService = new RealNameService(this);
        NameService nameProxy = new NameProxy(realService);

        // --- THE VALIDATION LOOP ---
        while (true) {
            int option = JOptionPane.showConfirmDialog(
                    null, message, "Choose name & class",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return true; // User clicked Cancel or X
            }

            String inputName = nameField.getText();

            try {
                // 1. Ask Proxy to submit
                nameProxy.submitName(inputName);

                // 2. If Class isn't selected, handle that too
                if (selectedClass[0] == null) {
                    JOptionPane.showMessageDialog(null, "Please select a class!");
                    continue; // Restart loop
                }

                // 3. If we get here, Name is Good AND Class is selected
                this.chosenClass = selectedClass[0];
                break; // BREAK THE LOOP -> Start Game

            } catch (IllegalArgumentException e) {
                // 4. Proxy said NO. Show error and RESTART loop.
                JOptionPane.showMessageDialog(null,
                        "Invalid Name: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                // The loop repeats, forcing them to try again.
            }
        }

        log.info("Player name: {}, Class: {}", playerName, chosenClass);
        return false; // Proceed to game
    }
    public void sendChatMessage(String message) {
        if (mediator != null) {
            mediator.sendChatMessage(message);
        }
    }
}
