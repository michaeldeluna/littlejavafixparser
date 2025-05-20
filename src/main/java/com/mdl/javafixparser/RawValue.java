package com.mdl.javafixparser;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RawValue implements CharSequence {
    public static final char SOH = '\u0001';

    private byte[] data;
    public int start;
    public int end;

    public RawValue(byte[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    public void set(byte[] data, int tagStart, int tagEnd) {
        this.data = data;
        this.start = tagStart;
        this.end = tagEnd;
    }

    public int asInt() {
        if (end < start)
            return 0;

        int val = data[start] - '0';
        for (int i = start + 1; i <= end; i++)
            val = val * 10 + data[i] - '0';

        return val;
    }

    public char asChar() {
        if (end < start)
            return SOH;

        return (char) data[start];
    }

    public CharSequence asCharSequence() {
        return this;
    }

    public char charAt(int index) {
        int ix = index + start;
        if (ix >= end + 1)
            throw new StringIndexOutOfBoundsException(index);

        return (char)data[ix];
    }

    public int length() {
        return end + 1 - start;
    }

    public CharSequence subSequence(int newStart, int newEnd) {
        if (newStart < 0 || newEnd > length())
            throw new IndexOutOfBoundsException();

        // need a better impl (pooling?) to not create garbage
        return new RawValue(data, start + newStart, start + newEnd);
    }

    public float asFloat() {
        if (end < start)
            return 0f;

        int divisor = 0;
        int val = data[start] - '0';
        for (int i = start + 1; i <= end; i++)
            if (Character.isDigit(data[i])) {
                val = val * 10 + data[i] - '0';
                divisor *= 10;
            } else if (data[i] == '.')
                divisor = 1;

        if (divisor == 0)
            return val;

        return (float) val / divisor;
    }

    // need a better impl (pooling?) to not create garbage
    public String toString() {
        return new String(data, start, end - start + 1, UTF_8);
    }
}
