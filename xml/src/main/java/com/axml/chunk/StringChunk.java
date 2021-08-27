package com.axml.chunk;

import com.axml.chunk.base.BaseChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sens on 2021/8/27.
 */
public class StringChunk extends BaseChunk {
    public final int stringCount;
    public final int styleCount;
    public final int unknow;
    public final int stringPoolOffset;
    public final int stylePoolOffset;

    public final int[] stringOffsets;
    public final int[] styleOffsets;

    public final List<String> stringList;

    public String getString(int index) {
        return stringList.get(index);
    }

    public StringChunk(ByteBuffer byteBuffer) {
        super(byteBuffer);
        this.stringCount = byteBuffer.getInt();
        this.styleCount = byteBuffer.getInt();
        this.unknow = byteBuffer.getInt();
        this.stringPoolOffset = byteBuffer.getInt();
        this.stylePoolOffset = byteBuffer.getInt();

        stringOffsets = new int[stringCount];
        for (int i = 0; i < stringOffsets.length; i++)
            stringOffsets[i] = byteBuffer.getInt();

        styleOffsets = new int[styleCount];
        for (int i = 0; i < styleOffsets.length; i++)
            styleOffsets[i] = byteBuffer.getInt();

        stringList = new ArrayList<>(stringCount);

        for (int i = 0; i < stringCount; i++) {
            byteBuffer.position(ChunkStartPosition + stringPoolOffset + stringOffsets[i]);
            char strLength = byteBuffer.getChar();
            char[] strs = new char[strLength];
            for (char j = 0; j < strLength; j++)
                strs[j] = byteBuffer.getChar();
            stringList.add(new String(strs));
        }
        //final separator
        byteBuffer.getChar();
        //styleCount always = 0  break
    }

    private void stringToBytes(ByteArrayOutputStream stream, String str) throws IOException {
        if (str == null) return;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + str.length() * 2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putChar((char) str.length());
        char[] charArray = str.toCharArray();
        for (char c : charArray)
            byteBuffer.putChar(c);
        byteBuffer.putChar('\0');
        stream.write(byteBuffer.array());
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 4 + stringOffsets.length * 4 + styleOffsets.length * 4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(stringList.size());
        byteBuffer.putInt(styleCount);
        byteBuffer.putInt(unknow);
        byteBuffer.putInt(stringPoolOffset);
        byteBuffer.putInt(stylePoolOffset);
        for (int offset : stringOffsets) byteBuffer.putInt(offset);
        for (int offset : styleOffsets) byteBuffer.putInt(offset);
        stream.write(byteBuffer.array());
        for (String str : stringList)
            stringToBytes(stream, str);
    }
}
