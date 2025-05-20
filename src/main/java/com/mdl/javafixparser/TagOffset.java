package com.mdl.javafixparser;

public class TagOffset {
    public final RawValue tag;
    public final RawValue value;

    public TagOffset() {
        this(new RawValue(null, -1, -1),
             new RawValue(null, -1, -1)
        );
    }

    public TagOffset(RawValue tag, RawValue value) {
        this.tag = tag;
        this.value = value;
    }

    public void init(byte[] data, int tagStart, int tagEnd, int valueStart, int valueEnd) {
        tag.set(data, tagStart, tagEnd);
        value.set(data, valueStart, valueEnd);
    }

    public String paddedString() {
        return String.format(
                "%3s = %s",
                tag.toString(),
                value.toString()
        );
    }
}
