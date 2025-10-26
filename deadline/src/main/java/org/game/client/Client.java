package org.game.client;

import lombok.extern.slf4j.Slf4j;
import org.game.client.input.Controller;
import org.game.client.input.ControllerAdapter;
import org.game.client.input.KeyboardHandler;
import org.game.client.input.MouseHandler;
import org.game.entity.ClassType;
import org.game.entity.Player;
import org.game.json.Json;
import org.game.message.*;
import org.game.utils.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import java.util.Queue;
import java.util.UUID;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.game.json.JsonLabelPair.labelPair;

@Slf4j
public final class Client {

    private static final int PORT = 9000;

    private final UUID clientId = UUID.randomUUID();
    private String playerName = "";
    private ClassType chosenClass;

    private SocketChannel socketChannel;
    private Selector selector;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 8);// 8 KB
    private final Queue<ByteBuffer> pendingWrites = new ConcurrentLinkedQueue<>();

    private final Json json = new Json();

    private final GameState gameState =  new GameState();
    private GamePanel gamePanel;
    private final KeyboardHandler keyboardHandler = new KeyboardHandler();
    private final MouseHandler mouseHandler = new MouseHandler();

    static void main() {
        new Client().createClientGui();
    }

    private void createClientGui() {
        ControllerAdapter adapter = new ControllerAdapter(new Controller());
        if (showCharacterWindow()) {
            adapter.shutdown();
            return;
        }

        //-----UI
        JFrame frame = new JFrame("Game");


        gamePanel = new GamePanel(clientId, gameState, keyboardHandler, mouseHandler, adapter);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setSize(600, 400);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        //-------------------

        gamePanel.setMoveCallback((dx, dy) -> {
            MoveMessage moveMessage = new MoveMessage(clientId, dx, dy);
            sendLocalInput(json.toJson(moveMessage, labelPair(Message.JSON_LABEL, "move")));
        });

        gamePanel.setShootCallback(() -> {
            Player player = gameState.getPlayer(clientId);
            if (player == null) return;

         ProjectileSpawnMessage proj = new ProjectileSpawnMessage(
                             player.getGlobalX(),
                             player.getGlobalY(),
                             player.getDirection(),
                             UUID.randomUUID(),
                             clientId
                     );

            sendLocalInput(json.toJson(proj, labelPair(Message.JSON_LABEL, "projectileSpawn")));
        });

        gamePanel.setHealthCallback(enemy -> {
            EnemyHealthUpdateMessage msg = new EnemyHealthUpdateMessage(enemy.getId(), enemy.getHitPoints());
            sendLocalInput(json.toJson(msg, labelPair(Message.JSON_LABEL, "enemyHealth")));
        });

        gamePanel.setPowerUpCallback(powerUp -> {
            PowerUpRemoveMessage msg = new PowerUpRemoveMessage(powerUp.getId());
            sendLocalInput(json.toJson(msg, labelPair(Message.JSON_LABEL, "powerUpRemove")));
        });

        gamePanel.startGameLoop();
        startNetworkThread();
    }

    private boolean showCharacterWindow() {

        JPanel classPanelWrapper = new JPanel(new BorderLayout());
        classPanelWrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final ClassType[] selectedClass = {null};

        JPanel panel = GUI.drawClassPanel(ct -> selectedClass[0] = ct);


        classPanelWrapper.add(panel, BorderLayout.CENTER);

        JTextField nameField = new JTextField();
        GUI.allowOnlyLetterOrDigit(nameField, 30);

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


    private void startNetworkThread() {
        var threadBuilder = Thread.ofVirtual()
                .name("ClientThread : " + playerName);


        threadBuilder.start(() -> {
            try {
                connectToServer();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void connectToServer() throws InterruptedException {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();

            socketChannel.configureBlocking(false);

            socketChannel.connect(new InetSocketAddress(PORT));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    selector.select(500);
                } catch (ClosedSelectorException cse) {
                    break;
                }
                var selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) continue;

                    if (key.isConnectable()) {
                        finishConnect(key);
                    } else if (key.isReadable()) {
                        doRead(key);
                    } else if (key.isWritable()) {
                        doWrite(key);
                    }
                }
                SelectionKey key = (socketChannel != null) ? socketChannel.keyFor(selector) : null;
                if (key != null && key.isValid() && !pendingWrites.isEmpty()) {
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
            }

        } catch (IOException e) {
            log.error("Failed to connect to server on port : " + PORT + " " + " connection refused");
            Thread.sleep(600);
            //try to connect again
            connectToServer();
        } finally {
            try {
                if (socketChannel != null) socketChannel.close();
                if (selector != null) selector.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }


    private void sendLocalInput(String payload) {
        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
        ByteBuffer writeBuffer = ByteBuffer.allocate(4 + bytes.length);
        writeBuffer.putInt(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();

        pendingWrites.add(writeBuffer);

        if (selector != null) {
            selector.wakeup();
        }
    }

    private void doWrite(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            for (; ; ) {
                ByteBuffer writeBuffer = pendingWrites.peek();
                if (writeBuffer == null) break;

                sc.write(writeBuffer);
                if (writeBuffer.hasRemaining()) {
                    break;
                }
                pendingWrites.poll();
            }
        } catch (IOException e) {
            key.cancel();
            sc.close();
            return;
        }

        if (pendingWrites.isEmpty() && key.isValid()) {
            int ops = (key.interestOps() & ~SelectionKey.OP_WRITE) | SelectionKey.OP_READ;
            key.interestOps(ops);
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        int read = sc.read(readBuffer);
        if (read == -1) {
            key.cancel();
            sc.close();
            return;
        }
        readBuffer.flip();
        for (; ; ) {
            if (readBuffer.remaining() < 4) break;
            readBuffer.mark();
            int length = readBuffer.getInt();
            if (readBuffer.remaining() < length) {
                readBuffer.reset();
                break;
            }
            byte[] messageBytes = new byte[length];
            readBuffer.get(messageBytes);
            String jsonPayload = new String(messageBytes, StandardCharsets.UTF_8);

            gamePanel.processMessage(json.fromJson(jsonPayload, Message.class));
        }
        readBuffer.compact();
    }


    private void finishConnect(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.finishConnect()) {
            log.debug("Connected to server");

            int ops = SelectionKey.OP_READ;
            if (!pendingWrites.isEmpty()) {
                ops |= SelectionKey.OP_WRITE;
            }
            key.interestOps(ops);


            Message message = new JoinMessage(clientId, chosenClass, playerName);
            sendLocalInput(json.toJson(message, labelPair(Message.JSON_LABEL, "join")));
        } else {
            key.cancel();
        }
    }
}
