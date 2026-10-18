import unittest
import os
from threading import Thread, get_native_id

from expresso import Expresso, Exposure, ExposureCategory, ExposureLevel

class TestBasicApi(unittest.TestCase):

    def setUp(self):
        Expresso.clearAllCategories()
        Expresso.setLevel(ExposureLevel.LEVEL1)
        Expresso.clearIndexFilter()
        Expresso.clearTIDFilter()
        Expresso.resetEventCounter()

        Expresso.enableTimestamp(False)
        Expresso.enableElapsedTime(False)
        Expresso.enableApplicationTime(False)
        Expresso.enablePIDTag(False)
        Expresso.enableTIDTag(False)

        Expresso.setBridge(None)

    def tearDown(self):
        Expresso.clearAllCategories()
        Expresso.setLevel(ExposureLevel.LEVEL1)
        Expresso.clearIndexFilter()
        Expresso.clearTIDFilter()
        Expresso.resetEventCounter()

        Expresso.enablePIDTag(False)
        Expresso.enableTIDTag(False)
        Expresso.enableTimestamp(False)
        Expresso.enableElapsedTime(False)
        Expresso.enableApplicationTime(False)

        Expresso.setBridge(None)

    def capture(self):
        captured = []

        def bridge(message):
            captured.append(message)
            print(message, end="")
            print()

        Expresso.setBridge(bridge)
        return captured

    # BASIC API
    def test_classes_exist(self):
        self.assertIsNotNone(Expresso)
        self.assertIsNotNone(Exposure)

    def test_create_exposure_without_identity(self):
        exposure = Expresso.exposure()

        self.assertIsInstance(exposure, Exposure)
        self.assertIsNone(exposure.getIdentity())

    def test_create_exposure_with_identity(self):
        exposure = Expresso.exposure("TEST-LOGGER")

        self.assertIsInstance(exposure, Exposure)
        self.assertEqual(exposure.getIdentity(), "TEST-LOGGER")

    # DEFAULT LEVEL
    def test_default_level(self):
        captured = self.capture()

        Expresso.printf(ExposureCategory.VANILLA, ExposureLevel.LEVEL1, "VANILLA L1")
        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG L1")
        Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE L1")
        Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL2, "TEST L2")

        self.assertEqual(len(captured), 3)
        self.assertIn("VANILLA L1", captured[0])
        self.assertIn("DEBUG L1", captured[1])
        self.assertIn("BITWISE L1", captured[2])

    # LEVEL 2
    def test_level_two_exposes_lower_levels(self):
        captured = self.capture()
        Expresso.setLevel(ExposureLevel.LEVEL2)

        Expresso.printf(ExposureCategory.VANILLA, ExposureLevel.LEVEL1, "VANILLA L1")
        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL2, "DEBUG L2")
        Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL3, "BITWISE L3")

        self.assertEqual(len(captured), 2)
        self.assertIn("VANILLA L1", captured[0])
        self.assertIn("DEBUG L2", captured[1])

    # SELECTED LEVELS
    def test_selected_levels(self):
        captured = self.capture()

        Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL3, ExposureLevel.LEVEL5)

        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG L1")
        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL2, "DEBUG L2")
        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL3, "DEBUG L3")
        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL4, "DEBUG L4")
        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL5, "DEBUG L5")

        self.assertEqual(len(captured), 3)
        self.assertIn("DEBUG L1", captured[0])
        self.assertIn("DEBUG L3", captured[1])
        self.assertIn("DEBUG L5", captured[2])

    # CATEGORY FILTER
    def test_category_filter(self):
        captured = self.capture()

        Expresso.setLevel(ExposureLevel.LEVEL5)
        Expresso.setCategory(ExposureCategory.DEBUG)

        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG")
        Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE")
        Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL1, "TEST")

        self.assertEqual(len(captured), 1)
        self.assertIn("DEBUG", captured[0])

    # MULTIPLE CATEGORIES
    def test_multiple_categories(self):
        captured = self.capture()

        Expresso.setCategories(ExposureCategory.DEBUG, ExposureCategory.BITWISE)

        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG")
        Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE")
        Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL1, "TEST")

        self.assertEqual(len(captured), 2)
        self.assertIn("DEBUG", captured[0])
        self.assertIn("BITWISE", captured[1])

    # CLEAR CATEGORY FILTER
    def test_clear_category_filter(self):
        captured = self.capture()

        Expresso.setCategories(ExposureCategory.DEBUG, ExposureCategory.BITWISE)
        Expresso.clearAllCategories()
        Expresso.setLevel(ExposureLevel.LEVEL1)

        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG")
        Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE")
        Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL1, "TEST")

        self.assertEqual(len(captured), 3)

    # IDENTITY
    def test_identity_and_levels(self):
        captured = self.capture()

        Expresso.setLevel(ExposureLevel.LEVEL3)

        exposure = Expresso.exposure("TestEngine")

        exposure.l1(ExposureCategory.SYSTEMLOG, "Engine started")
        exposure.l2(ExposureCategory.COMPONENTIAL, "Loading component: %s", "Renderer")
        exposure.l3(ExposureCategory.LOWERLEVEL, "Internal value: %d", 42)

        self.assertEqual(len(captured), 3)

        self.assertIn("[TestEngine]", captured[0])
        self.assertIn("Engine started", captured[0])

        self.assertIn("[TestEngine]", captured[1])
        self.assertIn("Loading component: Renderer", captured[1])

        self.assertIn("[TestEngine]", captured[2])
        self.assertIn("Internal value: 42", captured[2])

    # GENERIC EXPOSURE
    def test_generic_exposure(self):
        captured = self.capture()

        exposure = Expresso.exposure("TestEngine")
        exposure.expose(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "Generic exposure")

        self.assertEqual(len(captured), 1)
        self.assertIn("Generic exposure", captured[0])

    # LAZY
    def test_lazy_exposure(self):
        captured = self.capture()

        Expresso.setLevel(ExposureLevel.LEVEL5)

        exposure = Expresso.exposure("TestEngine")

        exposure.l1(ExposureCategory.DEBUG, lambda: "Lazy message")
        exposure.l5(ExposureCategory.DEBUG, lambda: "Lazy deep message")

        self.assertEqual(len(captured), 2)
        self.assertIn("Lazy message", captured[0])
        self.assertIn("Lazy deep message", captured[1])

    # NEWLINE / LINEBREAK
    def test_newline(self):
        captured = self.capture()

        exposure = Expresso.exposure("TestEngine")
        exposure.nl(ExposureCategory.DEBUG, ExposureLevel.LEVEL1)

        self.assertEqual(len(captured), 1)
        self.assertEqual(captured[0], "\n\n")

    def test_linebreak(self):
        captured = self.capture()

        exposure = Expresso.exposure("TestEngine")
        exposure.lbr(ExposureCategory.DEBUG, ExposureLevel.LEVEL1)

        self.assertEqual(len(captured), 1)
        self.assertEqual(captured[0], "\n--------------------------------\n")

    # ERROR
    def test_error(self):
        captured = self.capture()

        exposure = Expresso.exposure("TestEngine")
        exposure.err("Something went wrong: %s", "test")

        self.assertEqual(len(captured), 1)
        self.assertIn("[TestEngine]", captured[0])
        self.assertIn("Something went wrong: test", captured[0])

    def test_exposure_lazy_error(self):
        captured = self.capture()
        exposure = Expresso.exposure("TestEngine")

        calls = 0

        def message():
            nonlocal calls
            calls += 1
            return "Lazy error Yes message"

        Expresso.setBridge(lambda text: captured.append(text))
        exposure.err(message)

        assert calls == 1
        self.assertEqual(len(captured), 1)
        self.assertIn("[TestEngine]", captured[0])
        self.assertIn("Lazy error Yes message", captured[0])


    def test_error_with_exception(self):
        captured = self.capture()

        exposure = Expresso.exposure("TestEngine")

        try: 1 / 0
        except Exception as exception:
            exposure.err("Division failed", exception)

        self.assertEqual(len(captured), 1)
        self.assertIn("Division failed", captured[0])
        self.assertIn("ZeroDivisionError", captured[0])

    # HERE
    def test_here(self):
        captured = self.capture()

        exposure = Expresso.exposure("TestEngine")
        exposure.here()

        self.assertEqual(len(captured), 1)
        self.assertIn("TestEngine", captured[0])
        self.assertIn("EXPRESSO REPORT HERE", captured[0])

    def test_here_with_label(self):
        captured = self.capture()

        exposure = Expresso.exposure("TestEngine")
        exposure.here("Reached renderer initialization")

        self.assertEqual(len(captured), 1)
        self.assertIn("Reached renderer initialization", captured[0])

    # STATIC HERE
    def test_static_here(self):
        captured = self.capture()

        Expresso.hereAnnounce()
        Expresso.hereAnnounce("StaticTest")
        Expresso.hereAnnounce(identity="StaticTest", label="Static marker with identity")

        self.assertEqual(len(captured), 3)
        self.assertIn("EXPRESSO REPORT HERE", captured[0])
        self.assertIn("StaticTest", captured[1])
        self.assertIn("Static marker with identity", captured[2])

    def test_static_here_custom_style(self):
        captured = self.capture()

        Expresso.hereAnnounce(
            customColor=Expresso.FG_GREEN,
            customHeader="--> [GREEN REPORT]",
            label="Static custom marker"
        )

        self.assertEqual(len(captured), 1)
        self.assertIn("[GREEN REPORT]", captured[0])
        self.assertIn("Static custom marker", captured[0])

    # TAGS
    def test_timestamp_and_elapsed_tags(self):
        captured = self.capture()

        Expresso.enableTimestamp(True)
        Expresso.enableElapsedTime(True)

        exposure = Expresso.exposure("TestEngine")
        exposure.l1(ExposureCategory.SYSTEMLOG, "Timestamp and elapsed time")

        self.assertEqual(len(captured), 1)
        self.assertIn("[", captured[0])
        self.assertIn("ms]", captured[0])
        self.assertIn("Timestamp and elapsed time", captured[0])

    # INDEX
    def test_event_counter(self):
        captured = self.capture()

        Expresso.resetEventCounter()

        exposure = Expresso.exposure("TestEngine")

        exposure.l1(ExposureCategory.DEBUG, "Event 1")
        exposure.l1(ExposureCategory.DEBUG, "Event 2")
        exposure.l1(ExposureCategory.DEBUG, "Event 3")

        self.assertEqual(len(captured), 3)
        self.assertEqual(Expresso.getCurrentEventCount(), 3)

        self.assertIn("[#1]", captured[0])
        self.assertIn("[#2]", captured[1])
        self.assertIn("[#3]", captured[2])

    # INDEX FILTER
    def test_index_filter(self):
        captured = self.capture()

        Expresso.resetEventCounter()
        Expresso.clearIndexFilter()
        Expresso.setSingleIndex(5)

        exposure = Expresso.exposure("TestEngine")

        exposure.l1(ExposureCategory.DEBUG, "Event 1")
        exposure.l1(ExposureCategory.DEBUG, "Event 2")
        exposure.l1(ExposureCategory.DEBUG, "Event 3")
        exposure.l1(ExposureCategory.DEBUG, "Event 4")
        exposure.l1(ExposureCategory.DEBUG, "Event 5")
        exposure.l1(ExposureCategory.DEBUG, "Event 6")

        self.assertEqual(len(captured), 1)
        self.assertIn("Event 5", captured[0])
        self.assertIn("[#5]", captured[0])

        self.assertEqual(Expresso.getCurrentEventCount(), 6)

    # RESET
    def test_reset_state(self):
        Expresso.setLevel(ExposureLevel.LEVEL5)
        Expresso.setCategories(ExposureCategory.DEBUG, ExposureCategory.BITWISE)

        Expresso.enableTimestamp(True)
        Expresso.enableElapsedTime(True)
        Expresso.enableApplicationTime(True)

        Expresso.clearIndexFilter()
        Expresso.resetEventCounter()

        Expresso.reset()

        self.assertEqual(Expresso.getCategories(), frozenset())
        self.assertEqual(Expresso.getLevels(), frozenset({ExposureLevel.LEVEL1}))
        self.assertEqual(Expresso.getCurrentEventCount(), 0)

    def test_get_levels(self):
        Expresso.setLevel(ExposureLevel.LEVEL3)

        self.assertEqual(Expresso.getLevels(), frozenset({ExposureLevel.LEVEL3}))

        Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5)

        self.assertEqual(Expresso.getLevels(), frozenset({ ExposureLevel.LEVEL1, ExposureLevel.LEVEL5 }))

        Expresso.setLevels()

        self.assertEqual(Expresso.getLevels(), frozenset())

    def test_index_range_end_only(self):
        captured = self.capture()
        exposure = Expresso.exposure("TestEngine")

        Expresso.setLevel(ExposureLevel.LEVEL1)
        Expresso.setIndexRange(3)

        exposure.l1(ExposureCategory.DEBUG, "Event 1\n")
        exposure.l1(ExposureCategory.DEBUG, "Event 2\n")
        exposure.l1(ExposureCategory.DEBUG, "Event 3\n")
        exposure.l1(ExposureCategory.DEBUG, "Event 4\n")

        self.assertEqual(len(captured), 3)
        self.assertIn("Event 1", captured[0])
        self.assertIn("Event 2", captured[1])
        self.assertIn("Event 3", captured[2])

    def test_printf_category_level(self):
        captured = self.capture()

        Expresso.setLevel(ExposureLevel.LEVEL1)

        Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "A=%s B=%s\n", "one", 2)

        self.assertEqual(len(captured), 1)
        self.assertIn("A=one B=2", captured[0])
        self.assertIn("[DEBUG]", captured[0])
        self.assertIn("[LEVEL1]", captured[0])

    def test_printf_with_identity(self):
        captured = self.capture()

        Expresso.setLevel(ExposureLevel.LEVEL1)

        Expresso.printf("TestEngine", ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "Identity test: %s\n", "working")

        self.assertEqual(len(captured), 1)
        self.assertIn("[TestEngine]", captured[0])
        self.assertIn("[DEBUG]", captured[0])
        self.assertIn("[LEVEL1]", captured[0])
        self.assertIn("Identity test: working", captured[0])

    def test_ansi_formatting_constants(self):
        constants = [
            "RESET", "BOLD", "DIM", "UNDERLINE", "LINEFEED", "FG_BLACK", 
            "FG_RED", "FG_GREEN", "FG_YELLOW", "FG_BLUE", "FG_MAGENTA", 
            "FG_CYAN", "FG_WHITE", "BG_BLACK", "BG_RED", "BG_GREEN", 
            "BG_YELLOW", "BG_BLUE", "BG_MAGENTA", "BG_CYAN", "BG_WHITE",
        ]

        for name in constants:
            value = getattr(Expresso, name, None)

            self.assertIsNotNone(value, f"Expresso.{name} is not exposed")

            if name == "LINEFEED":
                self.assertEqual(value, Expresso.LINEFEED, "Expresso.LINEFEED must be the line-feed control code")
            else:
                self.assertIsInstance(value, str, f"Expresso.{name} must be a string")
                self.assertNotEqual(value, "", f"Expresso.{name} must not be empty")

    def test_diagnostic_tags_inherit_timing_metadata(self):
        captured = self.capture()

        Expresso.enableTimestamp(True)
        Expresso.enableElapsedTime(True)
        Expresso.enableDiagnosticTags(True)

        Expresso.error("Diagnostic timing\n")

        self.assertEqual(len(captured), 1)
        self.assertIn("Diagnostic timing", captured[0])
        self.assertRegex(captured[0], r"\[\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\.\d{3}\]")
        self.assertRegex(captured[0], r"\[\+\d+\.\d{3}ms\]")

    def test_multiple_bridges(self):
        output_a = []
        output_b = []
        output_c = []

        def bridge_a(text):
            output_a.append(text)

        def bridge_b(text):
            output_b.append(text)

        def bridge_c(text):
            output_c.append(text)

        exposure = Expresso.exposure("BridgeTest")

        Expresso.setBridges(bridge_a, bridge_b)
        exposure.l1(ExposureCategory.DEBUG, "Broadcast test")

        self.assertTrue(output_a)
        self.assertEqual(output_a, output_b)
        self.assertEqual(output_c, [])

        output_a.clear()
        output_b.clear()

        Expresso.addBridge(bridge_c)
        exposure.l1(ExposureCategory.DEBUG, "Add test")

        self.assertTrue(output_a)
        self.assertTrue(output_b)
        self.assertTrue(output_c)

        output_a.clear()
        output_b.clear()
        output_c.clear()

        self.assertTrue(Expresso.removeBridge(bridge_b))
        exposure.l1(ExposureCategory.DEBUG, "Remove test")

        self.assertTrue(output_a)
        self.assertEqual(output_b, [])
        self.assertTrue(output_c)

        self.assertFalse(Expresso.removeBridge(bridge_b))

        Expresso.clearBridges()

        self.assertFalse(Expresso.removeBridge(bridge_a))
        self.assertFalse(Expresso.removeBridge(bridge_c))

        output_a.clear()
        output_b.clear()
        output_c.clear()

        Expresso.setBridges(bridge_b, bridge_c)
        Expresso.setBridge(bridge_a)
        exposure.l1(ExposureCategory.DEBUG, "Replacement test")

        self.assertTrue(output_a)
        self.assertEqual(output_b, [])
        self.assertEqual(output_c, [])

    def test_bridge_failure_isolation(self):
        output_a = []
        output_b = []

        def failing_bridge(text):
            raise RuntimeError("Intentional bridge failure")

        def bridge_a(text):
            output_a.append(text)

        def bridge_b(text):
            output_b.append(text)

        exposure = Expresso.exposure("BridgeFailureTest")

        Expresso.setBridges(failing_bridge, bridge_a, bridge_b)

        try:
            exposure.l1(ExposureCategory.DEBUG, "Failure isolation test")
        finally:
            Expresso.clearBridges()

        self.assertTrue(output_a, "Bridge A should receive output after a failure")
        self.assertTrue(output_b, "Bridge B should receive output after a failure")
        self.assertEqual(output_a, output_b)

        # PID / TID TAGS

    def test_pid_and_tid_tags_are_disabled_by_default(self):
        captured = self.capture()
        exposure = Expresso.exposure("ProcessTest")

        exposure.l1(ExposureCategory.SYSTEMLOG, "Default metadata")

        self.assertEqual(len(captured), 1)
        self.assertNotIn(f"[PID:{os.getpid()}]", captured[0])
        self.assertNotIn(f"[TID:{get_native_id()}]", captured[0])

    def test_pid_and_tid_tags(self):
        captured = self.capture()
        exposure = Expresso.exposure("ProcessTest")

        Expresso.enablePIDTag(True)
        Expresso.enableTIDTag(True)

        exposure.l1(ExposureCategory.SYSTEMLOG, "Both tags enabled")

        self.assertEqual(len(captured), 1)
        self.assertIn(f"[PID:{os.getpid()}]", captured[0])
        self.assertIn(f"[TID:{get_native_id()}]", captured[0])
        self.assertIn("Both tags enabled", captured[0])

    def test_pid_and_tid_tags_toggle_independently(self):
        captured = self.capture()
        exposure = Expresso.exposure("ProcessTest")

        # PID only
        Expresso.enablePIDTag(True)
        Expresso.enableTIDTag(False)

        exposure.l1(ExposureCategory.SYSTEMLOG, "PID only")

        self.assertIn(f"[PID:{os.getpid()}]", captured[-1])
        self.assertNotIn("[TID:", captured[-1])

        # TID only
        Expresso.enablePIDTag(False)
        Expresso.enableTIDTag(True)

        exposure.l1(ExposureCategory.SYSTEMLOG, "TID only")

        self.assertNotIn("[PID:", captured[-1])
        self.assertIn(f"[TID:{get_native_id()}]", captured[-1])

        # Neither
        Expresso.enablePIDTag(False)
        Expresso.enableTIDTag(False)

        exposure.l1(ExposureCategory.SYSTEMLOG, "Neither tag")

        self.assertNotIn("[PID:", captured[-1])
        self.assertNotIn("[TID:", captured[-1])

        self.assertEqual(len(captured), 3)

    def test_thread_id_is_captured_at_emission(self):
        captured = self.capture()
        exposure = Expresso.exposure("ThreadTest")
        worker_ids = []

        Expresso.enablePIDTag(True)
        Expresso.enableTIDTag(True)

        main_tid = get_native_id()

        def worker():
            worker_ids.append(get_native_id())
            exposure.l1(
                ExposureCategory.SYSTEMLOG,
                "Worker thread event"
            )

        thread = Thread(target=worker)
        thread.start()
        thread.join()

        self.assertEqual(len(captured), 1)
        self.assertEqual(len(worker_ids), 1)

        worker_tid = worker_ids[0]

        self.assertNotEqual(worker_tid, main_tid)
        self.assertIn(f"[PID:{os.getpid()}]", captured[0])
        self.assertIn(f"[TID:{worker_tid}]", captured[0])
        self.assertIn("Worker thread event", captured[0])

    def test_diagnostic_tags_include_pid_and_tid(self):
        captured = self.capture()

        Expresso.enablePIDTag(True)
        Expresso.enableTIDTag(True)
        Expresso.enableDiagnosticTags(True)

        Expresso.error("Diagnostic process metadata")

        self.assertEqual(len(captured), 1)
        self.assertIn(f"[PID:{os.getpid()}]", captured[0])
        self.assertIn(f"[TID:{get_native_id()}]", captured[0])
        self.assertIn("Diagnostic process metadata", captured[0])

        # TID FILTER

    def test_tid_filter_allows_only_selected_thread(self):
        captured = self.capture()
        exposure = Expresso.exposure("TIDFilterTest")

        main_tid = get_native_id()
        worker_ids = []

        Expresso.setTIDFilter(main_tid)

        exposure.l1(
            ExposureCategory.SYSTEMLOG,
            "Main thread event"
        )

        def worker():
            worker_ids.append(get_native_id())
            exposure.l1(
                ExposureCategory.SYSTEMLOG,
                "Worker thread event"
            )

        thread = Thread(target=worker)
        thread.start()
        thread.join()

        self.assertEqual(len(worker_ids), 1)
        self.assertNotEqual(worker_ids[0], main_tid)

        self.assertEqual(len(captured), 1)
        self.assertIn("Main thread event", captured[0])
        self.assertNotIn(
            "Worker thread event",
            "".join(captured)
        )

    def test_clear_tid_filter_allows_all_threads(self):
        captured = self.capture()
        exposure = Expresso.exposure("TIDFilterTest")

        main_tid = get_native_id()
        worker_ids = []

        Expresso.setTIDFilter(main_tid)

        def worker():
            worker_ids.append(get_native_id())
            exposure.l1(
                ExposureCategory.SYSTEMLOG,
                "Filtered worker event"
            )

        thread = Thread(target=worker)
        thread.start()
        thread.join()

        self.assertEqual(captured, [])

        Expresso.clearTIDFilter()

        thread = Thread(target=worker)
        thread.start()
        thread.join()

        self.assertEqual(len(worker_ids), 2)
        self.assertNotEqual(worker_ids[0], main_tid)
        self.assertNotEqual(worker_ids[1], main_tid)

        self.assertEqual(len(captured), 1)
        self.assertIn("Filtered worker event", captured[0])

    def test_empty_tid_filter_rejects_all_threads(self):
        captured = self.capture()
        exposure = Expresso.exposure("TIDFilterTest")

        Expresso.setTIDFilter()

        exposure.l1(
            ExposureCategory.SYSTEMLOG,
            "Main thread event"
        )

        def worker():
            exposure.l1(
                ExposureCategory.SYSTEMLOG,
                "Worker thread event"
            )

        thread = Thread(target=worker)
        thread.start()
        thread.join()

        self.assertEqual(captured, [])

if __name__ == "__main__":
    unittest.main()