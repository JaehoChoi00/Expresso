# Java Implementation

[⬅️ Return to Hub](https://github.com/JaehoChoi00/Expresso/blob/main/README.md)

[➡️ Understand `Exposure Level, Exposure Category, Exposure Time Source`](src/main/java/shared/javaUtil/enums/Beans.md)

## Code

> * [`Exposure.java`](src/main/java/shared/javaUtil/Exposure.java)
> * [`Expresso.java`](src/main/java/shared/javaUtil/Expresso.java)
> * [`VariableConstants.java`](src/main/java/shared/javaUtil/VariableConstants.java)

## Sections

> * [`Overview`](#overview)
> * [`Installation & Build`](#installation--build)
> * [`API Fields & Styles`](#api-fields--styles)
> * [`Configuration Methods`](#configuration-methods)
> * [`Dual-Mode Evaluation Logic`](#dual-mode-evaluation-logic)
> * [`Time Sources & Runtime Timing`](#time-sources--runtime-timing)
> * [`Bridges & Pipelines`](#bridges--pipelines)
> * [`Formatting & Diagnostic Actions`](#formatting--diagnostic-actions)
> * [`Exposure Depth`](#exposure-depth)
> * [`Core API Integration`](#core-api-integration)

---

### Overview

Expresso is a lightweight exposure and diagnostic framework built around a **2D exposure filtering matrix of Category × Level**.

```text
Exposure
│
├── Category Domain Set
│   └── ExposureCategory
│       ├── VANILLA
│       ├── SYSTEMLOG
│       ├── DEBUG
│       ├── COMPONENTIAL
│       ├── LOWERLEVEL
│       ├── BITWISE
│       └── TEST
│
├── Depth Level Set
│   └── ExposureLevel
│       ├── LEVEL1
│       ├── LEVEL2
│       ├── LEVEL3
│       ├── LEVEL4
│       └── LEVEL5
│
├── Event Index Filter
│   └── globalEventCounter
│       └── minIndex <= event <= maxIndex
│
├── Identity Context
│   └── Optional component / subsystem identity
│
├── Dual-Mode Filtering Engine
│   ├── Single Level
│   │   └── Depth of Exposure Level
│   │       └── ActiveLevel >= RequiredLevel
│   │
│   └── Multiple Levels
│       └── Explicit Hand-Picked Isolation
│           └── enabledLevels.contains(RequiredLevel)
│
├── Time Systems
│   ├── Application Time
│   │   └── ExposureTimeSource
│   │
│   ├── Absolute Timestamp
│   │   └── yyyy-MM-dd HH:mm:ss.SSS
│   │
│   └── Expresso Runtime Elapsed Time
│       └── System.nanoTime()
│
├── Output Bridge
│   └── Consumer<String>
│       ├── null -> System.out
│       └── active -> External Consumer
│
└── Formatting / Diagnostics
    ├── Index
    ├── Identity
    ├── Category
    ├── Level
    ├── Time
    ├── Error Styling
    ├── HERE Styling
    └── ANSI Formatting
```

The central design principle is:

> Expresso decides what information is exposed. It does not decide what the consuming application should do with that information.

---

### [Installation & Build](#sections)

> * [Requirements](#requirements)
> * [Build](#build)
> * [Run the Test Suite](#run-the-test-suite)
> * [Build the JAR](#build-the-jar)
> * [Verify the JAR](#verify-the-jar)
> * [Development Workflow](#development-workflow)

The Java implementation can be used directly from source or packaged as a standard `.jar` library.

---

#### [Requirements](#installation--build)

* Java Development Kit (JDK)
* `javac`
* `jar`

---

#### [Build](#installation--build)

From the `java/` directory:

```bash
mkdir -p build/classes

javac -d build/classes src/main/java/shared/javaUtil/*.java src/main/java/shared/javaUtil/enums/*.java
```

---

#### [Run the Test Suite](#installation--build)

Expresso includes a pure-Java test program that uses Expresso itself to report test results.

Compile the test:

```bash
javac -cp build/classes -d build/classes test/ExpressoTest.java
```

Run it:

```bash
java -cp build/classes ExpressoTest
```

A successful run produces:

```text
[2026-09-11 19:38:44.157] [#1] [ExpressoTest] [TEST] EXPRESSO TEST SUITE START
--------------------------------

...

Testing complete
[#6] [ExpressoTest] [TEST] [LEVEL1] PASSED: 46
[#7] [ExpressoTest] [TEST] [LEVEL1] FAILED: 0

--------------------------------
[#8] [ExpressoTest] [TEST] [LEVEL1] ALL TESTS PASSED.
```

The test suite verifies core Expresso behavior, including:

* Exposure creation
* Category filtering
* Level filtering
* Lazy `Supplier<String>` evaluation
* Formatting
* Error reporting
* Execution markers
* Timestamp output
* Runtime elapsed time
* Event indexing
* Index filtering
* Dynamic categories
* Reset behavior

---

#### [Build the JAR](#installation--build)

Create the distributable JAR:

```bash
jar cf build/expresso-0.0.1.jar -C build/classes shared/javaUtil
```

The resulting library is:

```text
build/
└── expresso-0.0.1.jar
```

The JAR contains the compiled `shared.javaUtil` library and does not include the test program.

---

#### [Verify the JAR](#installation--build)

Inspect the packaged classes:

```bash
jar tf build/expresso-0.0.1.jar
```

The JAR should contain:

```text
shared/javaUtil/
├── Expresso.class
├── Exposure.class
├── VariableConstants.class
└── enums/
    ├── ExposureCategory.class
    ├── ExposureLevel.class
    └── ExposureTimeSource.class
```

---

#### [Development Workflow](#installation--build)

The normal development cycle is:

```text
Modify Source
     │
     ▼
Compile
     │
     ▼
Run Test Suite
     │
     ▼
Build JAR
     │
     ▼
Distribute
```

From the `java/` directory:

```bash
mkdir -p build/classes

javac -d build/classes src/main/java/shared/javaUtil/*.java src/main/java/shared/javaUtil/enums/*.java

javac -cp build/classes -d build/classes test/ExpressoTest.java

java -cp build/classes ExpressoTest

jar cf build/expresso-0.0.1.jar -C build/classes shared/javaUtil
```

---

### [API Fields & Styles](#sections)

| Text Formatting Type    | API Variable Alias Reference                          | Source                       |
| :---------------------- | :---------------------------------------------------- | :--------------------------- |
| Reset Code              | `Expresso.RESET`                                      | `VariableConstants.RESET`    |
| Font Adjustments        | `Expresso.BOLD`, `Expresso.DIM`, `Expresso.UNDERLINE` | `VariableConstants.*`        |
| Line Controls           | `Expresso.LINEFEED`                                   | `VariableConstants.LINEFEED` |
| Text Foreground Palette | `Expresso.FG_BLACK` to `Expresso.FG_WHITE`            | `VariableConstants.FG_*`     |
| Background Palette      | `Expresso.BG_BLACK` to `Expresso.BG_WHITE`            | `VariableConstants.BG_*`     |

#### Dynamic Colors

```java
Expresso.FG_COLOR(int id);
Expresso.BG_COLOR(int id);
```

Fetches an ANSI indexed 256-color foreground or background escape sequence.

---

### [Configuration Methods](#sections)

#### Level Filtering

```java
public static void setLevel(ExposureLevel newLevel);

public static void setLevels(ExposureLevel... handPickedLevels);

public static Set<ExposureLevel> getLevels();
```

`setLevel()` activates **Cascading Mode**.

`setLevels()` activates **Explicit Mode** when multiple levels are supplied.

An empty level set prevents all exposure events from being emitted.

---

#### Category Filtering

```java
public static void setCategory(ExposureCategory category);

public static void setCategories(ExposureCategory... handPickedCategories);

public static void enableCategory(ExposureCategory category);

public static void disableCategory(ExposureCategory category);

public static void clearAllCategories();

public static Set<String> getCategories();
```

An empty category set acts as an unrestricted category filter.

Java also supports dynamic categories:

```java
ExposureCategory.of("PHYSICS");
```

Category filtering is based on the normalized category name.

---

#### Tag Visibility

```java
public static void enableIndexTag(boolean enable);

public static void enableIdentityTag(boolean enable);

public static void enableCategoryTag(boolean enable);

public static void enableLevelTag(boolean enable);

public static void enableTimestamp(boolean enable);

public static void enableElapsedTime(boolean enable);

public static void enableApplicationTime(boolean enable);
```

Index, identity, category, and level tags are enabled by default.

Timestamp, elapsed time, and application time are disabled by default.

---

#### Event Index Filtering

```java
public static void setIndexRange(long start, long end);

public static void setIndexRange(long end);

public static void setSingleIndex(long targetIndex);

public static void clearIndexFilter();

public static void resetEventCounter();

public static long getCurrentEventCount();
```

The event index represents Expresso exposure events and is independent of any event, frame, or lifecycle counter maintained by the consuming application.

The global event counter allows specific sections of an execution trace to be isolated without modifying the original exposure statements.

The event index increments after category and level filtering, before the index-range filter is applied.

This means the index represents the **exposure attempt stream after category/level filtering**, rather than only events that are ultimately visible in the output.

---

#### Reset

```java
public static void reset();
```

`reset()` restores the core Expresso runtime state, including exposure filters, event indexing, timing configuration, and external bridge configuration.

---

#### Diagnostic Styling

```java
public static void setErrorStyle(String color, String prefix);

public static void resetErrorStyle();

public static void setHereStyle(String color, String header);

public static void resetHereStyle();

public static void enableDiagnosticTags(boolean enable);
```

Diagnostic tags are disabled by default.

When enabled, unrestricted diagnostic events such as `error()` and `hereAnnounce()` can inherit the active timing metadata.

---

### [Dual-Mode Evaluation Logic](#sections)

Expresso uses the number of enabled exposure levels to determine its filtering mode.

```java
public static boolean isExposed(
        ExposureCategory category,
        ExposureLevel requiredLevel
) {
    if (category == null || requiredLevel == null) return false;

    Set<String> activeCategories = enabledCategories;

    if (!activeCategories.isEmpty()
            && !activeCategories.contains(category.name())) {
        return false;
    }

    synchronized (enabledLevels) {
        int levelsCount = enabledLevels.size();

        if (levelsCount == 0) return false;

        if (levelsCount == 1) {
            ExposureLevel activeLevel = enabledLevels.iterator().next();

            if (activeLevel.getLevel() < requiredLevel.getLevel()) {
                return false;
            }
        } else {
            if (!enabledLevels.contains(requiredLevel)) {
                return false;
            }
        }
    }

    return true;
}
```

#### Single-Level / Cascading Mode

```java
Expresso.setLevel(ExposureLevel.LEVEL3);
```

Produces:

```text
LEVEL1 -> exposed
LEVEL2 -> exposed
LEVEL3 -> exposed
LEVEL4 -> hidden
LEVEL5 -> hidden
```

The active level defines the **maximum exposure depth**.

---

#### Multi-Level / Explicit Mode

```java
Expresso.setLevels(
    ExposureLevel.LEVEL1,
    ExposureLevel.LEVEL5
);
```

Produces:

```text
LEVEL1 -> exposed
LEVEL2 -> hidden
LEVEL3 -> hidden
LEVEL4 -> hidden
LEVEL5 -> exposed
```

This allows non-contiguous exposure selection.

---

### [Time Sources & Runtime Timing](#sections)

Expresso separates three concepts of time.

```text
Time
│
├── Application Time
│   └── Supplied by the consuming application
│       └── ExposureTimeSource
│
├── Absolute Timestamp
│   └── Real-world system date/time
│       └── LocalDateTime
│
└── Expresso Runtime Elapsed Time
    └── Time relative to Expresso's runtime start reference
        └── System.nanoTime()
```

#### Application Time

Applications can provide their own concept of time:

```java
public static void setTimeSource(ExposureTimeSource source);

public static void enableApplicationTime(boolean enable);
```

The `ExposureTimeSource` belongs to the consuming application.

Expresso does not interpret the returned value.

For example, a game could expose:

```text
DAY 142 14:32:08
```

A simulation could expose:

```text
T+0042.871s
```

A replay system could expose:

```text
FRAME 183920
```

Expresso simply displays the value supplied by the source.

This allows Expresso to integrate with systems whose internal notion of time is completely different from real-world time.

---

#### Absolute Timestamp

```java
Expresso.enableTimestamp(true);
```

Displays:

```text
[2026-09-01 23:53:42.381]
```

The timestamp format is:

```text
yyyy-MM-dd HH:mm:ss.SSS
```

This provides a human-readable real-world date and time.

---

#### Expresso Runtime Elapsed Time

```java
Expresso.enableElapsedTime(true);
```

Displays:

```text
[+142.381ms]
```

The elapsed time is measured using:

```java
System.nanoTime()
```

relative to Expresso's runtime start reference.

---

### [Bridges & Pipelines](#sections)

```java
public static void setBridge(Consumer<String> outsideSystem);
```

The bridge allows Expresso output to be routed into an external system.

The bridge accepts a Java `Consumer<String>`:

```java
Consumer<String> outsideSystem = text -> System.out.print(text);
```

or:

```java
Expresso.setBridge(
    text -> System.out.print("[BRIDGE]: " + text)
);
```

Possible destinations include:

```text
File
UI
Game Console
Network Stream
Custom Logger
Database
```

The bridge receives the already constructed raw output string.

#### Routing Pipeline

```text
[Exposure.printf() Invoked]
             │
             ▼
    Is Category & Level Exposed?
             │
       ┌─────┴─────┐
      No           Yes
       │            │
       ▼            ▼
   [Abort]    Increment Event Index
                       │
                       ▼
                Check Index Range
                       │
                ┌──────┴──────┐
             Outside        Inside
                │              │
                ▼              ▼
             [Abort]    Append Metadata
                               │
                               ▼
                        Format Message
                               │
                               ▼
                    Is externalBridge active?
                               │
                         ┌─────┴─────┐
                        Yes          No
                         │           │
                         ▼           ▼
                  Consumer<String> System.out
```

This creates a separation between:

> **Exposure generation → Output destination**

---

### [Formatting & Diagnostic Actions](#sections)

#### Structured Exposure

```java
public static void printf(
        ExposureCategory category,
        ExposureLevel requiredLevel,
        String format,
        Object... args
);

public static void printf(
        String identity,
        ExposureCategory category,
        ExposureLevel requiredLevel,
        String format,
        Object... args
);
```

An identity can be supplied directly, although most application code can use the `Exposure` facade instead.

---

#### Structural Formatting

```java
public static void NEWLINE(
        ExposureCategory category,
        ExposureLevel requiredLevel
);

public static void LINEBREAK(
        ExposureCategory category,
        ExposureLevel requiredLevel
);
```

`NEWLINE()` emits an additional line break.

`LINEBREAK()` produces:

```text
--------------------------------
```

---

#### Unrestricted Error Reporting

```java
public static void error(
        String format,
        Object... args
);
```

`error()` bypasses the normal exposure matrix.

A trailing `Throwable` is automatically extracted and printed with its complete stack trace.

Example:

```java
try {
    int invalidOperation = 1 / 0;
} catch (ArithmeticException e) {
    Expresso.error(
        "Execution failed during register calculation [%s]:\n",
        "DIV_ZERO",
        e
    );
}
```

The resulting output includes:

```text
java.lang.ArithmeticException: / by zero
```

---

#### Execution Markers

```java
public static void hereAnnounce();

public static void hereAnnounce(String label);

public static void hereAnnounce(
        String customColor,
        String customHeader,
        String label
);
```

These provide high-visibility execution markers for identifying code paths.

Example:

```java
Expresso.hereAnnounce();
```

or:

```java
Expresso.hereAnnounce("Reached renderer initialization");
```

---

### [Exposure Depth](#sections)

`Exposure` is the instance-bound developer-facing interface for `Expresso`.

```java
Exposure exposure = Expresso.exposure("Renderer");
```

The identity is automatically attached to emitted exposure events.

This allows an application component to maintain its own exposure context without repeatedly supplying its identity to the underlying `Expresso` API.

#### Shorthand Levels

```java
exposure.l1(category, "...");

exposure.l2(category, "...");

exposure.l3(category, "...");

exposure.l4(category, "...");

exposure.l5(category, "...");
```

Each shorthand method represents a specific `ExposureLevel`.

---

#### Generic Exposure

```java
exposure.expose(category, level, "...");
```

The generic method can also receive a `Supplier<String>`:

```java
exposure.l3(
    ExposureCategory.DEBUG,
    () -> expensiveDiagnosticCalculation()
);
```

The supplier is evaluated only when the category and level pass the active exposure filter.

This allows expensive diagnostic calculations to remain lazy when their exposure is disabled.

---

#### Execution Helpers

```java
exposure.enabled(category, level);

exposure.nl(category, level);

exposure.lbr(category, level);

exposure.err("Something went wrong\n");

exposure.err(() -> "Lazy error message\n");

exposure.here();

exposure.here("Custom label");
```

These helpers provide component-local access to common Expresso operations while automatically retaining the `Exposure` identity.

---

### [Core API Integration](#sections)

#### Example

```java
import shared.javaUtil.Expresso;
import shared.javaUtil.Exposure;
import shared.javaUtil.enums.ExposureCategory;
import shared.javaUtil.enums.ExposureLevel;

public class Example {
    public static void main(String[] args) {

        Exposure alu = Expresso.exposure("ALU");

        Expresso.setCategories(
            ExposureCategory.COMPONENTIAL,
            ExposureCategory.LOWERLEVEL
        );

        Expresso.setLevels(
            ExposureLevel.LEVEL1,
            ExposureLevel.LEVEL5
        );

        Expresso.enableTimestamp(true);
        Expresso.enableApplicationTime(false);

        Expresso.setBridge(rawText -> {

            String cleanText =
                rawText.replaceAll("\u001B\\[[;\\d]*m", "");

            System.out.print(
                "[BRIDGE PIPELINE LOG]: " + cleanText
            );
        });

        alu.l1(
            ExposureCategory.COMPONENTIAL,
            Expresso.BOLD
                + "ALU Unit Initialized -> "
                + Expresso.RESET
                + Expresso.FG_GREEN
                + "READY"
                + Expresso.RESET
                + "\n"
        );

        alu.l3(
            ExposureCategory.COMPONENTIAL,
            "Intermediate Bus Clock: 400MHz\n"
        );

        alu.l5(
            ExposureCategory.LOWERLEVEL,
            "NAND Gate Transistor Logic Validated.\n"
        );

        alu.lbr(
            ExposureCategory.COMPONENTIAL,
            ExposureLevel.LEVEL1
        );

        alu.here();

        alu.here("This is the label.");

        try {
            int invalidOperation = 1 / 0;
        } catch (ArithmeticException e) {
            alu.err(
                "Execution failed during register calculation [%s]:\n",
                "DIV_ZERO",
                e
            );
        }
    }
}
```

Because Explicit Mode is active, only `LEVEL1` and `LEVEL5` exposures are emitted.

The `LEVEL3` exposure is intentionally suppressed.

#### Example Output

```text
[BRIDGE PIPELINE LOG]: [2026-09-01 23:53:42.381] [#1] [ALU] [COMPONENTIAL] [LEVEL1] ALU Unit Initialized -> READY

[BRIDGE PIPELINE LOG]: [2026-09-01 23:53:42.382] [#2] [ALU] [LOWERLEVEL] [LEVEL5] NAND Gate Transistor Logic Validated.

[BRIDGE PIPELINE LOG]:
--------------------------------

[BRIDGE PIPELINE LOG]: --> [EXPRESSO REPORT HERE]: [ALU]

[BRIDGE PIPELINE LOG]: --> [EXPRESSO REPORT HERE]: [ALU] This is the label.

[BRIDGE PIPELINE LOG]: [ERROR]: [ALU] Execution failed during register calculation [DIV_ZERO]:

java.lang.ArithmeticException: / by zero
```

---

## [Design Philosophy](#sections)

Expresso is divided into separate responsibilities:

```text
                    ┌──────────────────────┐
                    │      APPLICATION     │
                    │                      │
                    │  Game / Simulation / │
                    │  Engine / Tool / App │
                    └──────────┬───────────┘
                               │
                        Application Time
                               │
                               ▼
                    ┌──────────────────────┐
                    │  ExposureTimeSource  │
                    └──────────┬───────────┘
                               │
                               ▼
    ┌───────────────────────────────────────────────────────┐
    │                       EXPRESSO                        │
    │                                                       │
    │  Category ──┐                                         │
    │  Level ─────┤                                         │
    │  Index ─────┤──> Exposure Evaluation ──> Formatting   │
    │  Identity ──┤                                         │
    │  Time ──────┘                                         │
    │                                                       │
    └──────────────────────────┬────────────────────────────┘
                               │
                               ▼
                        Consumer<String>
                               │
                  ┌────────────┼────────────┐
                  ▼            ▼            ▼
                  UI          File       Console
```

The framework therefore provides **visibility without ownership**.

The application owns its state.

The application owns its concept of time.

Expresso decides **what becomes visible**, how it is tagged, and where the resulting exposure stream is routed.
