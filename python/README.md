# Python Implementation

[Return to Hub](https://github.com/JaehoChoi00/Expresso/blob/main/README.md)

## Code

> * [`expresso.py`](https://github.com/JaehoChoi00/Expresso/blob/main/python/src/expresso/expresso.py)
> * [`exposure.py`](https://github.com/JaehoChoi00/Expresso/blob/main/python/src/expresso/exposure.py)
> * [`variable_constants.py`](https://github.com/JaehoChoi00/Expresso/blob/main/python/src/expresso/variable_constants.py)
> * [`exposure_category.py`](https://github.com/JaehoChoi00/Expresso/blob/main/python/src/expresso/enums/exposure_category.py)
> * [`exposure_level.py`](https://github.com/JaehoChoi00/Expresso/blob/main/python/src/expresso/enums/exposure_level.py)
> * [`exposure_time_source.py`](https://github.com/JaehoChoi00/Expresso/blob/main/python/src/expresso/enums/exposure_time_source.py)

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

### [`Overview`](#sections)

Expresso is a lightweight exposure and diagnostic framework built around a **2D exposure filtering matrix of Category × Level**.

```txt
Exposure

│
├── Category Domain Set
│   └── set
│       ├── VANILLA
│       ├── SYSTEMLOG
│       ├── DEBUG
│       ├── COMPONENTIAL
│       ├── LOWERLEVEL
│       └── TEST
│
├── Depth Level Set
│   └── set
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
│   ├── Wall-Clock Timestamp
│   │   └── datetime.now()
│   │
│   └── Expresso Runtime Elapsed Time
│       └── time.perf_counter()
│
├── Output Bridge
│   └── Callable
│       ├── None -> Native print()
│       └── active -> External Callable
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

### [`Installation & Build`](#sections)

> * [Requirements](#requirements)
> * [Installing from PyPI](#install-from-pypi)
> * [Installing from the Repository](#installing-from-the-repository)
> * [Verify the Installation](#verify-the-installation)
> * [Run the Test Suite](#run-the-test-suite)
> * [Build the Package](#build-the-package)
> * [Install the Built Package](#install-the-built-package)
> * [Development Workflow](#development-workflow)
> * [Installation Summary](#installation-summary)

---

The Python implementation is distributed as a standard Python package and can be installed using `pip`.

#### [Requirements](#installation--build)

- Python 3.9 or newer
- `pip`
- A virtual environment is recommended

#### [Install from PyPI](#installation--build)

***Syntax: Bash***

```bash
python -m pip install expresso-framework
```

***Syntax: Project import***

```py
from expresso import Expresso, Exposure, ExposureCategory, ExposureLevel, ExposureTimeSource
```

---

#### [Installing from the Repository](#installation--build)

***Syntax: Bash***

```bash
cd python

python -m venv .venv
source .venv/bin/activate

python -m pip install -e .
```

> [!NOTE]
> The `-e` flag installs Expresso in **editable mode**, allowing changes made to the source code to be reflected immediately without reinstalling the package.

#### [Verify the Installation](#installation--build)

***Syntax: Bash***

```bash
python -c "from expresso import Expresso, Exposure, ExposureCategory, ExposureLevel; print('Import Complete')"
```

***Result:***

```txt
Import Complete
```

#### [Run the Test Suite](#installation--build)

***Syntax: Bash***

```bash
python -m unittest discover -s tests -v
```

***Result:***

```txt
----------------------------------------------------------------------
Ran 31 tests

OK
```
>[!NOTE] 
>The execution time may vary depending on the system.

#### [Build the Package](#installation--build)

Install the Python build frontend:

***Syntax: Bash***

```bash
python -m pip install --upgrade build
```

Build the package:

***Syntax: Bash***

```bash
python -m build
```

Generated distribution files are placed inside the `dist/` directory:

```txt
dist/
├── expresso_framework-<version>-py3-none-any.whl
└── expresso_framework-<version>.tar.gz
```

#### [Install the Built Package](#installation--build)

***Syntax: Bash***

```bash
python -m pip install dist/*.whl
```

Verify installation:

***Syntax: Bash***

```bash
python -c "from expresso import Expresso, Exposure, ExposureCategory, ExposureLevel; print('Import Complete')"
```

#### [Development Workflow](#installation--build)

For normal development:

```txt
Repository
    │
    ▼
python/
    │
    ├── Create virtual environment
    │
    ▼
python -m venv .venv
    │
    ▼
Activate virtual environment
    │
    ▼
python -m pip install -e .
    │
    ▼
Develop / Modify Source
    │
    ▼
Run Test Suite
    │
    ▼
python -m unittest discover -s tests -v
```

When a distributable package is required:

```txt
Source
    │
    ▼
python -m build
    │
    ▼
dist/
    ├── Wheel (.whl)
    └── Source Distribution (.tar.gz)
```

#### [Installation Summary](#installation--build)

Local setup:

***Syntax: Bash***

```bash
cd python

python -m venv .venv
source .venv/bin/activate

python -m pip install -e .

python -c "from expresso import Expresso, Exposure, ExposureCategory, ExposureLevel; print('Import Complete')"

python -m unittest discover -s tests -v
```

---

### [`API Fields & Styles`](#sections)

| Text Formatting Type        | API Variable Alias Reference                          | Source                       |
| :-------------------------- | :---------------------------------------------------- | :--------------------------- |
| **Reset Code**              | `Expresso.RESET`                                      | `VariableConstants.RESET`    |
| **Font Adjustments**        | `Expresso.BOLD`, `Expresso.DIM`, `Expresso.UNDERLINE` | `VariableConstants.*`        |
| **Line Controls**           | `Expresso.LINEFEED`                                   | `VariableConstants.LINEFEED` |
| **Text Foreground Palette** | `Expresso.FG_BLACK` to `Expresso.FG_WHITE`            | `VariableConstants.FG_*`     |
| **Background Palette**      | `Expresso.BG_BLACK` to `Expresso.BG_WHITE`            | `VariableConstants.BG_*`     |

#### Dynamic Colors

```py
Expresso.FG_COLOR(id)
Expresso.BG_COLOR(id)
```

Fetches an ANSI indexed 256-color foreground or background escape sequence.

---

### [`Configuration Methods`](#sections)

> * [Level Filtering](#level-filtering)
> * [Category Filtering](#category-filtering)
> * [Tag Visibility](#tag-visibility)
> * [Event Index Filtering](#event-index-filtering)
> * [Reset](#reset)
> * [Diagnostic Styling](#diagnostic-styling)

---

#### [Level Filtering](#configuration-methods)

```py
Expresso.setLevel(newLevel)

Expresso.setLevels(*handPickedLevels)

Expresso.getLevels()
```

`setLevel()` activates **Cascading Mode**.

`setLevels()` activates **Explicit Mode** when multiple levels are supplied.

> [!NOTE]
> An empty level set prevents all exposure events from being emitted.

---

#### [Category Filtering](#configuration-methods)

```py
Expresso.setCategory(category)

Expresso.setCategories(*handPickedCategories)

Expresso.enableCategory(category)

Expresso.disableCategory(category)

Expresso.clearAllCategories()

Expresso.getCategories()
```

An empty category set acts as an unrestricted category filter.

---

#### [Tag Visibility](#configuration-methods)

```py
Expresso.enableIndexTag(enable)

Expresso.enableIdentityTag(enable)

Expresso.enableCategoryTag(enable)

Expresso.enableLevelTag(enable)

Expresso.enableTimestamp(enable)

Expresso.enableElapsedTime(enable)

Expresso.enableApplicationTime(enable)
```

> [!NOTE]
> Index, identity, category, and level tags are enabled by default.
> 
> Timestamp, elapsed time, and application time are disabled by default.

---

#### [Event Index Filtering](#configuration-methods)

```py
Expresso.setIndexRange(start, end)

Expresso.setIndexRange(end)

Expresso.setSingleIndex(targetIndex)

Expresso.clearIndexFilter()

Expresso.resetEventCounter()

Expresso.getCurrentEventCount()
```

> [!NOTE]
> The event index represents Expresso exposure events and is independent of any event, frame, or lifecycle counter maintained by the consuming application.
> 
> The global event counter allows specific sections of an execution trace to be isolated without modifying the original exposure statements.

---

#### [Reset](#configuration-methods)

```py
def reset():
    Expresso.clearAllCategories()
    Expresso.setLevel(ExposureLevel.LEVEL1)
    Expresso.clearIndexFilter()
    Expresso.resetEventCounter()
    Expresso.enableTimestamp(False)
    Expresso.enableElapsedTime(False)
    Expresso.enableApplicationTime(False)
    Expresso.setBridge(None)
```


`reset()` restores Expresso to its default runtime state, including exposure filters, event indexing, timing options, and external bridge configuration.

---

#### [Diagnostic Styling](#configuration-methods)

```py
Expresso.setErrorStyle(color, prefix)

Expresso.resetErrorStyle()

Expresso.setHereStyle(color, header)

Expresso.resetHereStyle()

Expresso.enableDiagnosticTags(enable)
```

Diagnostic tags are disabled by default.

When enabled, unrestricted diagnostic events such as `error()` and `hereAnnounce()` can inherit the active timing metadata.

---

### [`Dual-Mode Evaluation Logic`](#sections)

Expresso determines its filtering mode from the number of enabled exposure levels.

```py
@staticmethod
def isExposed(category, requiredLevel):
    if category is None or requiredLevel is None: return False
    with Expresso.categoriesLock:
        activeCategories = Expresso.enabledCategories
        if activeCategories and category.name not in activeCategories:
            return False

    with Expresso.levelsLock:
        activeLevels = Expresso.enabledLevels

        if not activeLevels: return False

        if len(activeLevels) == 1:
            activeLevel = next(iter(activeLevels))
            
            if activeLevel.getLevel() < requiredLevel.getLevel(): return False
        else:
            if requiredLevel not in activeLevels: return False

    return True
```

#### Single-Level / Cascading Mode

```py
Expresso.setLevel(ExposureLevel.LEVEL3)
```

Produces:

```txt
LEVEL1 -> exposed
LEVEL2 -> exposed
LEVEL3 -> exposed
LEVEL4 -> hidden
LEVEL5 -> hidden
```

The active level defines the **maximum exposure depth**.

---

#### Multi-Level / Explicit Mode

```py
Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5)
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

> * [Application Time](#application-time)
> * [Wall-Clock Timestamp](#wall-clock-timestamp)
> * [Expresso Runtime Elapsed Time](#expresso-runtime-elapsed-time)

Expresso separates three concepts of time.

```txt
Time
│
├── Application Time
│   └── Supplied by the consuming application
│       └── ExposureTimeSource
│
├── Wall-Clock Timestamp
│   └── Real-world system date/time
│       └── datetime.now()
│
└── Expresso Runtime Elapsed Time
    └── Time relative to Expresso's runtime start reference
        └── time.perf_counter()
```

---

#### [Application Time](#time-sources--runtime-timing)

Applications can provide their own concept of time:

```py
Expresso.setTimeSource(source)

Expresso.enableApplicationTime(True)
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

#### [Wall-Clock Timestamp](#time-sources--runtime-timing)

```py
Expresso.enableTimestamp(True)
```

Displays:

```txt
[2026-09-08 00:34:34.189]
```

The timestamp format is:

```txt
yyyy-MM-dd HH:mm:ss.SSS
```

This provides a human-readable real-world date and time.

---

#### [Expresso Runtime Elapsed Time](#time-sources--runtime-timing)

```py
Expresso.enableElapsedTime(True)
```

Displays:

```txt
[+1.537ms]
```

The elapsed time is measured using:

```py
time.perf_counter()
```

relative to Expresso's `runtimeStart`.

---

### [`Bridges & Pipelines`](#sections)

```py
Expresso.setBridge(outsideSystem)
```

The bridge allows Expresso output to be routed into an external system.

The bridge accepts a callable:

```py
def outsideSystem(text): print(text)
```

or:

```py
Expresso.setBridge(lambda text: print("[BRIDGE]: " + text))
```

Possible destinations include:

```txt
File
UI
Game Console
Network Stream
Custom Logger
Database
```

The bridge receives the **already constructed output string**.

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
                    Callable      print()
```

This creates a separation between:

> **Exposure generation** → **Output destination**

---

### [`Formatting & Diagnostic Actions`](#sections)

#### [Structured Exposure](#formatting--diagnostic-actions)

```py
Expresso.printf(category, requiredLevel, formatString, *args)
```

An identity can also be supplied:

```py
Expresso.printf(identity, category, requiredLevel, formatString, *args)
```

---

#### [Structural Formatting](#formatting--diagnostic-actions)

```py
Expresso.NEWLINE(category, requiredLevel)

Expresso.LINEBREAK(category, requiredLevel)
```

`NEWLINE()` emits an additional line break.

`LINEBREAK()` produces:

```txt
--------------------------------
```

---

#### [Error Reporting](#formatting--diagnostic-actions)

**Global diagnostic reporting**

```py
Expresso.error(formatString, *args)
```

> `error()` bypasses the normal exposure matrix.

**Identity-bound diagnostic reporting**

```py
exposure.err("Division failed\n", exception)
```

> `err()` bypasses the normal exposure matrix and associates the diagnostic event with the `Exposure` identity.

The complete traceback is then emitted.

---

#### [Execution Markers](#formatting--diagnostic-actions)

```py
Expresso.hereAnnounce()

Expresso.hereAnnounce(label="Reached renderer initialization")
```

These provide high-visibility execution markers for identifying code paths.

---

### [`Exposure Depth`](#sections)

`Exposure` is the instance-bound developer-facing interface for `Expresso`.

```py
exposure = Expresso.exposure("Renderer")
```

The identity is automatically attached to emitted exposure events.

#### Shorthand Levels

```py
exposure.l1(category, "...")

exposure.l2(category, "...")

exposure.l3(category, "...")

exposure.l4(category, "...")

exposure.l5(category, "...")
```

---

#### Generic Exposure

```py
exposure.expose(category, level, "...")
```

The generic method can also receive a callable.

```py
exposure.l3(ExposureCategory.DEBUG, lambda: expensiveDiagnosticCalculation())
```
> [!NOTE]
> The callable is evaluated **only when the category and level pass the active exposure filter**.
> 
> Unrestricted diagnostic methods such as `err()` evaluate their callable when the diagnostic event is emitted.

---

#### Execution Helpers

```py
exposure.nl(category, level)

exposure.lbr(category, level)

exposure.err("Something went wrong\n")

exposure.err(lambda: "Lazy error message\n")

exposure.here()

exposure.here("Custom label")
```

---

### [`Core API Integration`](#sections)

#### Example

```py
from expresso import Expresso, ExposureCategory, ExposureLevel

exposure = Expresso.exposure("ALU")

Expresso.setCategories(ExposureCategory.COMPONENTIAL, ExposureCategory.LOWERLEVEL)

Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5)

Expresso.enableTimestamp(True)

Expresso.enableApplicationTime(False)

Expresso.setBridge(lambda rawText: print("[BRIDGE PIPELINE LOG]: " + rawText))

exposure.l1(ExposureCategory.COMPONENTIAL, Expresso.BOLD + "ALU Unit Initialized -> " + Expresso.RESET + Expresso.FG_GREEN + "READY" + Expresso.RESET + "\n")

exposure.l3(ExposureCategory.COMPONENTIAL, "Intermediate Bus Clock: 400MHz\n")

exposure.l5(ExposureCategory.LOWERLEVEL, "NAND Gate Transistor Logic Validated.\n")

exposure.lbr(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL1)

exposure.here()

exposure.here("This is the label.")

try:
    invalidOperation = 1 / 0
except Exception as exception:
    exposure.err("Execution failed during register calculation [%s]:\n", "DIV_ZERO", exception)
```

#### Example Output

```txt
[BRIDGE PIPELINE LOG]: [2026-09-11 15:26:24.250] [#1] [ALU] [COMPONENTIAL] [LEVEL1] ALU Unit Initialized -> READY

[BRIDGE PIPELINE LOG]: [2026-09-11 15:26:24.250] [#2] [ALU] [LOWERLEVEL] [LEVEL5] NAND Gate Transistor Logic Validated.

[BRIDGE PIPELINE LOG]: 
--------------------------------

[BRIDGE PIPELINE LOG]: [ALU] --> [EXPRESSO REPORT HERE]

[BRIDGE PIPELINE LOG]: [ALU] --> [EXPRESSO REPORT HERE]: This is the label.

[BRIDGE PIPELINE LOG]: [ERROR]: [ALU] Execution failed during register calculation [DIV_ZERO]:

Traceback...
ZeroDivisionError: division by zero
```

---

## Design Philosophy

Expresso is divided into separate responsibilities:

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
                         Callable Bridge
                               │
                  ┌────────────┼────────────┐
                  ▼            ▼            ▼
                  UI          File       Console
```

The framework therefore provides **visibility without ownership**.

The application owns its state.

The application owns its concept of time.

Expresso decides **what becomes visible**, how it is tagged, and where the resulting exposure stream is routed.

[Back to beginning](#python-implementation)
