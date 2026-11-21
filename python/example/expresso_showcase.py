"""
Expresso Framework — Reference Template & Feature Walkthrough
=============================================================
Run with:
    pip install expresso-framework
    python expresso_showcase.py

Design Guarantees:
- Zero external dependencies (100% Python standard library)
- Stateless & read-only exposure (no automatic history recording)
- Auto-grader safe (silence all output in one line via setBridge)
"""

import threading
from expresso import (
    Expresso,
    ExposureCategory,
    ExposureLevel,
    ExposureTimeSource,
)

# Short formatting & ANSI color aliases
RESET, BOLD = Expresso.RESET, Expresso.BOLD
CYAN = Expresso.FG_COLOR(117)
AMBER = Expresso.FG_COLOR(214)
GREEN = Expresso.FG_COLOR(150)
PURPLE = Expresso.FG_COLOR(141)


def banner(title: str) -> None:
    print(f"\n{BOLD}{CYAN}[{title}]{RESET}")


# =============================================================================
# CASE 1: Cascading Depth (`setLevel`) & Lazy Evaluation
# =============================================================================
# Cascading mode exposes LEVEL1 up to the active level and skips deeper levels.
# Callables (lambdas) passed to filtered levels are never executed.
banner("CASE 1: Cascading Depth (LEVEL1..LEVEL2) & Lazy Lambdas")

CATEGORY_A = ExposureCategory("CATEGORY_A")
CATEGORY_B = ExposureCategory("CATEGORY_B")

component_a = Expresso.exposure("ComponentA")

Expresso.setLevel(ExposureLevel.LEVEL2)

component_a.l1(CATEGORY_A, GREEN + "Emitted at LEVEL1 (active <= LEVEL2)\n" + RESET)
component_a.nl(CATEGORY_A, ExposureLevel.LEVEL1)
component_a.l2(CATEGORY_B, "   Emitted at LEVEL2 (active <= LEVEL2)\n")
component_a.lbr(CATEGORY_B, ExposureLevel.LEVEL2)

# Filtered out when active level is LEVEL2 (and the callable never executes):
component_a.l3(CATEGORY_B, "   Filtered out at LEVEL3\n")
component_a.l5(CATEGORY_B, lambda: f"   Filtered out at LEVEL5: {[x ** 2 for x in range(10000)]}\n")


# =============================================================================
# CASE 2: 2D Matrix Slicing (`setLevels` + `setCategories`)
# =============================================================================
# Combine explicit level selection with category filtering to isolate an
# exact cross-section of the 2D (Category x Level) matrix.
banner("CASE 2: 2D Matrix Slicing (Explicit Levels + Categories)")

Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5)  # Skips LEVEL2..4
Expresso.setCategories(CATEGORY_B)                               # Hides CATEGORY_A

component_a.l1(CATEGORY_A, "Filtered out: CATEGORY_A is not in active categories\n")
component_a.l1(CATEGORY_B, "Emitted: CATEGORY_B @ LEVEL1\n")
component_a.l3(CATEGORY_B, "Filtered out: CATEGORY_B @ LEVEL3 (not in explicit level set)\n")
component_a.l5(CATEGORY_B, PURPLE + "Emitted: CATEGORY_B @ LEVEL5\n" + RESET)

Expresso.clearAllCategories()
Expresso.setLevel(ExposureLevel.LEVEL1)


# =============================================================================
# CASE 3: Per-Handle Toggles, Global Mute (`Expresso.ON`) & `here()`
# =============================================================================
# Mute an individual handle (`setEnabled`) or all level-based output globally
# (`Expresso.ON`), while `here()` still marks code checkpoints unconditionally.
banner("CASE 3: Handle Toggles, Global Mute & Checkpoints (here())")

component_b = Expresso.exposure("ComponentB")

# Mute only `component_b`; `component_a` remains active
component_b.setEnabled(False)
component_b.l1(CATEGORY_B, "Filtered out: ComponentB handle is disabled\n")
component_a.l1(CATEGORY_A, "Emitted: ComponentA handle is still enabled\n")
component_b.setEnabled(True)

# Global mute: silences standard level output, while `here()` remains visible
Expresso.ON(False)
component_a.l1(CATEGORY_A, "Filtered out: Expresso.ON(False) is active\n")
component_a.here("Checkpoint marker (visible even when Expresso.ON is False)")
Expresso.ON(True)


# =============================================================================
# CASE 4: Diagnostic Metadata Tags, Custom Styling & Event Index Windowing
# =============================================================================
# Toggle timestamps, elapsed time, PID/TID tags, custom `here()` styling, and
# isolate specific event index ranges without modifying exposure calls.
banner("CASE 4: Diagnostic Metadata, Custom Styling & Index Range")

Expresso.resetEventCounter()
Expresso.enableTimestamp(True)
Expresso.enableElapsedTime(True)
Expresso.enablePIDTag(True)
Expresso.enableTIDTag(True)
Expresso.enableDiagnosticTags(True)
Expresso.setHereStyle(AMBER, "[MARKER]")

# Emit only event indices #2 and #3
Expresso.setIndexRange(2, 3)

component_a.l1(CATEGORY_A, "Event #1 -> Filtered out by index range [2..3]\n")
component_a.l1(CATEGORY_A, "Event #2 -> Emitted with diagnostic tags\n")
component_a.l1(CATEGORY_A, "Event #3 -> Emitted with diagnostic tags\n")
component_a.l1(CATEGORY_A, "Event #4 -> Filtered out by index range [2..3]\n")

component_a.here("Custom styled marker inheriting active metadata tags")

# Restore defaults
Expresso.clearIndexFilter()
Expresso.resetHereStyle()
Expresso.enableDiagnosticTags(False)
Expresso.enableTimestamp(False)
Expresso.enableElapsedTime(False)
Expresso.enablePIDTag(False)
Expresso.enableTIDTag(False)


# =============================================================================
# CASE 5: Thread ID Isolation (`setTIDFilter`)
# =============================================================================
# Restrict exposure output to a single thread while background threads run.
banner("CASE 5: Thread ID Isolation (setTIDFilter)")

main_tid = threading.get_native_id()
Expresso.setTIDFilter(main_tid)


def background_worker() -> None:
    component_b.l1(CATEGORY_B, "Filtered out: emitted from background thread\n")


t = threading.Thread(target=background_worker)
t.start()
t.join()

component_b.l1(CATEGORY_B, f"Emitted: matches active TID={main_tid}\n")
Expresso.clearTIDFilter()


# =============================================================================
# CASE 6: Iteration-Resetting Bridge (`ExposureTimeSource` + `setBridge`)
# =============================================================================
# Route loop output to a bridge buffer that resets on every iteration so a
# display panel only renders the current iteration rather than scrollback.
banner("CASE 6: Iteration-Resetting Bridge & Custom Time Source")


class IterationClock(ExposureTimeSource):
    def __init__(self) -> None:
        self.iteration = 0

    def getTime(self) -> str:
        return f"ITER {self.iteration:04d}"


clock = IterationClock()
Expresso.setTimeSource(clock)
Expresso.enableApplicationTime(True)
Expresso.setLevel(ExposureLevel.LEVEL1)

ITERATION = ExposureCategory("ITERATION")
loop_component = Expresso.exposure("LoopComponent")
frame_buffer = []

Expresso.setBridge(frame_buffer.append)

for i in range(1, 61):
    clock.iteration = i
    frame_buffer.clear()  # Reset buffer at the start of every iteration

    loop_component.l5(ITERATION, "   Per-iteration LEVEL5 detail (filtered at LEVEL1)\n")

    if i == 30:
        loop_component.l1(ITERATION, AMBER + "Emitted at iteration 30 (LEVEL1)\n" + RESET)

    # Temporarily elevate level for a single iteration, then restore
    if i == 45:
        Expresso.setLevel(ExposureLevel.LEVEL5)
        loop_component.l5(ITERATION, "   Emitted at iteration 45 (temporary LEVEL5)\n")
        Expresso.setLevel(ExposureLevel.LEVEL1)

    if frame_buffer:
        print("".join(frame_buffer), end="")

Expresso.clearBridges()
Expresso.enableApplicationTime(False)


# =============================================================================
# CASE 7: Multi-Bridge Fan-Out (`setBridges`), `err()` & Master Mute
# =============================================================================
# Fan out formatted strings to multiple destinations simultaneously, emit
# structured tracebacks (`err()`), and silence all output in one line.
banner("CASE 7: Multi-Bridge Fan-Out, Error Tracebacks & Master Mute")

captured_events = []
Expresso.setBridges(
    lambda text: print("[BRIDGE_1] " + text, end=""),
    captured_events.append,
)

Expresso.setErrorStyle(Expresso.FG_RED + BOLD, "[ERROR] ")
try:
    _ = 1 / 0
except ZeroDivisionError as exc:
    component_a.err("Caught exception:\n", exc)
Expresso.resetErrorStyle()

print(f"Bridge 2 captured {len(captured_events)} formatted event(s) in parallel.")

# Master mute: silences ALL output (including `here()` and `err()`)
Expresso.setBridge(lambda _: None)
component_a.l1(CATEGORY_A, "Silenced by master bridge mute\n")
component_a.here("Also silenced by master bridge mute")

# Restore clean default state
Expresso.reset()
print(f"\n{GREEN}{BOLD}All 7 cases completed cleanly.{RESET}\n")