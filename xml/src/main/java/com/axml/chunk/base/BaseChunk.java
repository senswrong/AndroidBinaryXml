package com.axml.chunk.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Sens on 2021/8/27.
 */
public abstract class BaseChunk {
    public final int ChunkStartPosition;
    public final int chunkType;
    public int chunkSize;

    public BaseChunk(ByteBuffer byteBuffer) {
        byteBuffer.position(byteBuffer.position() - 4);
        ChunkStartPosition = byteBuffer.position();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.chunkType = byteBuffer.getInt();
        this.chunkSize = byteBuffer.getInt();
    }

    protected abstract void toBytes(ByteArrayOutputStream stream) throws IOException;

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        toBytes(stream);
        byte[] bytes = stream.toByteArray();
        this.chunkSize = bytes.length + 8;
        ByteBuffer byteBuffer = ByteBuffer.allocate(8 + bytes.length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(this.chunkType);
        byteBuffer.putInt(this.chunkSize);
        byteBuffer.put(bytes);
        return byteBuffer.array();
    }

    @Override
    public String toString() {
        return "";
    }
}
