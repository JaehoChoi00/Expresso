# Exposure Matrix Filter

[:arrow_left: Return to Hub](/shared/SharedUtility.md)

[:arrow_left: Return to Understanding Expresso](/shared/javaUtil/Expresso.md)

> What type of coffee bean, and what level of roast?

> * [`ExposureLevel.java`](/shared/javaUtil/enums/ExposureLevel.java)
> * [`ExposureCategory.java`](/shared/javaUtil/enums/ExposureCategory.java)
> * [`ExposureTimeSource.java`](/shared/javaUtil/enums/ExposureTimeSource.java)

## Sections

> * [`Overview`](#overview)
> * [`ExposureCategory`](#exposurecategory)
> * [`ExposureLevel`](#exposurelevel)
> * [`ExposureTimeSource`](#exposuretimesource)
> * [`LEVEL1`](#level1)
> * [`LEVEL2`](#level2)
> * [`LEVEL3`](#level3)
> * [`LEVEL4`](#level4)
> * [`LEVEL5`](#level5)
> * [`Flexible Selection`](#flexibl-selection)

---

### [`Overview`](#sections)

Expresso uses two independent dimensions to determine what becomes visible:

```txt
                    EXPOSURE MATRIX
                           │
              ┌────────────┤
              │            │          
          Category       Level          
              ▼            ▼            
            VANILLA      LEVEL 1     Essential
            SYSTEMLOG    LEVEL 2     Component
            DEBUG        LEVEL 3     Intermediate
            COMPONENTIAL LEVEL 4     Internal
            LOWERLEVEL   LEVEL 5     Primitive
            TEST
```

The **Category** is your choice of **Coffee bean**:

> **"What kind of information is this?"**

The **Level** is your desired **roast** level:

> **"How deeply do we want to inspect it?"**

Neither dimension inherently owns the other.

For example:

```java
Exposure.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL3, "Intermediate value = %d\n", value);
```

means:

```txt
Category = DEBUG
Depth    = LEVEL3
```

The active configuration decides whether that combination is exposed.

---

### [`ExposureCategory`](#sections)

`ExposureCategory` defines the **horizontal domain** of the exposure matrix.

```java

public enum ExposureCategory {

    /** Regular unfiltered mode. */
    VANILLA,

    /** System log or system-related calls. */
    SYSTEMLOG,

    /** Regular debugging information. */
    DEBUG,

    /** Major system building blocks such as ALU, registers, components. */
    COMPONENTIAL,

    /** Low-level diagnostic tracing such as variables and primitive operations. */
    LOWERLEVEL,

    /** Atomic bit-level operations, logic gates (XOR, AND, OR), bit shifts, and carry bit propagations. */
    BITWISE,

    /** Testing-related information. */
    TEST
}
```

### Category Intuition

```txt
VANILLA
└── General application output

SYSTEMLOG
└── System-level events and infrastructure

DEBUG
└── Developer-oriented diagnostic information

COMPONENTIAL
└── Major system building blocks
    ├── ALU
    ├── Registers
    ├── Components
    └── Subsystems

LOWERLEVEL
└── Primitive / implementation-level information
    ├── Variables
    ├── Individual transformations
    └── Low-level execution

BITWISE
└── Bit-level operations

TEST
└── Testing and validation output
```

Categories can be explicitly selected:

```java
Expresso.setCategories(ExposureCategory.VANILLA, ExposureCategory.COMPONENTIAL);
```

Only these horizontal domains are then eligible for exposure.

```java
Expresso.printf(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL2,  "ALU ADD REGISTERS LOADED\n"); // Shown

Expresso.printf(ExposureCategory.LOWERLEVEL, ExposureLevel.LEVEL5, "NAND: 1 NAND 1 = 0\n"); // Blocked
```

Categories therefore provide **domain isolation** independently from exposure depth.

---

### [`ExposureTimeSource`](#sections)

`ExposureTimeSource` provides an optional external source of **application-defined time information** that can be displayed alongside Expresso output.

Unlike the built-in timestamp, which represents the machine's current real time, an `ExposureTimeSource` allows the application itself to determine what "time" should be displayed.

This makes it useful for systems that have their own concept of time, such as:

```txt
Game Time
Simulation Time
Tick / Frame Time
Replay Time
Virtual Clock
Network Time
Custom Application Clock
```

The source is registered with Expresso:

```java
Expresso.setTimeSource(source);
```

and enabled with:

```java
Expresso.enableApplicationTime(true);
```

When active, the supplied value is emitted as an additional metadata tag:

```txt
[12:45.320] [#42] [COMPONENTIAL] [LEVEL3] Intermediate calculation...
```

The basic interface is:

```java
public interface ExposureTimeSource {

    String getTime();

}
```

A simple implementation could be:

```java
ExposureTimeSource simulationClock = () -> {
    return String.format(
        "T+%.3fs",
        simulationTime
    );
};

Expresso.setTimeSource(simulationClock);
Expresso.enableApplicationTime(true);
```

The resulting output could look like:

```txt
[T+14.382s] [#42] [COMPONENTIAL] [LEVEL3] Intermediate calculation...
```

### Application Time vs Timestamp

Expresso distinguishes between two different concepts:

```txt
TIMESTAMP
└── Real-world wall-clock time
    └── yyyy-MM-dd HH:mm:ss.SSS

APPLICATION TIME
└── Time defined by the application
    └── Supplied through ExposureTimeSource
```

They can also be enabled simultaneously:

```java
Expresso.enableTimestamp(true);
Expresso.enableApplicationTime(true);
```

producing output such as:

```txt
[2026-09-02 14:32:18.421] [T+14.382s] [#42] [COMPONENTIAL] [LEVEL3] ...
```

This separation allows Expresso to describe both **when something happened in the real world** and **where the application believes it is in its own timeline**.

---

### [`ExposureLevel`](#sections)

`ExposureLevel` defines the **vertical depth** of the exposure matrix.

```java
public enum ExposureLevel {

    LEVEL1(1), // Final & essential result

    LEVEL2(2), // Major operation being performed

    LEVEL3(3), // Important intermediate values

    LEVEL4(4), // Internal operations of the component

    LEVEL5(5)  // Lowest-level operations & individual transformations

}
```

The levels form a conceptual progression:

```txt
LEVEL1 ── ESSENTIAL
   │
   └── Final / externally meaningful result

LEVEL2 ── COMPONENT
   │
   └── Major operation being performed

LEVEL3 ── INTERMEDIATE
   │
   └── Important intermediate values

LEVEL4 ── INTERNAL
   │
   └── Internal operations of a component

LEVEL5 ── PRIMITIVE
   │
   └── Lowest-level operations / individual transformations
```

The level does **not** determine the category.

For example, both of these are valid:

```java
Exposure.printf(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL1, "ALU READY\n");

Exposure.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "Debugger attached\n");
```

The category identifies the **domain**.

The level identifies the **depth**.

---


### [`LEVEL1`](#sections)

**Essential / final information**

LEVEL1 is intended for information that is useful when the system is viewed from the outside.

```java
Expresso.setCategories(ExposureCategory.VANILLA);

Expresso.setLevel(ExposureLevel.LEVEL1);

Expresso.printf(ExposureCategory.VANILLA, ExposureLevel.LEVEL1, "Output = 01010100\n");

Expresso.printf(ExposureCategory.LOWERLEVEL, ExposureLevel.LEVEL5, "XOR: 0 XOR 0 = 0\n"); // Hidden
```

Output:

```txt
Output = 01010100
```

When `LEVEL1` is the active single level, it acts as a **cascading floor**:

```txt
LEVEL1 -> visible
LEVEL2 -> hidden
LEVEL3 -> hidden
LEVEL4 -> hidden
LEVEL5 -> hidden
```

---

### [`LEVEL2`](#sections)

**Major component operations**

```java
Expresso.setCategories(ExposureCategory.COMPONENTIAL);

Expresso.setLevel(ExposureLevel.LEVEL2);

Expresso.printf(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL2, "Input_1 = 00111000\n");

Expresso.printf(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL2, "Input_2 = 00011100\n");

Expresso.printf(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL1, "Output = 01010100\n");
```

Because a single `LEVEL2` is active, the system uses cascading evaluation:

```txt
LEVEL1 -> visible
LEVEL2 -> visible
LEVEL3 -> hidden
LEVEL4 -> hidden
LEVEL5 -> hidden
```

Output:

```txt
Input_1 = 00111000
Input_2 = 00011100
Output = 01010100
```

---

### [`LEVEL3`](#sections)

**Important intermediate information**

```java
Expresso.setCategories(ExposureCategory.COMPONENTIAL);

Expresso.setLevel(ExposureLevel.LEVEL3);

Expresso.printf(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL2, "Input_1 = 00111000\n");

Expresso.printf(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL2, "Input_2 = 00011100\n");

Expresso.NEWLINE(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL3);

Expresso.printf(
    ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL3, "Binary Addition:\n" + "11110000\n" + "+\n" + "00001111\n" + "────────\n" + "11111111\n"
    );
```

Output:

```txt
Input_1 = 00111000
Input_2 = 00011100

Binary Addition:
11110000
+
00001111
────────
11111111
```

At LEVEL3, all lower-depth information is also visible because the system is operating in cascading mode.

---

### [`LEVEL4`](#sections)

**Internal component operations**

```java
Expresso.setCategories(
    ExposureCategory.COMPONENTIAL
);

Expresso.setLevel(
    ExposureLevel.LEVEL4
);

Expresso.printf(
    ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL4, 
    "Bit 8:\n" + "A: 0\n" + "B: 0\n" + "Carry: 0\n" + "Result: 0\n"
);

Expresso.printf(
    ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL4, 
    "Bit 7:\n" + "A: 0\n" + "B: 0\n" + "Carry: 0\n" + "Result: 0\n"
);
```

Output:

```txt
Bit 8:
A: 0
B: 0
Carry: 0
Result: 0

Bit 7:
A: 0
B: 0
Carry: 0
Result: 0
```

LEVEL4 exposes the internal workings of a component while still hiding deeper LEVEL5 primitive operations.

---

### [`LEVEL5`](#sections)

**Primitive / lowest-level operations**

```java
Expresso.setCategories(ExposureCategory.LOWERLEVEL);

Expresso.setLevel(ExposureLevel.LEVEL5);

Expresso.printf(ExposureCategory.LOWERLEVEL,ExposureLevel.LEVEL5,"Bit 7\n" +"────────────────────────\n\n");

Expresso.printf(
    ExposureCategory.LOWERLEVEL, ExposureLevel.LEVEL5,
    "A = 0\n" + "B = 0\n" + "Carry In = 0\n\n"
);

Expresso.printf(
    ExposureCategory.LOWERLEVEL, ExposureLevel.LEVEL5,
    "XOR:\n" + "0 XOR 0 = 0\n\n" + "AND:\n" + "0 AND 0 = 0\n"
);
```

Output:

```txt
Bit 7
────────────────────────

A = 0
B = 0
Carry In = 0

XOR:
0 XOR 0 = 0

AND:
0 AND 0 = 0
```

LEVEL5 represents the deepest intended inspection of the system.

---

### [`Flexible Selection`](#sections)

Expresso provides two fundamentally different ways to select exposure depth.

### Cascading Mode

Activated when exactly **one level** is selected:

```java
Expresso.setLevel(
 ExposureLevel.LEVEL3
);
```

The selected level becomes a threshold.

```txt
                 ACTIVE LEVEL
                      │
                      ▼
                   LEVEL3
                  /      \
                 /        \
            LEVEL1        LEVEL4
            LEVEL2        LEVEL5
                │
                ▼
            EXPOSED       HIDDEN
```

Therefore:

```txt
LEVEL1 -> exposed
LEVEL2 -> exposed
LEVEL3 -> exposed
LEVEL4 -> hidden
LEVEL5 -> hidden
```

---

### Explicit Mode

Activated when **multiple levels** are selected:

```java
Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5);
```

Now the selected levels are treated as exact values rather than a threshold.

```txt
LEVEL1 -> exposed
LEVEL2 -> hidden
LEVEL3 -> hidden
LEVEL4 -> hidden
LEVEL5 -> exposed
```

This is useful when a developer wants to see two distant layers simultaneously.

For example:

```java
Expresso.setCategories(ExposureCategory.COMPONENTIAL, ExposureCategory.LOWERLEVEL);

Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5);
```

This produces a deliberately sparse view:

```txt
                    EXPOSURE

             LEVEL1 ────────► visible
                │
             LEVEL2 ────────► hidden
                │
             LEVEL3 ────────► hidden
                │
             LEVEL4 ────────► hidden
                │
             LEVEL5 ────────► visible
```

The result is effectively a **non-contiguous slice through the exposure matrix**.

---

## The Matrix in Practice

The complete model can therefore be thought of as:

```txt
                            EXPOSURE LEVEL
                  L1       L2       L3       L4       L5
               ┌────────┬────────┬────────┬────────┬────────┐
 VANILLA       │        │        │        │        │        │
               ├────────┼────────┼────────┼────────┼────────┤
 SYSTEMLOG     │        │        │        │        │        │
               ├────────┼────────┼────────┼────────┼────────┤
 DEBUG         │        │        │        │        │        │
               ├────────┼────────┼────────┼────────┼────────┤
 COMPONENTIAL  │        │        │        │        │        │
               ├────────┼────────┼────────┼────────┼────────┤
 LOWERLEVEL    │        │        │        │        │        │
               ├────────┼────────┼────────┼────────┼────────┤
 TEST          │        │        │        │        │        │
               └────────┴────────┴────────┴────────┴────────┘
                                     │
                                     ▼
                        isExposed(category, level)
```

Every exposure event occupies a conceptual coordinate:

```txt
(Category, Level)
```

For example:

```txt
(COMPONENTIAL, LEVEL2)
(DEBUG,        LEVEL3)
(LOWERLEVEL,   LEVEL5)
```

The filtering system then determines which coordinates are currently visible.

This gives Expresso its fundamental abstraction:

> **Category determines the horizontal domain. Level determines the vertical depth. Together they define the exposure coordinate.**
