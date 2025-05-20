package com.mdl.javafixparser;

import org.junit.jupiter.api.Test;

import static com.mdl.javafixparser.SinglePassTagParserTest.logon;
import static com.mdl.javafixparser.SinglePassTagParserTest.repeating_group;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleSlowTagParserTest {

    final SimpleSlowTagParser simple = new SimpleSlowTagParser();

    @Test void basicParseIntoAMap() {
        FixMsg msg = simple.parse(logon);

        assertEquals("FIX.4.4", msg.getCharSeq(8));
        assertEquals("any_string", msg.getCharSeq(50));
        assertEquals(126, msg.getInt(9));
        assertEquals('A', msg.getChar(35));

        assertEquals("theBroker.12345", msg.getCharSeq(49));
        assertEquals("passw0rd!", msg.getCharSeq(554));

        String expected = """
                    8 = FIX.4.4
                    9 = 126
                   10 = 131
                   34 = 1
                   35 = A
                   49 = theBroker.12345
                   50 = any_string
                   52 = 20170117-08:03:04
                   56 = CSERVER
                   57 = TRADE
                   98 = 0
                  108 = 30
                  141 = Y
                  553 = 12345
                  554 = passw0rd!""";

        assertEquals(expected, msg.toString());
    }

    @Test void simpleRepeatingGroups() {
        FixMsg msg = simple.parse(repeating_group);

        assertEquals("PARTY1", msg.getCharSeq(448, 0));
        assertEquals("PARTY2", msg.getCharSeq(448, 1));
        assertEquals(3, msg.getInt(452, 1));

        assertEquals(4, msg.getInt(452, 2));
        assertEquals("PARTY3", msg.getCharSeq(448, 2));

        assertEquals("PARTY4", msg.getCharSeq(448, 3));
    }
}
