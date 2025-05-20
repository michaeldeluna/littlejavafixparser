package com.mdl.javafixparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

public class SimpleSlowTagParser {
    static final String soh = "\u0001";

    public FixMsg parse(byte[] bytes) {
        SimpleSlowFixMsg msg = new SimpleSlowFixMsg();

        String txt = new String(bytes, UTF_8);
        String[] split = txt.split(soh);

        for (String s : split) {
            int equalsAt = s.indexOf('=');

            String key = s.substring(0, equalsAt);
            String value = s.substring(equalsAt + 1);

            msg.tags
                    .computeIfAbsent(key, _ -> new ArrayList<>())
                    .add(value);
        }

        return msg;
    }


    private static class SimpleSlowFixMsg implements FixMsg {
        private final Map<String, List<String>> tags = new HashMap<>();

        public boolean isSet(int tag) {
            return tags.containsKey(tag);
        }

        public int getInt(int tag) {
            return Integer.parseInt(firstValueOf(tag));
        }

        public int getInt(int tag, int group) {
            return Integer.parseInt(valueOf(tag).get(group));
        }

        public char getChar(int tag) {
            return firstValueOf(tag).charAt(0);
        }

        public char getChar(int tag, int group) {
            return valueOf(tag).get(group).charAt(0);
        }

        public CharSequence getCharSeq(int tag) {
            return firstValueOf(tag);
        }

        public CharSequence getCharSeq(int tag, int group) {
            return valueOf(tag).get(group);
        }

        public float getFloat(int tag) {
            return Float.parseFloat(firstValueOf(tag));
        }

        public float getFloat(int tag, int group) {
            return Float.parseFloat(valueOf(tag).get(group));
        }

        public boolean getBool(int tag) {
            return firstValueOf(tag).equals("Y");
        }

        private String firstValueOf(int tag) {
            return valueOf(tag).getFirst();
        }

        private List<String> valueOf(int tag) {
            return tags.get(Integer.toString(tag));
        }

        public String toString() {
            return tags.entrySet()
                       .stream()
                       .map(e -> {
                           List<String> values = e.getValue();
                           return String.format(
                                   "%3s = %s",
                                   e.getKey(),
                                   values.size() == 1
                                           ? values.getFirst()
                                           : values.subList(1, values.size())
                           );
                       })
                       .sorted()
                       .collect(joining("\n"));
        }
    }
}
