package com.mdl.javafixparser;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class FixTagAccumulator implements SinglePassTagParser.Listener, FixMsg {

    public static final int MAX_TAG_VALUE = 1000;

    private final TagOffset[] tags;
    private final Links links;
    private int offsetCount = 0;

    FixTagAccumulator(int capacity) {
        this.tags = new TagOffset[capacity];
        for (int i = 0; i < tags.length; i++)
            tags[i] = new TagOffset();

        this.links = new Links(capacity, MAX_TAG_VALUE);
    }

    public void init() {
        offsetCount = 0;
        links.clear();
    }

    public void onTagOffsets(byte[] data, int tagStart, int tagEnd, int valueStart, int valueEnd) {
        TagOffset t = tags[offsetCount];
        ++offsetCount;

        t.init(data, tagStart, tagEnd, valueStart, valueEnd);

        int tag = t.tag.asInt();
        links.put(tag, t);
    }

    public boolean isSet(int tag) {
        return links.contains(tag);
    }

    public int getInt(int tag) {
        return getInt(tag, 0);
    }

    public int getInt(int tag, int group) {
        return rawValue(tag, group).asInt();
    }

    public char getChar(int tag) {
        return getChar(tag, 0);
    }

    public char getChar(int tag, int group) {
        return rawValue(tag, group).asChar();
    }

    public CharSequence getCharSeq(int tag) {
        return getCharSeq(tag, 0);
    }

    public CharSequence getCharSeq(int tag, int group) {
        return rawValue(tag, group).asCharSequence();
    }

    public float getFloat(int tag) {
        return getFloat(tag, 0);
    }

    public float getFloat(int tag, int group) {
        return rawValue(tag, group).asFloat();
    }

    public boolean getBool(int tag) {
        return getChar(tag) == 'Y';
    }

    private RawValue rawValue(int tag, int group) {
        return links.get(tag, group).tagOffset.value;
    }

    public String paddedString() {
        return Arrays.stream(tags)
                     .limit(offsetCount)
                     .map(TagOffset::paddedString)
                     .sorted()
                     .collect(joining("\n"));
    }
}
