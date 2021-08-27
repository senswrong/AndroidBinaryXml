package com.axml.chunk.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Sens on 2021/8/27.
 */
public class BaseContentChunk extends BaseChunk {
    public final int lineNumber;
    public final int unknown;

    public BaseContentChunk(ByteBuffer byteBuffer) {
        super(byteBuffer);
        lineNumber = byteBuffer.getInt();
        unknown = byteBuffer.getInt();
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(this.lineNumber);
        byteBuffer.putInt(this.unknown);
        stream.write(byteBuffer.array());
    }
}
