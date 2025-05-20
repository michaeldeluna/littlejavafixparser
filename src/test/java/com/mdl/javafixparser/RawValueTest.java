package com.mdl.javafixparser;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RawValueTest {

    final RawValue rawValue = new RawValue(null, -1, -1 -1);

    @Test void parsesIntegersFromTheByteArray() {
        int expected = 123456;

        byte[] bytes = Integer.toString(expected).getBytes(UTF_8);
        rawValue.set(
                bytes,
                0,
                bytes.length - 1);

        assertEquals(
                expected,
                rawValue.asInt());
    }

    @Test void parsesFloatsFromTheByteArray() {
        float expected = 1236.45f;

        byte[] bytes = Float.toString(expected).getBytes(UTF_8);
        rawValue.set(
                bytes,
                0,
                bytes.length - 1);

        assertEquals(
                expected,
                rawValue.asFloat());
    }


    @Test void implementsCharSequence() {
        String expected = "=helloworld.1x";
        byte[] bytes = expected.getBytes(UTF_8);
        rawValue.set(bytes, 0, bytes.length - 1);
        CharSequence cs = rawValue.asCharSequence();

        assertEquals(
                expected.length(),
                cs.length());

        assertEquals(
                expected.charAt(0),
                cs.charAt(0)
        );

        assertEquals(
                expected,
                cs.toString()
        );

        rawValue.set(bytes, 1, bytes.length - 2);
        assertEquals(
                "helloworld.1",
                rawValue.asCharSequence().toString()
        );
    }

    @Test void charSequenceOutOfBounds() {
        String expected = "lorem ipsum dolor sit amet";
        byte[] bytes = expected.getBytes(UTF_8);
        rawValue.set(bytes, 0, bytes.length - 1);
        CharSequence cs = rawValue.asCharSequence();

        assertEquals(
                't',
                cs.charAt(bytes.length - 1));

        assertThrows(
                StringIndexOutOfBoundsException.class,
                () -> cs.charAt(bytes.length));
    }

    @Test void charSequenceSubSequence() {
        String expected = "The quick brown fox jumps over the lazy dog";
        byte[] bytes = expected.getBytes(UTF_8);
        rawValue.set(bytes, 0, bytes.length - 1);

        CharSequence cs = rawValue.subSequence(
                expected.indexOf('q'),
                expected.indexOf('x')
        );

        assertEquals(
                "quick brown fox",
                cs.toString());
    }
}
