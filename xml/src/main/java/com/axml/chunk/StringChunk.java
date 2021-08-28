package com.axml.chunk;

import com.axml.chunk.base.BaseChunk;
import common.utils.StringUtils;

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
    public int stringCount;
    public int styleCount;
    public boolean isUTF8;
    public boolean isSorted;
    public int stringStart;
    public int styleStart;

    public int[] stringOffsets;
    public int[] styleOffsets;

    public List<String> stringList;

    public StringChunk(ByteBuffer byteBuffer) {
        super(byteBuffer);
        this.stringCount = byteBuffer.getInt();
        this.styleCount = byteBuffer.getInt();
        this.isUTF8 = byteBuffer.getShort() == 1;
        this.isSorted = byteBuffer.getShort() == 1;
        this.stringStart = byteBuffer.getInt();
        this.styleStart = byteBuffer.getInt();

        stringOffsets = new int[stringCount];
        for (int i = 0; i < stringOffsets.length; i++)
            stringOffsets[i] = byteBuffer.getInt();

        styleOffsets = new int[styleCount];
        for (int i = 0; i < styleOffsets.length; i++)
            styleOffsets[i] = byteBuffer.getInt();

        stringList = new ArrayList<>(stringCount);

        for (int i = 0; i < stringCount; i++) {
            byteBuffer.position(ChunkStartPosition + stringStart + stringOffsets[i]);
            char length = byteBuffer.getChar();
            char[] string = new char[length];
            for (char j = 0; j < length; j++)
                string[j] = byteBuffer.getChar();
            stringList.add(new String(string));
        }
        //styleCount always = 0  [skip]
        byteBuffer.position(ChunkStartPosition + chunkSize);
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

    public String getString(int index) {
        return stringList.get(index);
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        stringCount = stringList.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 4 + stringOffsets.length * 4 + styleOffsets.length * 4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(stringCount);
        byteBuffer.putInt(styleCount);
        byteBuffer.putShort((short) (isUTF8 ? 1 : 0));
        byteBuffer.putShort((short) (isSorted ? 1 : 0));
        byteBuffer.putInt(stringStart);
        byteBuffer.putInt(styleStart);
        int stringOffset = 0;
        if (stringOffsets.length != stringCount)
            stringOffsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            stringOffsets[i] = stringOffset;
            byteBuffer.putInt(stringOffset);
            stringOffset += 4 + stringList.get(i).length() * 2;
        }
        //styleCount always = 0  [skip]
        for (int offset : styleOffsets) byteBuffer.putInt(offset);
        stream.write(byteBuffer.array());
        for (String str : stringList)
            stringToBytes(stream, str);
    }
}
