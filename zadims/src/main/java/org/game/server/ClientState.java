package org.game.server;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

class ClientState {

    private  final ByteBuffer readBuffer = ByteBuffer.allocate(8 * 1024);
    private boolean isReading = false;
    private  int messageLength = 0;
    private final Queue<ByteBuffer> writeQueue = new ArrayDeque<>();

    private  String id;
    private String name;
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

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public boolean isReading() {
        return isReading;
    }
    public void setReading(boolean reading) {
        this.isReading = reading;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
}
