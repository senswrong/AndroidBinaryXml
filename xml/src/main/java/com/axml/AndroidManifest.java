package com.axml;

import com.axml.chunk.*;
import com.axml.chunk.base.BaseChunk;
import com.axml.chunk.base.ChunkType;
import common.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sens on 2021/8/27.
 * {@see <a href="https://github.com/senswrong/AndroidManifest">AndroidManifest</a>}
 */
public class AndroidManifest {
    public short fileType;
    public short headerSize;
    public int fileSize;
    public StringChunk stringChunk;
    public ResourceChunk resourceChunk;
    public List<BaseChunk> structList = new ArrayList();

    public AndroidManifest(File androidManifest) {
        this(FileUtils.getFileData(androidManifest));
    }

    public AndroidManifest(byte[] datas) {
        int available = datas.length;
        ByteBuffer byteBuffer = ByteBuffer.wrap(datas);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        fileType = byteBuffer.getShort();
        headerSize = byteBuffer.getShort();
        fileSize = byteBuffer.getInt();
        List<NamespaceChunk> namespaceChunkList = new ArrayList<>();
        while (byteBuffer.position() < available) {
            short Type = byteBuffer.getShort();
            ChunkType chunkType = ChunkType.valueOf(Type);
            if (chunkType == null) break;
            switch (chunkType) {
                case CHUNK_STRING:
                    stringChunk = new StringChunk(byteBuffer);
                    break;
                case CHUNK_RESOURCE:
                    resourceChunk = new ResourceChunk(byteBuffer);
                    break;
                case CHUNK_START_NAMESPACE:
                    NamespaceChunk namespaceChunk = new NamespaceChunk(byteBuffer, stringChunk);
                    namespaceChunkList.add(namespaceChunk);
                    structList.add(namespaceChunk);
                    break;
                case CHUNK_START_TAG:
                    structList.add(new StartTagChunk(byteBuffer, stringChunk, namespaceChunkList));
                    break;
                case CHUNK_END_TAG:
                    structList.add(new EndTagChunk(byteBuffer, stringChunk));
                    break;
                case CHUNK_END_NAMESPACE:
                    structList.add(new NamespaceChunk(byteBuffer, stringChunk));
                    break;
            }
        }
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (stringChunk != null) stream.write(stringChunk.toBytes());
        if (resourceChunk != null) stream.write(resourceChunk.toBytes());
        for (BaseChunk chunk : structList)
            stream.write(chunk.toBytes());
        fileSize = 8 + stream.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(fileType);
        byteBuffer.putShort(headerSize);
        byteBuffer.putInt(fileSize);
        byteBuffer.put(stream.toByteArray());
        return byteBuffer.array();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BaseChunk baseChunk : structList)
            sb.append(baseChunk);
        return sb.toString();
    }
}
