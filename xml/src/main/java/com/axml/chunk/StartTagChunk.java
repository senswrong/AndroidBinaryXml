package com.axml.chunk;

import android.util.TypedValue;
import com.axml.chunk.base.BaseContentChunk;
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
public class StartTagChunk extends BaseContentChunk {
    public final int namespaceUri;
    public final int name;
    public final int flags;
    public int attributeCount;
    public final int classAttribute;
    public List<Attribute> attributes;

    private final StringChunk stringChunk;
    private final List<NamespaceChunk> namespaceChunkList;

    public StartTagChunk(ByteBuffer byteBuffer, StringChunk stringChunk, List<NamespaceChunk> namespaceChunkList) {
        super(byteBuffer);
        namespaceUri = byteBuffer.getInt();
        name = byteBuffer.getInt();
        flags = byteBuffer.getInt();
        attributeCount = byteBuffer.getInt();
        classAttribute = byteBuffer.getInt();

        attributes = new ArrayList<>(attributeCount);
        for (int i = 0; i < attributeCount; i++)
            attributes.add(new Attribute(byteBuffer));

        this.stringChunk = stringChunk;
        this.namespaceChunkList = namespaceChunkList;
    }

    public static class Attribute {
        public final int namespaceUri;
        public final int name;
        public final int value;
        public final int type;
        public final int data;

        public Attribute(ByteBuffer byteBuffer) {
            namespaceUri = byteBuffer.getInt();
            name = byteBuffer.getInt();
            value = byteBuffer.getInt();
            type = byteBuffer.getInt() >> 24;
            data = byteBuffer.getInt();
        }
        
        protected void toBytes(ByteArrayOutputStream stream) throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 4);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(namespaceUri);
            byteBuffer.putInt(name);
            byteBuffer.putInt(value);
            byteBuffer.putInt(type << 24);
            byteBuffer.putInt(data);
            stream.write(byteBuffer.array());
        }
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        super.toBytes(stream);
        this.attributeCount = attributes.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(namespaceUri);
        byteBuffer.putInt(name);
        byteBuffer.putInt(flags);
        byteBuffer.putInt(attributeCount);
        byteBuffer.putInt(classAttribute);
        stream.write(byteBuffer.array());
        for (Attribute attribute : attributes)
            attribute.toBytes(stream);
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
        if (stringChunk == null || namespaceChunkList == null) return "";
        StringBuilder tagBuilder = new StringBuilder("<");
        //always = -1
        if (namespaceUri != -1) tagBuilder.append(getPrefix(namespaceUri)).append(":");

        String tagName = getString(name);
        tagBuilder.append(tagName);
        if ("manifest".equals(tagName))//add namespace
            for (NamespaceChunk namespaceChunk : namespaceChunkList)
                tagBuilder.append(" ").append("xmlns:").append(getString(namespaceChunk.prefix)).append("=\"").append(getString(namespaceChunk.uri)).append('"');

        for (Attribute attribute : attributes) {
            tagBuilder.append(" ");
            if (attribute.namespaceUri != -1) tagBuilder.append(getPrefix(attribute.namespaceUri)).append(":");
            String data = TypedValue.coerceToString(attribute.type, attribute.data);
            tagBuilder.append(getString(attribute.name))
                    .append("=")
                    .append('"')
                    .append(StringUtils.isEmpty(data) ? getString(attribute.value) : data)
                    .append('"');
        }
        return tagBuilder.append(">\n").toString();
    }
}
