# Java Implementation

[:arrow_left: Return to Hub](../README.md)

[:arrow_right: Understand `Exposure Level, Exposure Category, Exposure Time Source`](/java/src/shared/javaUtil/enums/Beans.md)

## Code
> * [`Exposure.java`](/java/src/shared/javaUtil/Exposure.java)
> * [`Expresso.java`](/java/src/shared/javaUtil/Expresso.java)

## Sections

> * [`Overview`](#overview)
> * [`API Fields & Styles`](#api-fields--styles)
> * [`Configuration Methods`](#configuration-methods)
> * [`Dual-Mode Evaluation Logic`](#dual-mode-evaluation-logic)
> * [`Time Sources & Runtime Timing`](#time-sources--runtime-timing)
> * [`Bridges & Pipelines`](#bridges--pipelines)
> * [`Formatting & Diagnostic Actions`](#formatting--diagnostic-actions)
> * [`Exposure Depth`](#exposure-facade)
> * [`Core API Integration`](#core-api-integration)

---

### [`Overview`](#sections)

Expresso is a lightweight exposure and diagnostic framework built around a **2D filtering matrix**.

The two primary dimensions are:

```txt
Exposure
│
├── Category Domain Set
│   └── ExposureCategory
│
├── Depth Level Set
│   └── ExposureLevel
│
├── Event Index Filter
│   └── globalEventCounter
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
│           └── EnabledLevels.contains(RequiredLevel)
│
├── Time Systems
│   ├── Application Time
│   │   └── ExposureTimeSource
│   │
│   ├── Absolute Timestamp
│   │   └── yyyy-MM-dd HH:mm:ss.SSS
│   │
│   └── Expresso Runtime Elapsed Time
│
├── Output Bridge
│   └── External Output Consumer
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

> **Expresso decides what information is exposed. It does not decide what the consuming application should do with that information.**

---

### [`API Fields & Styles`](#sections)

| Text Formatting Type        | API Variable Alias Reference                          | Escape Sequence Target       |
| :-------------------------- | :---------------------------------------------------- | :--------------------------- |
| **Reset Code**              | `Expresso.RESET`                                      | `VariableConstants.RESET`    |
| **Font Adjustments**        | `Expresso.BOLD`, `Expresso.DIM`, `Expresso.UNDERLINE` | `VariableConstants.*`        |
| **Line Controls**           | `Expresso.LINEFEED`                                   | `VariableConstants.LINEFEED` |
| **Text Foreground Palette** | `Expresso.FG_BLACK` to `Expresso.FG_WHITE`            | `VariableConstants.FG_*`     |
| **Background Palette**      | `Expresso.BG_BLACK` to `Expresso.BG_WHITE`            | `VariableConstants.BG_*`     |

#### Dynamic Colors

```java
Expresso.FG_COLOR(int id);
Expresso.BG_COLOR(int id);
```

Fetches an ANSI indexed 256-color foreground or background escape sequence.

---

### [`Configuration Methods`](#sections)

#### Level Filtering

```java
public static void setLevel(ExposureLevel newLevel);

public static void setLevels(ExposureLevel... handPickedLevels);

public static Set<ExposureLevel> getLevels();
```

`setLevel()` activates **Cascading Mode**.

`setLevels()` with multiple values activates **Explicit Mode**.

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

The global event counter allows specific sections of an execution trace to be isolated without modifying the original logging statements.

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

### [`Dual-Mode Evaluation Logic`](#sections)

Expresso uses the number of enabled exposure levels to determine its filtering mode.

```java
public static boolean isExposed(ExposureCategory category, ExposureLevel requiredLevel) {
    if (category == null || requiredLevel == null) return false;
    
    Set<String> activeCategories = enabledCategories;
    if (!activeCategories.isEmpty() && !activeCategories.contains(category.name())) {
        return false;
    }
    
    synchronized (enabledLevels) {
        int levelsCount = enabledLevels.size();
        if (levelsCount == 0) return false;

        if (levelsCount == 1) {
            ExposureLevel activeLevel = enabledLevels.iterator().next();
            if (activeLevel.getLevel() < requiredLevel.getLevel()) return false;
        } else {
            if (!enabledLevels.contains(requiredLevel)) return false;
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

```txt
LEVEL1 -> exposed
LEVEL2 -> exposed
LEVEL3 -> exposed
LEVEL4 -> hidden
LEVEL5 -> hidden
```

The active level acts as a **floor threshold**.

---

#### Multi-Level / Explicit Mode

```java
Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5);
```

Produces:

```txt
LEVEL1 -> exposed
LEVEL2 -> hidden
LEVEL3 -> hidden
LEVEL4 -> hidden
LEVEL5 -> exposed
```

This allows non-contiguous exposure selection.

---

### [`Time Sources & Runtime Timing`](#sections)

Expresso deliberately separates three different concepts of time.

```txt
Time
│
├── Application Time
│   └── Supplied by the consuming application
│       └── ExposureTimeSource
│
├── Absolute Timestamp
│   └── Real-world system date/time
│       └── yyyy-MM-dd HH:mm:ss.SSS
│
└── Elapsed Runtime
    └── Time since Expresso initialization
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

```txt
DAY 142 14:32:08
```

A simulation could expose:

```txt
T+0042.871s
```

A replay system could expose:

```txt
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

```txt
[2026-09-01 23:53:42.381]
```

The timestamp format is:

```txt
yyyy-MM-dd HH:mm:ss.SSS
```

This provides an unambiguous real-world date and time for persisted logs.

---

#### Expresso Runtime Elapsed Time

```java
Expresso.enableElapsedTime(true);
```

Displays:

```txt
[+142.381ms]
```

This represents the elapsed time since the Expresso runtime initialized.

---

### [`Bridges & Pipelines`](#sections)

```java
public static void setBridge(Consumer<String> outsideSystem);
```

The bridge allows Expresso output to be routed into an external system.

Examples include:

```txt
File
UI
Game Console
Network Stream
Custom Logger
Database
```

The bridge receives the **already constructed raw output string**.

#### Routing Pipeline

```txt
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
                Consumer<String>  System.out
```

This creates a separation between:

> **Exposure generation** → **Output destination**

---

### [`Formatting & Diagnostic Actions`](#sections)

#### Structured Exposure

```java
public static void printf(ExposureCategory category, ExposureLevel requiredLevel, String format, Object... args);

public static void printf(String identity, ExposureCategory category, ExposureLevel requiredLevel, String format, Object... args);
```

---

#### Structural Formatting

```java
public static void NEWLINE(ExposureCategory category, ExposureLevel requiredLevel);

public static void LINEBREAK(ExposureCategory category, ExposureLevel requiredLevel);
```

---

#### Unrestricted Error Reporting

```java
public static void error(String format, Object... args);
```

`error()` bypasses the normal exposure matrix.

A trailing `Throwable` is automatically extracted and printed with its complete stack trace.

---

#### Execution Markers

```java
public static void hereAnnounce();

public static void hereAnnounce(String label);

public static void hereAnnounce(String customColor, String customHeader, String label);
```

These provide high-visibility execution markers for rapidly identifying code paths.

---

### [`Exposure Facade`](#sections)

`Exposure` is the instance-bound developer-facing bridge for `Expresso`.

```java
Exposure exposure = Expresso.exposure("Renderer");

// or

Exposure exposure = Expresso.exposure(Renderer.class.getSimpleName());
```

The identity is automatically attached to emitted exposure events.

#### Shorthand Levels

```java
exposure.l1(category, "...");

exposure.l2(category, "...");

exposure.l3(category, "...");

exposure.l4(category, "...");

exposure.l5(category, "...");
```

#### Generic Exposure

```java
exposure.expose(category, level, "...");
```

All levels also support `Supplier<String>`:

```java
exposure.l3(ExposureCategory.DEBUG, () -> expensiveDiagnosticCalculation());
```

The supplier is evaluated **only when the category and level pass the active exposure filter**.

This makes the facade suitable for performance-sensitive or hot-loop diagnostic code.

#### Execution Helpers

```java
exposure.enabled(category, level);

exposure.nl(category, level);

exposure.lbr(category, level);

exposure.err("...");

exposure.err(() -> "...");

exposure.here();

exposure.here("Custom label");
```

---

### [`Core API Integration`](#sections)

**#### Example**

```java
Exposure alu = Expresso.exposure("ALU");

Expresso.setCategories(ExposureCategory.COMPONENTIAL, ExposureCategory.LOWERLEVEL);
Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5);
Expresso.enableTimestamp(true);
Expresso.enableApplicationTime(false);

Expresso.setBridge(rawText -> { 
    String cleanText = rawText.replaceAll( "\u001B\\[[;\\d]*m", "" ); 
    System.out.print("[BRIDGE PIPELINE LOG]: " + cleanText); 
    });

alu.l1(
    ExposureCategory.COMPONENTIAL,
    Expresso.BOLD + "ALU Unit Initialized -> " + Expresso.RESET +
    Expresso.FG_GREEN + "READY" + Expresso.RESET + "\n"
);

alu.l3(ExposureCategory.COMPONENTIAL, "Intermediate Bus Clock: 400MHz\n");

alu.l5(ExposureCategory.LOWERLEVEL, "NAND Gate Transistor Logic Validated.\n");

alu.lbr(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL1);

alu.here();

alu.here("This is the label.");

try { int invalidOperation = 1 / 0; } 
catch (ArithmeticException e) { alu.err("Execution failed during register calculation [%s]:\n", "DIV_ZERO", e); }
```

**#### Example Output**

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

## Design Philosophy

Expresso is intentionally divided into separate responsibilities:

```txt
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
