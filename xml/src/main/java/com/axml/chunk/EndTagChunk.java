package com.axml.chunk;

import com.axml.chunk.base.BaseContentChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Created by Sens on 2021/8/27.
 */
public class EndTagChunk extends BaseContentChunk {
    public final int namespaceUri;
    public final int name;

    private final StringChunk stringChunk;
    private final List<NamespaceChunk> namespaceChunkList;

    public EndTagChunk(ByteBuffer byteBuffer, StringChunk stringChunk, List<NamespaceChunk> namespaceChunkList) {
        super(byteBuffer);
        namespaceUri = byteBuffer.getInt();
        name = byteBuffer.getInt();

        this.stringChunk = stringChunk;
        this.namespaceChunkList = namespaceChunkList;
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        super.toBytes(stream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(this.namespaceUri);
        byteBuffer.putInt(this.name);
        stream.write(byteBuffer.array());
    }

    private String getPrefix(int uri) {
        for (NamespaceChunk namespaceChunk : namespaceChunkList)
            if (namespaceChunk.uri == uri)
                return getString(namespaceChunk.prefix);
        return null;
    }

    public String getString(int index) {
        if (index == -1) return "";
        return stringChunk.getString(index);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("</");
        //always = -1
        if (namespaceUri != -1) stringBuilder.append(getPrefix(namespaceUri)).append(":");
        stringBuilder.append(getString(name)).append(">\n");
        return stringBuilder.toString();
    }
}
