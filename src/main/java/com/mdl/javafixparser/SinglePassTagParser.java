package com.mdl.javafixparser;

public class SinglePassTagParser {
    public static final byte EQUALS = '=';
    public static final byte SOH = 1; // FIX field delimiter

    public interface Listener {
        void init();
        void onTagOffsets(byte[] data, int tagStart, int tagEnd, int valueStart, int valueEnd);
    }

    public void parse(byte[] bytes, Listener listener) {
        listener.init();

        int state = 0; // 0=seek tag start, 1=in tag, 2=in value
        int tagStart = -1;
        int valueStart = -1;

        for (int i = 0; i < bytes.length; i++) {
            byte current = bytes[i];

            switch (state) {
                case 0: // Seeking tag start
                    if (Character.isDigit((char) current)) {
                        tagStart = i;
                        state = 1;
                    }
                    break;

                case 1: // Reading tag
                    if (current == EQUALS) {
                        valueStart = i + 1;
                        state = 2;
                    } else if (!Character.isDigit((char) current)) {
                        // Invalid tag character, reset
                        state = 0;
                        tagStart = -1;
                    }
                    break;

                case 2: // Reading value
                    if (current == SOH) {
                        // Tag ends just before '='
                        int tagEnd = valueStart - 2; // '=' is at valueStart - 1
                        int valueEnd = i - 1;

                        listener.onTagOffsets(bytes, tagStart, tagEnd, valueStart, valueEnd);

                        state = 0;
                        tagStart = -1;
                        valueStart = -1;
                    }
                    break;
            }
        }
    }
}
