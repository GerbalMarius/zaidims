package org.game.server;

import lombok.Getter;
import lombok.Setter;
import org.game.entity.ClassType;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

@Getter
@Setter
public final class ClientState {

    private  final ByteBuffer readBuffer = ByteBuffer.allocate(8 * 1024);
    private boolean isReading = false;
    private  int messageLength = 0;
    private final Queue<ByteBuffer> writeQueue = new ArrayDeque<>();

    private UUID id;
    private String name;
    private ClassType playerClass;
    private int x = 0;// server-owned position
    private  int y = 0;

    public void enqueueWrite(ByteBuffer b) {
        writeQueue.add(b);
    }
    public boolean noMoreMessages() {
        return writeQueue.isEmpty();
    }

    public void pollMessage() {
        writeQueue.poll();
    }

    public ByteBuffer peekMessage() {
        return  writeQueue.peek();
    }

}