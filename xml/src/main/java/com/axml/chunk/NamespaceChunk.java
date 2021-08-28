package com.axml.chunk;

import com.axml.chunk.base.BaseContentChunk;
import com.axml.chunk.base.ChunkType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Sens on 2021/8/27.
 */
public class NamespaceChunk extends BaseContentChunk {
    public final int prefix;
    public final int uri;

    public NamespaceChunk(ByteBuffer byteBuffer, StringChunk stringChunk) {
        super(byteBuffer, stringChunk);
        this.prefix = byteBuffer.getInt();
        this.uri = byteBuffer.getInt();
        byteBuffer.position(ChunkStartPosition + chunkSize);
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        super.toBytes(stream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(this.prefix);
        byteBuffer.putInt(this.uri);
        stream.write(byteBuffer.array());
    }

    @Override
    public String toString() {
        if (chunkType == ChunkType.CHUNK_START_NAMESPACE.TYPE) {
            StringBuilder tagBuilder = new StringBuilder();
            if (comment > -1) tagBuilder.append("<!--").append(getString(comment)).append("-->").append("\n");
            tagBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            return tagBuilder.toString();
        }
        return "";
    }
}
