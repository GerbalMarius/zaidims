package org.game.server;

import org.game.json.Json;
import org.game.message.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.filtering;
import static org.game.json.JsonLabelPair.labelPair;

public class Server {

    private static final int PORT = 9000;
    private Selector selector;
    private ServerSocketChannel serverChannel;

    private final Map<SocketChannel, ClientState> clients = new LinkedHashMap<>();

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
        System.out.println("Server started on port: " + PORT);

        for (;;){

            if (selector.select() == 0) {
                continue;
            }
            var keys = selector.selectedKeys().iterator();
            while (keys.hasNext()){
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
        System.out.println("Accepted  from : " + sc.getRemoteAddress());
        System.out.println(sc.hashCode());
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
        System.out.println("From " + from.getRemoteAddress() + ": " + message.toString());

        switch (message) {
            case JoinMessage(String playerId, String playerName, int _, int _) -> {
                state.setId(playerId);

                state.setName(playerName);

                Collection<ClientState> states = clients.values();

                int count = states.stream()
                        .collect(filtering(cs -> cs.getId() != null, counting()))
                        .intValue();

                //setting rndm starting pos
                state.setX(100 + (count % 10) * 60);
                state.setY(100 + (count / 10) * 60);

                for (ClientState other : states) {
                    if (other.getId() != null && state != other) {
                        var join = new JoinMessage(other.getId(), other.getName(), other.getX(), other.getY());
                        sendTo(from, json.toJson(join, labelPair(Message.JSON_LABEL, "join")));
                    }
                }

                JoinMessage join = new JoinMessage(state.getId(), state.getName(), state.getX(), state.getY());
                broadcast(json.toJson(join, labelPair(Message.JSON_LABEL, "join")));
            }
            case MoveMessage(String id, int dx, int dy) ->  {
                if (state.getId() == null || !state.getId().equals(id)) {
                    System.out.println("Spoofed MOVE ignored");
                    return;
                }
                state.setX(state.getX() + dx);
                state.setY(state.getY() + dy);

                MoveMessage move = new MoveMessage(id, state.getX(), state.getY());

                broadcast(json.toJson(move, labelPair(Message.JSON_LABEL, "move")));
            }
            case LeaveMessage leaveMessage -> broadcast(json.toJson(leaveMessage, labelPair(Message.JSON_LABEL, "leave")));
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

    private void closeKey(SelectionKey key){
        SocketChannel sc = (SocketChannel) key.channel();
        ClientState cs = (ClientState) key.attachment();

        String remote = "";
        try {
            remote = sc.getRemoteAddress().toString();
        } catch (IOException e) {
            System.err.println("ERROR GETTING REMOTE ADDRESS");
        }
        System.out.println("Closing " + remote);

        if (cs != null && cs.getId() != null) {
            LeaveMessage leaveMessage = new LeaveMessage(cs.getId());
            broadcast(json.toJson(leaveMessage, labelPair(Message.JSON_LABEL, "leave")));
        }

        clients.remove(sc);
        try {
            sc.close();
        }
        catch (IOException e) {
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
}
