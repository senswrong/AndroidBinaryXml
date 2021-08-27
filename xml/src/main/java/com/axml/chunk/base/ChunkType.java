package com.axml.chunk.base;

/**
 * Created by Sens on 2021/8/27.
 */
public enum ChunkType {
    CHUNK_STRING            /*chunk type*/(0x001C0001),
    CHUNK_RESOURCE          /*chunk type*/(0x00080180),
    CHUNK_START_NAMESPACE   /*chunk type*/(0x00100100),
    CHUNK_END_NAMESPACE     /*chunk type*/(0x00100101),
    CHUNK_START_TAG         /*chunk type*/(0x00100102),
    CHUNK_END_TAG           /*chunk type*/(0x00100103),
    ;
    public final int TYPE;

    ChunkType(int TYPE) {
        this.TYPE = TYPE;
    }

    public static ChunkType valueOf(int TYPE) {
        for (ChunkType value : ChunkType.values())
            if (value.TYPE == TYPE) return value;
        return null;
    }
}
