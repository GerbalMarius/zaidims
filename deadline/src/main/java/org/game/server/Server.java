package org.game.server;

import org.game.entity.ClassType;
import org.game.entity.Enemy;
import org.game.json.Json;
import org.game.message.*;
import org.game.server.spawner.EnemySpawnManager;
import org.game.server.spawner.EnemySpawner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.game.json.JsonLabelPair.labelPair;
import static org.game.server.Server.ServerActions.*;

public final class Server {

    private static final int PORT = 9000;
    private Selector selector;
    private ServerSocketChannel serverChannel;

    private final Map<SocketChannel, ClientState> clients = new LinkedHashMap<>();

     static int enemyId = 0;
     static int spawnCount = 0;

     private  final  EnemySpawnManager spawnManager = new EnemySpawnManager(this);


    private final Json json = new Json();


    static void main() throws IOException {
        new Server().start();
    }

    public void start() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();

        serverChannel.configureBlocking(false);

        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        IO.println("Server started on port: " + PORT);

        for (; ; ) {

            if (selector.select() == 0) {
                continue;
            }
            var keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                try {
                    if (key.isAcceptable()) accept();
                    else if (key.isReadable()) read(key);
                    else if (key.isWritable()) write(key);
                } catch (IOException e) {
                    closeKey(key);
                    e.printStackTrace();
                }
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel sc = serverChannel.accept();
        sc.configureBlocking(false);
        sc.socket().setTcpNoDelay(true);


        SelectionKey sk = sc.register(selector, SelectionKey.OP_READ);
        ClientState cs = new ClientState();
        sk.attach(cs);
        clients.put(sc, cs);
        IO.println("Accepted  from : " + sc.getRemoteAddress());

        if (spawnCount == 0) {
            spawnManager.startSpawning(0, 5, TimeUnit.SECONDS);
        }
        spawnCount++;
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();

        ByteBuffer readBuffer = state.getReadBuffer();


        int read = sc.read(readBuffer);
        if (read == -1) { // client closed
            closeKey(key);
            return;
        }

        readBuffer.flip();
        while (true) {
            int messageLength;

            if (!state.isReading()) {
                if (readBuffer.remaining() >= 4) {
                    messageLength = readBuffer.getInt();

                    state.setMessageLength(messageLength);
                    state.setReading(true);


                    if (messageLength <= 0 || messageLength > 10_000_000) {
                        closeKey(key);
                        return;
                    }
                } else {
                    break;
                }
            }

            messageLength = state.getMessageLength();
            if (readBuffer.remaining() >= messageLength) {
                byte[] msgBytes = new byte[messageLength];
                readBuffer.get(msgBytes);
                String message = new String(msgBytes, StandardCharsets.UTF_8);

                onMessage(sc, json.fromJson(message, Message.class));

                state.setReading(false);
                state.setMessageLength(0);
            } else {
                break;
            }
        }
        readBuffer.compact();
    }

    private void onMessage(SocketChannel from, Message message) throws IOException {
        ClientState state = clients.get(from);
        IO.println("From " + from.getRemoteAddress() + ": " + message.toString());

        switch (message) {
            case JoinMessage(UUID playerId, ClassType playerClass, String playerName, int _, int _) ->
                    createPlayer(from, this, playerId, playerClass, playerName, state);
            case MoveMessage(UUID id, int dx, int dy) -> movePlayer(id, this, dx, dy, state);
            case LeaveMessage leaveMessage -> broadcast(json.toJson(leaveMessage, labelPair(Message.JSON_LABEL, "leave")));
            case EnemyMoveMessage enemyMoveMessage -> {

            }
            case EnemyRemoveMessage enemyRemoveMessage -> {
            }
            case EnemySpawnMessage enemySpawnMessage -> {
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientState cs = (ClientState) key.attachment();


        while (!cs.noMoreMessages()) {
            ByteBuffer bb = cs.peekMessage();
            sc.write(bb);
            if (bb.hasRemaining()) {
                break;
            }
            cs.pollMessage();
        }

        if (cs.noMoreMessages()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        }
    }


    private void sendTo(SocketChannel sc, String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + bytes.length);
        buf.putInt(bytes.length);
        buf.put(bytes);
        buf.flip();

        ClientState cs = clients.get(sc);
        if (cs == null) return;


        ByteBuffer copy = ByteBuffer.allocate(buf.remaining());
        copy.put(buf);
        copy.flip();
        cs.enqueueWrite(copy);

        SelectionKey key = sc.keyFor(selector);
        if (key != null && key.isValid()) {
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
    }

    private void closeKey(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ClientState cs = (ClientState) key.attachment();

        String remote = "";
        try {
            remote = sc.getRemoteAddress().toString();
        } catch (IOException e) {
            System.err.println("ERROR GETTING REMOTE ADDRESS");
        }
        IO.println("Closing " + remote);

        if (cs != null && cs.getId() != null) {
            LeaveMessage leaveMessage = new LeaveMessage(cs.getId());
            broadcast(json.toJson(leaveMessage, labelPair(Message.JSON_LABEL, "leave")));
        }

        clients.remove(sc);
        try {
            sc.close();
        } catch (IOException e) {
            System.err.println("Error closing client " + remote);
        }
        key.cancel();
    }

    private void broadcast(String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(4 + bytes.length);
        buf.putInt(bytes.length);
        buf.put(bytes);
        buf.flip();

        for (var clientSockets : clients.entrySet()) {
            SocketChannel sc = clientSockets.getKey();
            ClientState cs = clientSockets.getValue();


            //Write a message copy for each client
            ByteBuffer copy = ByteBuffer.allocate(buf.remaining());
            copy.put(buf.slice());
            copy.flip();
            cs.enqueueWrite(copy);
            SelectionKey key = sc.keyFor(selector);
            if (key != null && key.isValid()) {
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            }
        }
    }

    public final static class ServerActions {

        private ServerActions() {
        }


        public static void createPlayer(SocketChannel from,
                                        Server server,
                                        UUID playerId,
                                        ClassType playerClass,
                                        String playerName,
                                        ClientState state) {
            state.setId(playerId);
            state.setPlayerClass(playerClass);
            state.setName(playerName);

            Collection<ClientState> states = server.clients.values();

            state.setX(WorldSettings.TILE_SIZE * 23);
            state.setY(WorldSettings.TILE_SIZE * 21);

            for (ClientState other : states) {
                if (other.getId() != null && state != other) {
                    var join = new JoinMessage(other.getId(), other.getPlayerClass(), other.getName(), other.getX(), other.getY());
                    server.sendTo(from, server.json.toJson(join, labelPair(Message.JSON_LABEL, "join")));
                }
            }

            JoinMessage join = new JoinMessage(state.getId(), state.getPlayerClass(), state.getName(), state.getX(), state.getY());
            server.broadcast(server.json.toJson(join, labelPair(Message.JSON_LABEL, "join")));
        }


        public static void movePlayer(UUID id, Server server, int dx, int dy, ClientState state) {
            if (state.getId() == null || !state.getId().equals(id)) {
                IO.println("Spoofed MOVE ignored");
                return;
            }
            state.setX(state.getX() + dx);
            state.setY(state.getY() + dy);

            MoveMessage move = new MoveMessage(id, state.getX(), state.getY());

            server.broadcast(server.json.toJson(move, labelPair(Message.JSON_LABEL, "move")));
        }

        public static void spawnEnemy(Server server, Enemy enemy, int startX, int startY) {


            EnemySpawnMessage spawnMessage = new EnemySpawnMessage(
                        enemyId++,
                        enemy.getType(),
                        enemy.getSize(),
                        startX,
                        startY
            );

            server.broadcast(server.json.toJson(spawnMessage, labelPair(Message.JSON_LABEL, "enemySpawn")));

        }
    }
}
