# Java Implementation



[⬅️ Return to Hub](https://github.com/JaehoChoi00/Expresso/blob/main/README.md) &#x20;



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

> * [`Design Philosophy`](#design-philosophy)



---



### Overview



Expresso is a lightweight exposure and diagnostic framework built around a ****2D exposure filtering matrix of Category × Level****.



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

│   └── Consumer\<String>

│       ├── null -> System.out

│       └── active -> External Consumer

│

└── Formatting / Diagnostics

&#x20;   ├── Index

&#x20;   ├── Identity

&#x20;   ├── Category

&#x20;   ├── Level

&#x20;   ├── Time

&#x20;   ├── Error Styling

&#x20;   ├── HERE Styling

&#x20;   └── ANSI Formatting

```



The central design principle is:



> Expresso decides what information is exposed. It does not decide what the consuming application should do with that information.



---



### [Installation & Build](**#section&#x73;**)



> * [Requirements](#requirements)

> * [Build](#build)

> * [Run the Test Suite](#run-the-test-suite)

> * [Build the JAR](#build-the-jar)

> * [Verify the JAR](#verify-the-jar)

> * [Development Workflow](#development-workflow)



The Java implementation can be used directly from source or packaged as a standard `.jar` library.



---



#### [Requirements](**#installation--buil&#x64;**)



* Java Development Kit (JDK)

* `javac`

* `jar`



---



#### [Build](**#installation--buil&#x64;**)



From the `java/` directory:



```bash

mkdir -p build/classes



javac -d build/classes src/main/java/shared/javaUtil/*.java src/main/java/shared/javaUtil/enums/*.java

```



---



#### [Run the Test Suite](**#installation--buil&#x64;**)



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

[#6] [ExpressoTest] [TEST] [LEVEL1] PASSED: 66

[#7] [ExpressoTest] [TEST] [LEVEL1] FAILED: 0



--------------------------------

[#8] [ExpressoTest] [TEST] [LEVEL1] ALL TESTS PASSED.

```



The test suite verifies core Expresso behavior, including:



* Exposure creation

* Category filtering

* Level filtering

* Lazy `Supplier\<String>` evaluation

* Formatting

* Error reporting

* Execution markers

* Timestamp output

* Runtime elapsed time

* Event indexing

* Index filtering

* Dynamic categories

* Reset behavior

* Process ID (PID) tag

* Thread ID (TID) tag

* Thread ID filtering and filter clearing

* PID/TID reset behavior



---



#### [Build the JAR](**#installation--buil&#x64;**)



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



#### [Verify the JAR](**#installation--buil&#x64;**)



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

&#x20;   ├── ExposureCategory.class

&#x20;   ├── ExposureLevel.class

&#x20;   └── ExposureTimeSource.class

```



---



#### [Development Workflow](**#installation--buil&#x64;**)



The normal development cycle is:



```text

Modify Source

&#x20;    │

&#x20;    ▼

Compile

&#x20;    │

&#x20;    ▼

Run Test Suite

&#x20;    │

&#x20;    ▼

Build JAR

&#x20;    │

&#x20;    ▼

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



### [API Fields & Styles](**#section&#x73;**)



| Text Formatting Type    | API Variable Alias Reference                          | Source                       |

| :---------------------- | :---------------------------------------------------- | :--------------------------- |

| Reset Code              | `Expresso.RESET`                                      | `VariableConstants.RESET`    |

| Font Adjustments        | `Expresso.BOLD`, `Expresso.DIM`, `Expresso.UNDERLINE` | `VariableConstants.*`        |

| Line Controls           | `Expresso.LINEFEED`                                   | `VariableConstants.LINEFEED` |

| Text Foreground Palette | `Expresso.FG_BLACK` to `Expresso.FG_WHITE`            | `VariableConstants.FG\_*`     |

| Background Palette      | `Expresso.BG_BLACK` to `Expresso.BG_WHITE`            | `VariableConstants.BG\_*`     |



#### Dynamic Colors



```java

Expresso.FG_COLOR(int id);

Expresso.BG_COLOR(int id);

```



Fetches an ANSI indexed 256-color foreground or background escape sequence.



---



### [Configuration Methods](**#section&#x73;**)



> [Level Filtering](#level-filtering)

> [Category Filtering](#category-filtering)

> [Tag Visibility](#tag-visibility)

> [Event Index Filtering](#event-index-filtering)

> [Reset](#reset)

> [Diagnostic Styling](#diagnostic-styling)



---



#### [Level Filtering](**#configuration-method&#x73;**)



```java

public static void setLevel(ExposureLevel newLevel);



public static void setLevels(ExposureLevel... handPickedLevels);



public static Set\<ExposureLevel> getLevels();

```



`setLevel()` activates ****Cascading Mode****.



`setLevels()` activates ****Explicit Mode**** when multiple levels are supplied.



An empty level set prevents all exposure events from being emitted.



---



#### [Category Filtering](**#configuration-method&#x73;**)



```java

public static void setCategory(ExposureCategory category);



public static void setCategories(ExposureCategory... handPickedCategories);



public static void enableCategory(ExposureCategory category);



public static void disableCategory(ExposureCategory category);



public static void clearAllCategories();



public static Set\<String> getCategories();

```



An empty category set acts as an unrestricted category filter.



Java also supports dynamic categories:



```java

ExposureCategory.of("PHYSICS");

```



Category filtering is based on the normalized category name.



---



#### [Tag Visibility](**#configuration-method&#x73;**)



```java

public static void enableIndexTag(boolean enable);



public static void enableIdentityTag(boolean enable);



public static void enableCategoryTag(boolean enable);



public static void enableLevelTag(boolean enable);



public static void enableTimestamp(boolean enable);



public static void enableElapsedTime(boolean enable);



public static void enableApplicationTime(boolean enable);

public static void enablePIDTag(boolean enable);

public static void enableTIDTag(boolean enable);

```



Index, identity, category, and level tags are enabled by default.



Timestamp, elapsed time, and application time are disabled by default.

PID and TID tags are disabled by default. Enable them independently with `enablePIDTag(true)` and `enableTIDTag(true)`. The PID tag identifies the current process; the TID tag identifies the current Java thread.



---



#### [Event Index Filtering](**#configuration-method&#x73;**)



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



This means the index represents the ****exposure attempt stream after category/level filtering****, rather than only events that are ultimately visible in the output.



---



#### Thread ID Filtering

```java
public static void setTIDFilter(long threadId);

public static void clearTIDFilter();
```

When a TID filter is active, exposure output is accepted only from the selected Java thread. Calling `clearTIDFilter()` removes the restriction. `reset()` also clears the TID filter and disables PID/TID tags.

This filter controls which thread's exposure events are emitted; it does not change the execution or scheduling of application threads.

---

#### [Reset](**#configuration-method&#x73;**)



```java

public static void reset();

```



`reset()` restores the core Expresso runtime state, including exposure filters, event indexing, timing configuration, and external bridge configuration.



---



#### [Diagnostic Styling](**#configuration-method&#x73;**)



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



### [Dual-Mode Evaluation Logic](**#section&#x73;**)



Expresso uses the number of enabled exposure levels to determine its filtering mode.



```java

public static boolean isExposed(

&#x20;       ExposureCategory category,

&#x20;       ExposureLevel requiredLevel

) {

&#x20;   if (category == null || requiredLevel == null) return false;



&#x20;   Set\<String> activeCategories = enabledCategories;



&#x20;   if (!activeCategories.isEmpty()

&#x20;           && !activeCategories.contains(category.name())) {

&#x20;       return false;

&#x20;   }



&#x20;   synchronized (enabledLevels) {

&#x20;       int levelsCount = enabledLevels.size();



&#x20;       if (levelsCount == 0) return false;



&#x20;       if (levelsCount == 1) {

&#x20;           ExposureLevel activeLevel = enabledLevels.iterator().next();



&#x20;           if (activeLevel.getLevel() < requiredLevel.getLevel()) {

&#x20;               return false;

&#x20;           }

&#x20;       } else {

&#x20;           if (!enabledLevels.contains(requiredLevel)) {

&#x20;               return false;

&#x20;           }

&#x20;       }

&#x20;   }



&#x20;   return true;

}

```



#### [Single-Level / Cascading Mode](**#dual-mode-evaluation-logi&#x63;**)



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



The active level defines the ****maximum exposure depth****.



---



#### [Multi-Level / Explicit Mode](**#dual-mode-evaluation-logi&#x63;**)



```java

Expresso.setLevels(

&#x20;   ExposureLevel.LEVEL1,

&#x20;   ExposureLevel.LEVEL5

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



### [Time Sources & Runtime Timing](**#section&#x73;**)



>



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

&#x20;   └── Time relative to Expresso's runtime start reference

&#x20;       └── System.nanoTime()

```



#### [Application Time](**#time-sources--runtime-timin&#x67;**)



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



#### [Absolute Timestamp](**#time-sources--runtime-timin&#x67;**)



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



#### [Expresso Runtime Elapsed Time](**#time-sources--runtime-timin&#x67;**)



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



### [Bridges & Pipelines](**#section&#x73;**)



```java

public static void setBridge(Consumer\<String> outsideSystem);

public static void setBridges(Consumer\<String>... bridges);

public static void addBridge(Consumer\<String> bridge);

public static boolean removeBridge(Consumer\<String> bridge);

public static void clearBridges();

```



The bridge allows Expresso output to be routed into one or more external systems.



A bridge accepts a Java `Consumer\<String>` receiving the ****already constructed output string****:



```java

Consumer\<String> outsideSystem = text -> System.out.print(text);



Expresso.setBridge(outsideSystem);

```



Or use a lambda:



```java

Expresso.setBridge(text -> System.out.print("[BRIDGE]: " + text));

```



#### [Multiple Bridges](**#bridges--pipeline&#x73;**)



Expresso supports routing the same output to multiple destinations simultaneously:



```java

Expresso.setBridges(

&#x20;   terminalBridge,

&#x20;   uiBridge,

&#x20;   fileBridge

);

```



Each registered bridge receives the same formatted output string.



****Bridge management methods:****



```java

Expresso.setBridge(bridge);                // Replace all bridges with one

Expresso.setBridges(bridgeA, bridgeB);     // Replace all bridges with several

Expresso.addBridge(bridge);                // Add a destination

Expresso.removeBridge(bridge);             // Remove a destination

Expresso.clearBridges();                   // Remove all destinations

```



When no bridges are registered, Expresso falls back to `System.out`.



Possible destinations include:



```text

Terminal

File

UI

Game Console

Network Stream

Custom Logger

Database

```



The bridge receives the formatted output after Expresso has applied its exposure filtering, index-range checks, metadata, and message formatting.



#### [Routing Pipeline](**#bridges--pipeline&#x73;**)



```text

[Exposure.printf() Invoked]

&#x20;            │

&#x20;            ▼

&#x20;   Is Category & Level Exposed?

&#x20;            │

&#x20;      ┌─────┴─────┐

&#x20;     No           Yes

&#x20;      │            │

&#x20;      ▼            ▼

&#x20;  [Abort]    Increment Event Index

&#x20;                      │

&#x20;                      ▼

&#x20;               Check Index Range

&#x20;                      │

&#x20;               ┌──────┴──────┐

&#x20;            Outside        Inside

&#x20;               │              │

&#x20;               ▼              ▼

&#x20;            [Abort]    Append Metadata

&#x20;                              │

&#x20;                              ▼

&#x20;                       Format Message

&#x20;                              │

&#x20;                              ▼

&#x20;                      Are Bridges Active?

&#x20;                              │

&#x20;                        ┌─────┴─────┐

&#x20;                       Yes          No

&#x20;                        │           │

&#x20;                        ▼           ▼

&#x20;                 Dispatch to All   System.out

&#x20;                 Registered Bridges

&#x20;                        │

&#x20;                        ▼

&#x20;                   [Complete]

```



Expresso determines what information is exposed. The developer determines where that information is delivered and which destinations are active.



---



### [Formatting & Diagnostic Actions](**#section&#x73;**)



> * [Structured Exposure](#structured-exposure)

> * [Structured Formatting](#structural-formatting)

> * [Error Reporting](#error-reporting)

> * [Execution Markers](#execution-markers)



---



#### [Structured Exposure](**#formatting--diagnostic-action&#x73;**)



```java

public static void printf(

&#x20;       ExposureCategory category,

&#x20;       ExposureLevel requiredLevel,

&#x20;       String format,

&#x20;       Object... args

);



public static void printf(

&#x20;       String identity,

&#x20;       ExposureCategory category,

&#x20;       ExposureLevel requiredLevel,

&#x20;       String format,

&#x20;       Object... args

);

```



An identity can be supplied directly, although most application code can use the `Exposure` facade instead.



---



#### [Structural Formatting](**#formatting--diagnostic-action&#x73;**)



```java

public static void NEWLINE(

&#x20;       ExposureCategory category,

&#x20;       ExposureLevel requiredLevel

);



public static void LINEBREAK(

&#x20;       ExposureCategory category,

&#x20;       ExposureLevel requiredLevel

);

```



`NEWLINE()` emits an additional line break.



`LINEBREAK()` produces:



```text

--------------------------------

```



---



#### [Unrestricted Error Reporting](**#formatting--diagnostic-action&#x73;**)



```java

public static void error(

&#x20;       String format,

&#x20;       Object... args

);

```



`error()` bypasses the normal exposure matrix.



A trailing `Throwable` is automatically extracted and printed with its complete stack trace.



Example:



```java

try {

&#x20;   int invalidOperation = 1 / 0;

} catch (ArithmeticException e) {

&#x20;   Expresso.error(

&#x20;       "Execution failed during register calculation [%s]:\n",

&#x20;       "DIV_ZERO",

&#x20;       e

&#x20;   );

}

```



The resulting output includes:



```text

java.lang.ArithmeticException: / by zero

```



---



#### [Execution Markers](**#formatting--diagnostic-action&#x73;**)



```java

public static void hereAnnounce();



public static void hereAnnounce(String label);



public static void hereAnnounce(

&#x20;       String customColor,

&#x20;       String customHeader,

&#x20;       String label

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



### [Exposure Depth](**#section&#x73;**)



`Exposure` is the instance-bound developer-facing interface for `Expresso`.



```java

Exposure exposure = Expresso.exposure("Renderer");

```



The identity is automatically attached to emitted exposure events.



This allows an application component to maintain its own exposure context without repeatedly supplying its identity to the underlying `Expresso` API.



#### [Shorthand Levels](**#exposure-dept&#x68;**)



```java

exposure.l1(category, "...");



exposure.l2(category, "...");



exposure.l3(category, "...");



exposure.l4(category, "...");



exposure.l5(category, "...");

```



Each shorthand method represents a specific `ExposureLevel`.



---



#### [Generic Exposure](**#exposure-dept&#x68;**)



```java

exposure.expose(category, level, "...");

```



The generic method can also receive a `Supplier\<String>`:



```java

exposure.l3(

&#x20;   ExposureCategory.DEBUG,

&#x20;   () -> expensiveDiagnosticCalculation()

);

```



The supplier is evaluated only when the category and level pass the active exposure filter.



This allows expensive diagnostic calculations to remain lazy when their exposure is disabled.



---



#### [Execution Helpers](**#exposure-dept&#x68;**)



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



### [Core API Integration](**#section&#x73;**)



#### [Example](**#core-api-integratio&#x6E;**)



```java

import shared.javaUtil.Expresso;

import shared.javaUtil.Exposure;

import shared.javaUtil.enums.ExposureCategory;

import shared.javaUtil.enums.ExposureLevel;



public class Example {

&#x20;   public static void main(String[] args) {



&#x20;       Exposure alu = Expresso.exposure("ALU");



&#x20;       Expresso.setCategories(

&#x20;           ExposureCategory.COMPONENTIAL,

&#x20;           ExposureCategory.LOWERLEVEL

&#x20;       );



&#x20;       Expresso.setLevels(

&#x20;           ExposureLevel.LEVEL1,

&#x20;           ExposureLevel.LEVEL5

&#x20;       );



&#x20;       Expresso.enableTimestamp(true);

&#x20;       Expresso.enableApplicationTime(false);



&#x20;       Expresso.setBridge(rawText -> {



&#x20;           String cleanText =

&#x20;               rawText.replaceAll("\u001B\\\\[[;\\\d]*m", "");



&#x20;           System.out.print(

&#x20;               "[BRIDGE PIPELINE LOG]: " + cleanText

&#x20;           );

&#x20;       });



&#x20;       alu.l1(

&#x20;           ExposureCategory.COMPONENTIAL,

&#x20;           Expresso.BOLD

&#x20;               \+ "ALU Unit Initialized -> "

&#x20;               \+ Expresso.RESET

&#x20;               \+ Expresso.FG_GREEN

&#x20;               \+ "READY"

&#x20;               \+ Expresso.RESET

&#x20;               \+ "\n"

&#x20;       );



&#x20;       alu.l3(

&#x20;           ExposureCategory.COMPONENTIAL,

&#x20;           "Intermediate Bus Clock: 400MHz\n"

&#x20;       );



&#x20;       alu.l5(

&#x20;           ExposureCategory.LOWERLEVEL,

&#x20;           "NAND Gate Transistor Logic Validated.\n"

&#x20;       );



&#x20;       alu.lbr(

&#x20;           ExposureCategory.COMPONENTIAL,

&#x20;           ExposureLevel.LEVEL1

&#x20;       );



&#x20;       alu.here();



&#x20;       alu.here("This is the label.");



&#x20;       try {

&#x20;           int invalidOperation = 1 / 0;

&#x20;       } catch (ArithmeticException e) {

&#x20;           alu.err(

&#x20;               "Execution failed during register calculation [%s]:\n",

&#x20;               "DIV_ZERO",

&#x20;               e

&#x20;           );

&#x20;       }

&#x20;   }

}

```



Because Explicit Mode is active, only `LEVEL1` and `LEVEL5` exposures are emitted.



The `LEVEL3` exposure is intentionally suppressed.



#### [Example Output](**#core-api-integratio&#x6E;**)



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



## [Design Philosophy](**#section&#x73;**)



Expresso is divided into separate responsibilities:



```text

&#x20;                   ┌──────────────────────┐

&#x20;                   │      APPLICATION     │

&#x20;                   │                      │

&#x20;                   │  Game / Simulation / │

&#x20;                   │  Engine / Tool / App │

&#x20;                   └──────────┬───────────┘

&#x20;                              │

&#x20;                       Application Time

&#x20;                              │

&#x20;                              ▼

&#x20;                   ┌──────────────────────┐

&#x20;                   │  ExposureTimeSource  │

&#x20;                   └──────────┬───────────┘

&#x20;                              │

&#x20;                              ▼

&#x20;   ┌───────────────────────────────────────────────────────┐

&#x20;   │                       EXPRESSO                        │

&#x20;   │                                                       │

&#x20;   │  Category ──┐                                         │

&#x20;   │  Level ─────┤                                         │

&#x20;   │  Index ─────┤──> Exposure Evaluation ──> Formatting   │

&#x20;   │  Identity ──┤                                         │

&#x20;   │  Time ──────┘                                         │

&#x20;   │                                                       │

&#x20;   └──────────────────────────┬────────────────────────────┘

&#x20;                              │

&#x20;                              ▼

&#x20;                       Consumer\<String>

&#x20;                              │

&#x20;                 ┌────────────┼────────────┐

&#x20;                 ▼            ▼            ▼

&#x20;                 UI          File       Console

```



The framework therefore provides ****visibility without ownership****.



The application owns its state.



The application owns its concept of time.



Expresso decides ****what becomes visible****, how it is tagged, and where the resulting exposure stream is routed.&#x20;



It does not decide what the consuming application should do with that information.



[:arrow_up: Return to Top](#java-implementation)