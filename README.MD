# Design Principles

- Pre-allocate objects once at initialisation and reuse. 
- Use array indexing
  - to keep track of used instances (TagOffsets)
  - instead of a map to access tags (tags are all integers) removing the need to Map

- Zero-copy access to original underlying FIX message bytes
- No Strings (...RawValue toString() & subsubSequence() could do with a bit of enhancement)
- Simple byte[] -> integer & float parsing. 
  - No need to convert to a string to pass to Integer.parseInt() / Float.parseFloat() (prevent hidden object allocation)
  - n.b. doesn't deal with negative numbers. 

# n.b.
- Does not support multi-threaded access. To prevent synchronisation contention and the complexity of writing multi-threaded code, each thread should have a single, exclusive, dedicated parser instance.
- No enforcement of a FIX tag's actual type (i.e. you can ask for a charSeq from an int value)

# Performance Characteristics
- Time Complexity: O(n) - strictly linear
- Space Complexity: O(m) - where m is number of tags / highest supported tag number


## Example usage...

```java

    byte[] data = as_bytes("8=FIX.4.4|9=148|35=D|49=TESTBUY|56=TESTSELL|34=108|...|40=1|54=1|55=MSFT|60=20180920-18:14:19.492|10=092|");
    
    FixTagAccumulator fixMsg = new FixTagAccumulator(1000); // create one + reuse (e.g. instance field)
    SinglePassTagParser singlePass = new SinglePassTagParser();
    singlePass.parse(data, fixMsg);
    
    fixMsg.getChar(8);     // D
    fixMsg.getCharSeq(56); // "TESTSELL"
    fixMsg.getInt(9);      // 148

```

### Repeated groups


```java

    byte[] data = as_bytes(
            "8=FIX.4.4|9=268|35=D|...|" +
            "453=2|" +
                "448=TRADER1|447=D|452=1|" +
                "448=CUST123|447=C|452=3|" +
            "78=2|" +
                "79=ACCT_A|467=CLIENT|80=100|" +
                "79=ACCT_B|467=CLIENT|80=100|");

    // can be the same fixMsg & parser as earlier...
    FixTagAccumulator fixMsg = new FixTagAccumulator(1000); 
    SinglePassTagParser singlePass = new SinglePassTagParser();

    singlePass.parse(data, fixMsg);

    fixMsg.getCharSeq(448, 0); // TRADER1
    fixMsg.getCharSeq(448, 1); // CUST123
    
    fixMsg.getCharSeq(79, 0);  // ACCT_A
    fixMsg.getCharSeq(79, 1);  // ACCT_B

```

# Performance comparison

SinglePassTagParser vs SimpleSlowTagParser (naive idiomatic Java implementation, allocates objects freely, autoboxes..)

## Timed Runs

Each time parse FIX bytes & retrieve some tag values
 - warm up: 10,000 
 - timed run: 100,000,000 

A few 10M runs on my MBP ...

|             | 1          | 2          | 3          | 4          |
|-------------|------------|------------|------------|------------|
| Single Pass | 52,324 ms  | 53,328 ms  | 52,944 ms  | 53,032 ms  |
| Naive       | 126,922 ms | 122,868 ms | 127,758 ms | 128,982 ms |


# Things to improve
_(..if I had the time)_ 
- Fully featured RawValue/CharSequence implementation (an ImmutableCharSequence?) supporting useful string and byte array slice sharing operations
- Configurable error handling and reporting (e.g. ignore malformed fix / halt parsing)
- Hybrid Links class which would be a bit more frugal w/ memory by delegating support of high tag numbers to a map

