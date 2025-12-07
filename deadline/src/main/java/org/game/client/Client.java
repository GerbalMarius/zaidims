package org.game.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.game.client.input.Controller;
import org.game.client.input.ControllerAdapter;
import org.game.client.input.KeyboardHandler;
import org.game.client.input.MouseHandler;
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
    private final KeyboardHandler keyboardHandler = new KeyboardHandler();
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
                adapter);

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
                "Select class (click or press Enter/Space):", classPanelWrapper
        };

        int option = JOptionPane.showConfirmDialog(
                null, message, "Choose name & class",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            if (name != null && !name.isEmpty()) {
                playerName = name.trim();
            } else {
                playerName = "Player " + clientId.toString().substring(0, 10);
            }
            chosenClass = selectedClass[0];
            log.info("Player name: {}, Class: {}", playerName, chosenClass);
        } else {
            return true; // canceled
        }
        return false;
    }
}
