package org.game.client;

import org.game.Json;
import org.game.message.JoinMessage;
import org.game.message.MessageType;
import org.game.message.MoveMessage;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

public class Client {
    private static final int PORT = 9000;

    private final String clientId =  UUID.randomUUID().toString();
    private String playerName = "";

    private SocketChannel socketChannel;
    private Selector selector;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 8);// 8 KB
    private final Queue<ByteBuffer> pendingWrites = new ConcurrentLinkedQueue<>();

    private final GameState gameState = new GameState();
    private GamePanel gamePanel;

    public static void main(String[] args) {
        new Client().createClientGui();
    }

    private void createClientGui(){
        String name = JOptionPane.showInputDialog(null, "Enter player name:", "Choose name", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) playerName = name.trim();
        else playerName = "Player " + clientId.substring(0, 10);

        // UI
        JFrame frame = new JFrame("Game");
        gamePanel = new GamePanel(gameState);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setSize(600, 400);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        gamePanel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {

                    case KeyEvent.VK_LEFT -> {
                        int dx = -5, dy = 0;
                        MoveMessage moveMessage = new MoveMessage(clientId, dx, dy);

                        optimisticMove(dx, dy);
                        sendLocalInput(Json.getInstance().toJson(moveMessage));
                    }
                    case KeyEvent.VK_RIGHT -> {
                        int dx = 5, dy = 0;
                        MoveMessage moveMessage = new MoveMessage(clientId, dx, dy);

                        optimisticMove(dx, dy);
                        sendLocalInput(Json.getInstance().toJson(moveMessage));
                    }
                    case KeyEvent.VK_UP -> {
                        int dx = 0, dy = -5;
                        MoveMessage moveMessage = new MoveMessage(clientId, dx, dy);

                        sendLocalInput(Json.getInstance().toJson(moveMessage));
                    }
                    case KeyEvent.VK_DOWN -> {
                        int dx = 0, dy = 5;
                        MoveMessage moveMessage = new MoveMessage(clientId, dx, dy);
                        sendLocalInput(Json.getInstance().toJson(moveMessage));
                    }
                }
            }
        });

        startNetworkConnection();
    }


    private void optimisticMove(int dx, int dy) {
        SwingUtilities.invokeLater(() -> {
            if (gameState.hasPlayer(clientId)) {
                gameState.movePlayerBy(clientId, dx, dy);
                gamePanel.repaint();
            } else {
                System.out.println("Optimistic move ignored: player not yet present in gameState");
            }
        });
    }
    private void startNetworkConnection(){
        var threadBuilder = Thread.ofVirtual()
                .name("ClientThread : " + clientId);

        threadBuilder.start(this::openConnection);
    }

    private void openConnection(){
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();

            socketChannel.configureBlocking(false);


            socketChannel.connect(new InetSocketAddress(PORT));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    selector.select(500);
                } catch (ClosedSelectorException cse){
                    break;
                }
                var selectedKeys = selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) continue;

                    if (key.isConnectable()) {
                        finishConnect(key);
                    }
                   else if (key.isReadable()) {
                        doRead(key);
                    }
                   else if (key.isWritable()) {
                        doWrite(key);
                    }
                }
                SelectionKey key = (socketChannel != null) ? socketChannel.keyFor(selector) : null;
                if (key != null && key.isValid() && !pendingWrites.isEmpty()) {
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
            try {
                if (socketChannel != null) socketChannel.close();
                if (selector != null) selector.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
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
            for (;;) {
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
        for(;;) {
            if (readBuffer.remaining() < 4) break;
            readBuffer.mark();
            int length = readBuffer.getInt();
            if (readBuffer.remaining() < length) {
                readBuffer.reset();
                break;
            }
            byte[] messageBytes = new byte[length];
            readBuffer.get(messageBytes);
            String json = new String(messageBytes, StandardCharsets.UTF_8);

            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            onServerMessage(jsonObject);
        }
        readBuffer.compact();
    }

    private void onServerMessage(final JsonObject json) {

        SwingUtilities.invokeLater(() -> {
            MessageType type = MessageType.valueOf(json.get("type").getAsString());
            switch (type) {
                case JOIN -> {
                    String playerId = json.get("playerId").getAsString();
                    String playerName = json.get("playerName").getAsString();
                    int x = json.get("startPosX").getAsInt();
                    int y = json.get("startPosY").getAsInt();
                    gameState.addPlayer(playerId, playerName, x, y);
                }
                case LEAVE -> {
                    String playerId = json.get("playerId").getAsString();
                    gameState.removePlayer(playerId);
                }
                case MOVE -> {
                    String playerId = json.get("playerId").getAsString();
                    int x = json.get("newX").getAsInt();
                    int y = json.get("newY").getAsInt();
                    gameState.setPlayerPosition(playerId, x, y);
                }
            }

            gamePanel.repaint();
        });

    }

    private void finishConnect(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        if (sc.finishConnect()) {
            System.out.println("Connected to server");

            int ops = SelectionKey.OP_READ;
            if (!pendingWrites.isEmpty()) {
                ops |= SelectionKey.OP_WRITE;
            }
            key.interestOps(ops);

            // send JOIN
            JoinMessage message = new JoinMessage(clientId, playerName);
            sendLocalInput(Json.getInstance().toJson(message));
        } else {
            key.cancel();
        }
    }
}
