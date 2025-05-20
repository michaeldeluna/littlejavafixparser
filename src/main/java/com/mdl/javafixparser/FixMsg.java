package com.mdl.javafixparser;

public interface FixMsg {
    boolean isSet(int tag);

    int getInt(int tag);
    int getInt(int tag, int group);

    char getChar(int tag);
    char getChar(int tag, int group);

    CharSequence getCharSeq(int tag);
    CharSequence getCharSeq(int tag, int group);

    float getFloat(int tag);
    float getFloat(int tag, int group);

    boolean getBool(int tag);
}
