import unittest

from expresso import Expresso, Exposure, ExposureCategory, ExposureLevel

class TestBasicApi(unittest.TestCase):

    def setUp(self):
        Expresso.clearAllCategories()
        Expresso.setLevel(ExposureLevel.LEVEL1)
        Expresso.clearIndexFilter()
        Expresso.resetEventCounter()

        Expresso.enableTimestamp(False)
        Expresso.enableElapsedTime(False)
        Expresso.enableApplicationTime(False)

        Expresso.setBridge(None)

    def tearDown(self):
        Expresso.clearAllCategories()
        Expresso.setLevel(ExposureLevel.LEVEL1)
        Expresso.clearIndexFilter()
        Expresso.resetEventCounter()

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


if __name__ == "__main__":
    unittest.main()