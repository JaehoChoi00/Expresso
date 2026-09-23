# C Implementation

[⬅️ Return to Hub](../README.md)
[➡️ Understand `Exposure Level, Exposure Category, Exposure Time Source`](../README.md)

## Code

> * [`Expresso.h`](include/expresso/Expresso.h)
> * [`Exposure.h`](include/expresso/Exposure.h)
> * [`ExposureCategory.h`](include/expresso/ExposureCategory.h)
> * [`ExposureLevel.h`](include/expresso/ExposureLevel.h)
> * [`ExposureTimeSource.h`](include/expresso/ExposureTimeSource.h)
> * [`VariableConstants.h`](include/expresso/VariableConstants.h)
> * [`Expresso.c`](src/Expresso.c)
> * [`Exposure.c`](src/Exposure.c)
> * [`ExposureCategory.c`](src/ExposureCategory.c)

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
> * [`Thread Safety`](#thread-safety)
> * [`Design Philosophy`](#design-philosophy)

---

### [Overview](#sections)

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
│           └── RequiredLevel is enabled
│
├── Time Systems
│   ├── Application Time
│   │   └── ExposureTimeSource
│   │
│   ├── Absolute Timestamp
│   │   └── System Clock
│   │
│   └── Expresso Runtime Elapsed Time
│       └── Monotonic Runtime Clock
│
├── Output Bridge
│   └── function pointer
│       ├── NULL -> stdout
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

The C implementation is designed as a native C library.

It can be built directly from source or integrated into another C project as a library.

> * [`Requirements`](#requirements)
> * [`Build`](#build)
> * [`Run-the-test-suite`](#run-the-test-suite)
> * [`Run-the-thread-test`](#run-the-thread-test)
> * [`Development Workflow`](#development-workflow)

#### [Requirements](#installation--build)

The C implementation requires:

* C compiler with C11 support
* Standard C library
* POSIX threads (`pthread`) for the current thread-safe runtime implementation

On macOS, the implementation can be compiled using Clang.

On Linux, GCC or Clang can be used.

#### [Build](#installation--build)

> [!IMPORTANT]
> BUILD SECTION PENDING 

#### [Run the Test Suite](#installation--build)

Expresso includes a C test program covering the core runtime behavior.

Compile the test:

```bash
gcc -Wall -Wextra -Wpedantic -pthread \
    test/ExpressoTest.c \
    src/Expresso.c \
    src/Exposure.c \
    src/ExposureCategory.c \
    -Iinclude \
    -o ExpressoTest
```

Run it:

```bash
./ExpressoTest
```

The test suite verifies core Expresso behavior, including:

* Exposure creation
* Identity handling
* Category filtering
* Level filtering
* Multiple category selection
* Dynamic custom categories
* Lazy exposure evaluation
* Formatted exposure
* Raw message preservation
* Event indexing
* Index filtering
* Newline handling
* Line-break formatting
* Error reporting
* Lazy error reporting
* Error styling
* HERE execution markers
* HERE styling
* Tag visibility
* Diagnostic tags
* Direct output
* External bridges
* Event counter behavior
* Reset behavior

A successful run ends with:

```text
Final event count: 0
TEST SUITE COMPLETE
```

#### [Run the Thread Test](#installation--build)

Expresso also includes a concurrent test for the global event counter.

Compile:

```bash
gcc -Wall -Wextra -Wpedantic -pthread \
    test/ThreadTest.c \
    src/Expresso.c \
    src/Exposure.c \
    src/ExposureCategory.c \
    -Iinclude \
    -o ThreadTest
```

Run:

```bash
./ThreadTest
```

The test creates multiple threads that simultaneously generate exposure events.

A successful run reports:

```text
Expected event count: 400
Actual event count:   400
PASS: Event counter is thread-safe.
```

The test verifies the atomicity of the global event counter under concurrent exposure generation.

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
Run Thread Test
     │
     ▼
Verify Behavior
     │
     ▼
Distribute
```

From the `c/` directory:

```bash
gcc -Wall -Wextra -Wpedantic -pthread \
    test/ExpressoTest.c \
    src/Expresso.c \
    src/Exposure.c \
    src/ExposureCategory.c \
    -Iinclude \
    -o ExpressoTest

./ExpressoTest

gcc -Wall -Wextra -Wpedantic -pthread \
    test/ThreadTest.c \
    src/Expresso.c \
    src/Exposure.c \
    src/ExposureCategory.c \
    -Iinclude \
    -o ThreadTest

./ThreadTest
```

---

### [API Fields & Styles](#sections)

| Text Formatting Type    | API Variable Alias         | Source                |
| ----------------------- | -------------------------- | --------------------- |
| Reset Code              | `RESET`                    | `VariableConstants.h` |
| Font Adjustments        | `BOLD`, `DIM`, `UNDERLINE` | `VariableConstants.h` |
| Line Controls           | `LINEFEED`                 | `VariableConstants.h` |
| Text Foreground Palette | `FG_BLACK` to `FG_WHITE`   | `VariableConstants.h` |
| Background Palette      | `BG_BLACK` to `BG_WHITE`   | `VariableConstants.h` |

The C implementation exposes these values as C macros rather than Java static fields.

For example:

```c
printf("%s%sREADY%s\n", BOLD, FG_GREEN, RESET);
```

ANSI formatting is optional and is simply part of the emitted text.

Expresso does not require consumers to use its formatting constants.

---

### [Configuration Methods](#sections)

> * [Level Filtering](#level-filtering)
> * [Category Filtering](#category-filtering)
> * [Tag Visibility](#tag-visibility)
> * [Event Index Filtering](#event-index-filtering)
> * [Reset](#reset)
> * [Diagnositc Styling](#diagnostic-styling)

#### [Level Filtering](#configuration-methods)

```c
void setLevel(ExposureLevel level);

void setLevels(ExposureLevel level, ...);
```

`setLevel()` activates Cascading Mode.

`setLevels()` activates Explicit Mode when multiple levels are supplied.

An empty level configuration prevents exposure events from being emitted.

The default level after `reset()` is:

```text
LEVEL1
```

#### [Category Filtering](#configuration-methods)

```c
void setCategory(ExposureCategory category);

void setCategories(ExposureCategory category, ...);

void enableCategory(ExposureCategory category);

void disableCategory(ExposureCategory category);

void clearAllCategories();
```

An empty category set acts as an unrestricted category filter.

C also supports custom categories:

```c
CUSTOM_CATEGORY(PHYSICS, "PHYSICS");
```

Custom categories are identified by their category name.

For example:

```c
CUSTOM_CATEGORY(PHYSICS, "PHYSICS");

setCategory(PHYSICS);
```

produces:

```text
[PHYSICS]
```

Category names therefore allow applications to define their own domain-specific exposure categories without modifying Expresso's built-in category enumeration.

#### [Tag Visibility](#configuration-methods)

```c
void enableIndexTag(bool enable);

void enableIdentityTag(bool enable);

void enableCategoryTag(bool enable);

void enableLevelTag(bool enable);

void enableTimestamp(bool enable);

void enableElapsedTime(bool enable);

void enableApplicationTime(bool enable);

void enableDiagnosticTags(bool enable);
```

Index, identity, category, and level tags are enabled by default.

Timestamp, elapsed time, application time, and diagnostic tags are disabled by default.

Tags control presentation metadata rather than exposure eligibility.

#### [Event Index Filtering](#configuration-methods)

```c
void setIndexRange(long long start, long long end);

void setIndexRangeMax(long long end);

void setSingleIndex(long long targetIndex);

void clearIndexFilter();

void resetEventCounter();

long long getCurrentEventCount();
```

The event index represents Expresso exposure events and is independent of any event, frame, or lifecycle counter maintained by the consuming application.

The global event counter allows specific sections of an execution trace to be isolated without modifying the original exposure statements.

The event index is allocated after category and level filtering and before the index-range filter is applied.

Therefore:

```text
Category / Level Filter
        │
        ▼
Event Index Allocated
        │
        ▼
Index Range Filter
        │
        ▼
Output
```

An exposure rejected by category or level does not consume an event index.

An exposure that receives an event index but falls outside the configured index range still advances the event counter.

#### [Reset](#configuration-methods)

```c
void reset();
```

`reset()` restores the core Expresso runtime state, including:

* Event counter
* Index filtering
* Category filtering
* Level filtering
* Tag configuration
* Timing configuration
* External bridge configuration

Custom error and HERE styles are preserved by reset.

#### [Diagnostic Styling](#configuration-methods)

```c
void setErrorStyle(const char *color, const char *prefix);

void resetErrorStyle(void);

void setHereStyle(const char *color, const char *header);

void resetHereStyle(void);

void enableDiagnosticTags(bool enable);
```

Diagnostic tags are disabled by default.

When enabled, unrestricted diagnostic events such as `error()` and `hereAnnounce()` can inherit the active timing metadata.

---

### [Dual-Mode Evaluation Logic](#sections)

> * [Single-Level / Casceading Mode](#single-level--cascading-mode)
> * [Multi-Level / Explicit Mode](#multi-level--explicit-mode)

Expresso uses the number of enabled exposure levels to determine its filtering mode.

```c
bool isExposed(ExposureCategory category, ExposureLevel requiredLevel);
```

The evaluation process is:

```text
Category valid?
     │
     ▼
Category enabled?
     │
     ▼
Level configuration valid?
     │
     ├── One level
     │      │
     │      ▼
     │   RequiredLevel <= ActiveLevel
     │
     └── Multiple levels
            │
            ▼
       RequiredLevel explicitly enabled
```

#### [Single-Level / Cascading Mode](#dual-mode-evaluation-logic)

```c
setLevel(LEVEL3);
```

Produces:

```text
LEVEL1 -> exposed
LEVEL2 -> exposed
LEVEL3 -> exposed
LEVEL4 -> hidden
LEVEL5 -> hidden
```

The active level defines the maximum exposure depth.

This is the default hierarchical interpretation of the five exposure levels.

#### [Multi-Level / Explicit Mode](#dual-mode-evaluation-logic)

```c
setLevels(LEVEL1, LEVEL5, END_LEVEL);
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

The distinction is:

```text
setLevel()
    = "Expose up to this depth."

setLevels()
    = "Expose exactly these selected depths."
```

---

### [Time Sources & Runtime Timing](#sections)

> * [`Application Time`](#application-time)
> * [`Absolute Timestamp`](#absolute-timestamp)
> * [`Expresso Runtime Elapsed Time`](#expresso-runtime-elapsed-time)

Expresso separates three concepts of time.

```text
Time
│
├── Application Time
│   └── Supplied by the consuming application
│       └── ExposureTimeSource
│
├── Absolute Timestamp
│   └── System clock
│
└── Expresso Runtime Elapsed Time
    └── Monotonic runtime clock
```

#### [Application Time](#time-sources--runtime-timing)

Applications can provide their own concept of time:

```c
void setTimeSource(ExposureTimeSource source);

void enableApplicationTime(bool enable);
```

`ExposureTimeSource` is a function pointer:

```c
typedef const char *(*ExposureTimeSource)(void);
```

The application owns the meaning of the returned string.

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

#### [Absolute Timestamp](#time-sources--runtime-timing)

```c
enableTimestamp(true);
```

The C implementation obtains the current system date/time and formats it for diagnostic output.

An example is:

```text
[2026-09-24 01:44:49.099]
```

This provides a human-readable real-world date and time.

#### [Expresso Runtime Elapsed Time](#time-sources--runtime-timing)

```c
enableElapsedTime(true);
```

The elapsed time is measured relative to Expresso's runtime initialization.

The C implementation uses a monotonic runtime clock so that elapsed time is not dependent on changes to the system wall clock.

Example:

```text
[0.001s]
```

---

### [Bridges & Pipelines](#sections)

```c
void setBridge(void (*outsideSystem)(const char *));
```

The bridge allows Expresso output to be routed into an external system.

The bridge receives the already constructed output string.

For example:

```c
void bridge(const char *text) {
    printf("[BRIDGE] %s", text);
}

setBridge(bridge);
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

The bridge does not need to understand Expresso's filtering logic.

It receives the resulting output after Expresso has constructed it.

#### [Routing Pipeline](#bridges--pipelines)

```text
[Exposure Invoked]
        │
        ▼
Is Category & Level Exposed?
        │
   ┌────┴────┐
  No         Yes
   │          │
   ▼          ▼
[Abort]   Increment Event Index
              │
              ▼
       Check Index Range
              │
       ┌──────┴──────┐
    Outside        Inside
       │              │
       ▼              ▼
    [Abort]     Append Metadata
                      │
                      ▼
                Format Message
                      │
                      ▼
              Is externalBridge
                 active?
                ┌─────┴─────┐
               Yes          No
                │            │
                ▼            ▼
          External      stdout
           Function
```

This creates a separation between:

> Exposure generation → Output destination

Expresso determines what becomes visible.

The consuming application determines what happens to the resulting output.

---

### [Formatting & Diagnostic Actions](#sections)

> * [Structured Exposure](#structured-exposure)
> * [Structured Formatting](#structural-formatting)
> * [Unrestricted Error Reporting](#unrestricted-error-reporting)
> * [Execution Markers](#execution-markers)

#### [Structured Exposure](#formatting--diagnostic-actions)

The primary low-level output function is:

```c
void expressoPrintf(
    const char *identity,
    ExposureCategory category,
    ExposureLevel requiredLevel,
    const char *format,
    ...
);
```

It combines:

* Identity
* Category
* Level
* Filtering
* Event indexing
* Metadata
* Formatting
* Output routing

The `Exposure` interface normally provides the more convenient application-facing API.

#### [Structural Formatting](#formatting--diagnostic-actions)

```c
void nl(Exposure *exposure, ExposureCategory category, ExposureLevel requiredLevel);

void lbr(Exposure *exposure, ExposureCategory category, ExposureLevel requiredLevel);
```

`nl()` emits an additional blank line.

`lbr()` produces a visual line break:

```text
--------------------------------
```

These helpers are deliberately separate from normal exposure messages.

An ordinary exposure message preserves the application's supplied text exactly.

Therefore:

```c
l1(&exposure, EXPOSURE_TEST, "Message");
```

does not automatically append a newline.

If a newline is required, the application explicitly supplies:

```c
l1(&exposure, EXPOSURE_TEST, "Message\n");
```

This allows exposure output to represent arbitrary text rather than forcing every exposure event to become a line.

#### [Unrestricted Error Reporting](#formatting--diagnostic-actions)

```c
void error(const char *format, ...);
```

`error()` bypasses the normal exposure matrix.

It is intended for diagnostic failures that should be reported regardless of the active category and level filters.

Example:

```c
error("Invalid value: %d\n", value);
```

Through the `Exposure` interface:

```c
err(&exposure, "Something went wrong\n");
```

Unlike normal exposure events, errors do not require an `ExposureCategory` or `ExposureLevel`.

#### [Lazy Error Reporting](#formatting--diagnostic-actions)

```c
void errLazy(Exposure *exposure, const char *(*message)(void));
```

A lazy error callback allows the error message to be generated only when it is actually required.

The callback returns the final diagnostic string.

#### Execution Markers

```c
void here(Exposure *exposure);

void hereLabel(Exposure *exposure, const char *label);

void hereCustom(
    Exposure *exposure,
    const char *customColor,
    const char *customHeader,
    const char *label
);
```

These provide high-visibility execution markers for identifying code paths.

Example:

```c
here(&exposure);
```

or:

```c
hereLabel(&exposure, "Reached renderer initialization");
```

A customized marker can be produced with:

```c
hereCustom(
    &exposure,
    BOLD FG_GREEN,
    "--> [RENDERER CHECKPOINT]",
    "Renderer initialized"
);
```

---

### [Exposure Depth](#sections)

> * [Shorthand Levels](#shorthand-levels)
> * [Generic Exposure](#generic-exposure)
> * [Lazy Exposure](#lazy-exposure)
> * [Execution Helpers](#execution-helpers)

`Exposure` is the instance-bound developer-facing interface for `Expresso`.

```c
Exposure exposure = createWithIdentity("Renderer");
```

The identity is automatically attached to emitted exposure events.

This allows an application component to maintain its own exposure context without repeatedly supplying its identity to the underlying `Expresso` API.

For example:

```c
l1(&exposure, EXPOSURE_TEST, "Renderer initialized\n");
```

produces:

```text
[#1] [Renderer] [TEST] [LEVEL1] Renderer initialized
```

#### [Shorthand Levels](#exposure-depth)

```c
l1(&exposure, category, "...");

l2(&exposure, category, "...");

l3(&exposure, category, "...");

l4(&exposure, category, "...");

l5(&exposure, category, "...");
```

Each shorthand function represents a specific `ExposureLevel`.

Conceptually:

```text
l1() -> LEVEL1
l2() -> LEVEL2
l3() -> LEVEL3
l4() -> LEVEL4
l5() -> LEVEL5
```

#### [Generic Exposure](#exposure-depth)

```c
expose(
    &exposure,
    category,
    level,
    "..."
);
```

The generic method allows the required exposure level to be selected dynamically.

For example:

```c
expose(
    &exposure,
    EXPOSURE_DEBUG,
    LEVEL3,
    "Intermediate state: %d\n",
    value
);
```

#### [Lazy Exposure](#exposure-depth)

```c
void exposeLazy(
    Exposure *exposure,
    ExposureCategory category,
    ExposureLevel level,
    const char *(*message)(void)
);
```

The callback is evaluated only when the category and level pass the active exposure filter.

This allows expensive diagnostic calculations to remain lazy when their exposure is disabled.

For example:

```c
const char *buildDiagnostic(void) {
    return "Expensive diagnostic result\n";
}

exposeLazy(
    &exposure,
    EXPOSURE_DEBUG,
    LEVEL5,
    buildDiagnostic
);
```

This avoids performing the diagnostic calculation when the exposure is not visible.

#### [Execution Helpers](#exposure-depth)

```c
enabled(&exposure, category, level);

nl(&exposure, category, level);

lbr(&exposure, category, level);

err(&exposure, "Something went wrong\n");

errLazy(&exposure, buildDiagnostic);

here(&exposure);

hereLabel(&exposure, "Custom label");
```

These helpers provide component-local access to common Expresso operations while automatically retaining the `Exposure` identity.

---

### [Core API Integration](#sections)

> * [Example](#example)
> * [Example Output](#example-output)

#### [Example](#core-api-integration)

```c
#include "Expresso.h"
#include "Exposure.h"
#include "ExposureCategory.h"

int main(void) {
    Exposure alu = createWithIdentity("ALU");

    reset();

    setCategories(
        EXPOSURE_COMPONENTIAL,
        EXPOSURE_LOWERLEVEL,
        END_CATEGORY
    );

    setLevels(
        LEVEL1,
        LEVEL5,
        END_LEVEL
    );

    enableTimestamp(true);
    enableApplicationTime(false);

    void bridge(const char *text) {
        printf("[BRIDGE PIPELINE LOG]: %s", text);
    }

    setBridge(bridge);

    l1(
        &alu,
        EXPOSURE_COMPONENTIAL,
        BOLD
        "ALU Unit Initialized -> "
        RESET
        FG_GREEN
        "READY"
        RESET
        "\n"
    );

    l3(
        &alu,
        EXPOSURE_COMPONENTIAL,
        "Intermediate Bus Clock: 400MHz\n"
    );

    l5(
        &alu,
        EXPOSURE_LOWERLEVEL,
        "NAND Gate Transistor Logic Validated.\n"
    );

    lbr(
        &alu,
        EXPOSURE_COMPONENTIAL,
        LEVEL1
    );

    here(&alu);

    hereLabel(&alu, "This is the label.");

    err(
        &alu,
        "Execution failed during register calculation [%s]\n",
        "DIV_ZERO"
    );

    return 0;
}
```

Because Explicit Mode is active, only `LEVEL1` and `LEVEL5` exposures are emitted.

The `LEVEL3` exposure is intentionally suppressed.

The resulting flow is:

```text
ALU
 │
 ├── COMPONENTIAL / LEVEL1 → exposed
 │
 ├── COMPONENTIAL / LEVEL3 → hidden
 │
 ├── LOWERLEVEL / LEVEL5 → exposed
 │
 ├── LINE BREAK
 │
 ├── HERE
 │
 ├── HERE label
 │
 └── ERROR
```

#### [Example Output](#core-api-integration)

```text
[BRIDGE PIPELINE LOG]: [2026-09-24 01:44:49.099] [#1] [ALU] [COMPONENTIAL] [LEVEL1] ALU Unit Initialized -> READY

[BRIDGE PIPELINE LOG]: [2026-09-24 01:44:49.100] [#2] [ALU] [LOWERLEVEL] [LEVEL5] NAND Gate Transistor Logic Validated.

[BRIDGE PIPELINE LOG]:
--------------------------------
[BRIDGE PIPELINE LOG]: --> [EXPRESSO REPORT HERE] [ALU]
[BRIDGE PIPELINE LOG]: --> [EXPRESSO REPORT HERE] [ALU]: This is the label.
[BRIDGE PIPELINE LOG]: [ERROR]: [ALU] Execution failed during register calculation [DIV_ZERO]
```

---

### [Thread Safety](#sections)

The C implementation uses C11 atomics and POSIX synchronization primitives for shared runtime state.

The global event counter is maintained using an atomic integer:

```c
static atomic_llong globalEventCounter;
```

Event allocation therefore remains atomic when multiple threads generate exposure events concurrently.

Shared runtime configuration is protected using:

```c
static pthread_mutex_t stateMutex;
```

The implementation also uses:

```c
pthread_once_t
```

to initialize the runtime start reference exactly once.

The current implementation therefore separates synchronization responsibilities:

```text
Global Event Counter
        │
        └── atomic operation

Runtime Configuration
        │
        └── mutex

Runtime Initialization
        │
        └── pthread_once
```

The included `ThreadTest` verifies concurrent event generation.

For example:

```text
4 threads
×
100 exposures
=
400 exposure events
```

The expected and actual event counts are compared after all threads have joined.

The thread test demonstrates the thread-safe behavior of the event counter under concurrent exposure generation.

It does not imply that every possible external output consumer is automatically thread-safe. Applications providing their own bridge remain responsible for the thread-safety requirements of that external system.

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
        ┌──────────────────────────────────────────────────┐
        │                     EXPRESSO                     │
        │                                                  │
        │  Category ──┐                                    │
        │  Level ─────┤                                    │
        │  Index ─────┤──> Exposure Evaluation             │
        │  Identity ──┤          │                         │
        │  Time ──────┘          ▼                         │
        │                     Formatting                   │
        │                          │                       │
        └──────────────────────────┼───────────────────────┘
                                   │
                                   ▼
                            Output Function
                                   │
                       ┌───────────┼───────────┐
                       ▼           ▼           ▼
                      UI          File       Console
```

The framework therefore provides **visibility without ownership**.

The application owns its state.

The application owns its concept of time.

Expresso decides **what becomes visible**, how it is tagged, and where the resulting exposure stream is routed. 

It does not decide what the consuming application should do with that information.

[:arrow_up: Return to Top](#c-implementation)
