package com.mdl.javafixparser;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

public class TimeComparisonTest {

    static final int MESSAGES = 100_000;
    static final int WARMUP = 10_000;

    static class Work {
        public final String description;
        public final byte[] data;
        public final Consumer<FixMsg>[] fetchFields;

        public Work(String description, byte[] data, Consumer<FixMsg>... fetchFields) {
            this.description = description;
            this.data = data;
            this.fetchFields = fetchFields;
        }

        void fetchFields(FixMsg msg1) {
            for (int i = 0; i < fetchFields.length; i++)
                fetchFields[i].accept(msg1);
        }
    }

    static Consumer<FixMsg> boolVal(int tag) {return m -> m.getBool(tag);}

    static Consumer<FixMsg> intVal(int tag) {return m -> m.getInt(tag);}

    static Consumer<FixMsg> charSeq(int tag) {return m -> m.getCharSeq(tag);}

    static Consumer<FixMsg> charSeq(int tag, int group) {return m -> m.getCharSeq(tag, group);}

    static Consumer<FixMsg> charVal(int tag) {return m -> m.getChar(tag);}

    static Consumer<FixMsg> floatVal(int tag) {return m -> m.getFloat(tag);}


    static final Work[] work_cycle = {
            new Work(
                    "logon",
                    SinglePassTagParserTest.logon,
                    charSeq(8), intVal(9), charSeq(35), boolVal(141), charSeq(554), charSeq(52)
            ),
            new Work(
                    "new_order_single",
                    SinglePassTagParserTest.new_order_single,
                    charSeq(8), intVal(9), charSeq(35), charSeq(52), charVal(40)
            ),
            new Work(
                    "execution_report",
                    SinglePassTagParserTest.execution_report,
                    charSeq(8), intVal(9), charSeq(35), charSeq(52), charVal(40), floatVal(44)
            ),
            new Work(
                    "repeating_group",
                    SinglePassTagParserTest.repeating_group,
                    charSeq(8), intVal(9), charSeq(35), charSeq(52), charVal(40), charSeq(448, 0), charSeq(448, 1), charSeq(448, 2), charSeq(448, 3)
            ),
            new Work(
                    "execution_report",
                    SinglePassTagParserTest.execution_report,
                    charSeq(8), intVal(9), charSeq(35), charSeq(52), charVal(40), floatVal(44)
            ),
            new Work(
                    "multi_repeating_groups",
                    SinglePassTagParserTest.multi_repeating_groups,
                    charSeq(8), intVal(9), charSeq(35), charSeq(52), charVal(40), charSeq(448, 0), charSeq(448, 1), charSeq(79, 0), charSeq(79, 1)
            ),
    };



    final Consumer<Work> simpleParser = new Consumer<>() {
        final SimpleSlowTagParser parser = new SimpleSlowTagParser();

        public void accept(Work work) {
            FixMsg msg = parser.parse(work.data);
            work.fetchFields(msg);
        }
    };

    final Consumer<Work> singlePassParser = new Consumer<>() {
        final FixTagAccumulator msg = new FixTagAccumulator(200);
        final SinglePassTagParser parser = new SinglePassTagParser();

        public void accept(Work work) {
            parser.parse(work.data, msg);
            work.fetchFields(msg);
        }
    };




    @Test void simpleAndSlowParserTimes() {
        printTiming(simpleParser);
    }

    @Test void singlePassParserTimes() {
        printTiming(singlePassParser);
    }

    static void printTiming(Consumer<Work> action) {
        repeatWork(action, WARMUP);

        long start = System.currentTimeMillis();

        repeatWork(action, MESSAGES);

        long end = System.currentTimeMillis();
        long duration = end - start;

        System.out.println(
                String.format(
                        "%,3d ms",
                        duration
                ));
    }

    static void repeatWork(Consumer<Work> consumer, int msgCount) {
        int cycles = msgCount / work_cycle.length;

        for (int i = 0; i < cycles; i++)
            for (int j = 0; j < work_cycle.length; j++)
                consumer.accept(work_cycle[j]);
    }
}
