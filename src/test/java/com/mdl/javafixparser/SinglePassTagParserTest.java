package com.mdl.javafixparser;

import org.junit.jupiter.api.Test;

import static com.mdl.javafixparser.SimpleSlowTagParser.soh;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SinglePassTagParserTest {

    public static final byte[] logon = as_bytes(
            "8=FIX.4.4|9=126|35=A|49=theBroker.12345|56=CSERVER|34=1|52=20170117-08:03:04|57=TRADE|50=any_string|98=0|108=30|141=Y|553=12345|554=passw0rd!|10=131|");

    public static final byte[] new_order_single = as_bytes(
            "8=FIX.4.4|9=148|35=D|49=TESTBUY|56=TESTSELL|34=108|52=20180920-18:14:19.508|11=636730640278898634|15=USD|21=2|38=7000|40=1|54=1|55=MSFT|60=20180920-18:14:19.492|10=092|");

    public static final byte[] execution_report = as_bytes(
            "8=FIX.4.2|9=163|35=8|49=TESTBUY|56=TESTSELL|34=972|52=20190206-16:25:10.403|11=14163685067084226997|17=238|20=0|39=2|55=AAPL|54=1|38=100|40=2|44=154.155|59=0|10=106|");

    public static final byte[] repeating_group = as_bytes(
            "8=FIX.4.4|9=176|35=D|49=CLIENT12|56=BROKER12|34=215|52=20250517-14:20:00.000|11=12345|21=1|55=IBM|54=1|38=100|40=2|44=135.50|59=0|" +
            "453=4|" +
                "448=PARTY1|447=D|452=1|" +
                "448=PARTY2|447=G|452=3|" +
                "448=PARTY3|447=G|452=4|" +
                "448=PARTY4|447=D|452=10|" +
            "10=128|");

    public static final byte[] multi_repeating_groups = as_bytes(
            "8=FIX.4.4|9=268|35=D|49=CLIENT|56=BROKER|34=5|52=20250517-15:30:00|11=ORDER123|55=AAPL|54=1|38=200|40=2|44=175.00|" +
            "453=2|" +
                "448=TRADER1|447=D|452=1|" +
                "448=CUST123|447=C|452=3|" +
            "78=2|" +
                "79=ACCT_A|467=CLIENT|80=100|" +
                "79=ACCT_B|467=CLIENT|80=100|" +
            "10=187|");


    final FixTagAccumulator fixMsg = new FixTagAccumulator(200); // nb: can even make this static !
    final SinglePassTagParser singlePass = new SinglePassTagParser();


    @Test void singlePassRepeatingGroups() {
        singlePass.parse(repeating_group, fixMsg);

        assertEquals("FIX.4.4", fixMsg.getCharSeq(8).toString());
        assertEquals("CLIENT12", fixMsg.getCharSeq(49).toString());
        assertEquals(176, fixMsg.getInt(9));
        assertEquals('D', fixMsg.getChar(35));

        assertEquals("PARTY1", fixMsg.getCharSeq(448, 0).toString());
        assertEquals("PARTY2", fixMsg.getCharSeq(448, 1).toString());
        assertEquals(3, fixMsg.getInt(452, 1));
        assertEquals('G', fixMsg.getChar(447, 2));

        assertEquals("PARTY4", fixMsg.getCharSeq(448, 3).toString());
    }

    @Test void unpackAllTagsButLoopOnlyOnce() {
        singlePass.parse(new_order_single, fixMsg);

        assertEquals(
                expected(new_order_single),
                fixMsg.paddedString()
        );


        singlePass.parse(execution_report, fixMsg);

        assertEquals(
                expected(execution_report),
                fixMsg.paddedString()
        );
    }

    @Test void skipMissingTag() {
        byte[] data = as_bytes(
                "8=FIX.4.4||35=D|");

        String expected = """
                 8 = FIX.4.4
                35 = D
               """.stripTrailing();

        singlePass.parse(data, fixMsg);

        assertEquals(
                expected,
                fixMsg.paddedString()
        );
    }

    @Test void skipMissingValue() {
        byte[] data = as_bytes(
                "8=FIX.4.4|9=|35=D|");

        String expected = """
                 8 = FIX.4.4
                 9 =\s
                35 = D
               """.stripTrailing();

        singlePass.parse(data, fixMsg);

        assertEquals(
                expected,
                fixMsg.paddedString()
        );

        assertEquals(0, fixMsg.getInt(9));
        assertEquals(RawValue.SOH, fixMsg.getChar(9));
    }

    @Test void skipMissing() {
        byte[] data = as_bytes(
                "8=FIX.4.4|9=35=D|");

        String expected = """
                 8 = FIX.4.4
                 9 = 35=D
               """.stripTrailing();

        singlePass.parse(data, fixMsg);

        assertEquals(
                expected,
                fixMsg.paddedString()
        );

        assertEquals("35=D", fixMsg.getCharSeq(9).toString());
    }

    @Test void skipOverWeirdAlphanumericTag() {
        byte[] data = as_bytes("8=FIX.4.4|35a=A|49=brokerX|");

        String expected = """
                 8 = FIX.4.4
                49 = brokerX
               """.stripTrailing();

        singlePass.parse(data, fixMsg);

        assertEquals(
                expected,
                fixMsg.paddedString()
        );
    }

    static String expected(byte[] data) {
        return new SimpleSlowTagParser().parse(data).toString();
    }

    static byte[] as_bytes(String s) {
        return s.replaceAll("\\|", soh)
                .getBytes(UTF_8);
    }
}
