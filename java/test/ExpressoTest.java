import shared.javaUtil.Expresso;
import shared.javaUtil.Exposure;
import shared.javaUtil.enums.ExposureCategory;
import shared.javaUtil.enums.ExposureLevel;
import java.lang.management.ManagementFactory;

public class ExpressoTest {

    private static int passed = 0;
    private static int failed = 0;

    private static final Exposure test = Expresso.exposure("ExpressoTest");

    public static void main(String[] args) {

        Expresso.enableIdentityTag(false);
        Expresso.enableLevelTag(false);
        Expresso.enableIdentityTag(true);
        Expresso.enableTimestamp(true);
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "EXPRESSO TEST SUITE START" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        
        Expresso.reset();
        
        testClassesExist();
        testExposureCreation();
        testDefaultLevel();
        testCumulativeLevelFiltering();
        testExplicitSelectedLevels();
        testCategoryFilter();
        testMultipleCategories();
        testClearCategoryFilter();
        testDynamicCategory();
        testGenericExposure();
        testLazySupplier();
        testBridges();
        testNewline();
        testLinebreak();
        testError();
        testErrorWithException();
        testHere();
        testHereWithLabel();
        testCustomHereStyle();
        testTimestampAndElapsedTime();
        testEventCounter();
        testIndexFilter();
        testPIDTag();
        testTIDTag();
        testTIDFilter();
        testPIDTIDReset();
        testResetState();

        test.here("Testing complete");

        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "PASSED: " + passed + Expresso.RESET + "\n");
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "FAILED: " + failed + Expresso.RESET + "\n");
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);

        if (failed == 0) {
            test.l1(ExposureCategory.TEST,  Expresso.BOLD + "ALL TESTS PASSED." + Expresso.RESET + "\n");
        } else {
            test.l1(ExposureCategory.TEST,  Expresso.BOLD + "SOME TESTS FAILED." + Expresso.RESET + "\n");
        }
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            test.l1(ExposureCategory.TEST, "[PASS] %s\n", name);
        } else {
            failed++;
            test.err("[FAIL] %s\n", name);
        }
    }

    private static void reset() {
        Expresso.reset();

        Expresso.enableIndexTag(true);
        Expresso.enableIdentityTag(true);
        Expresso.enableCategoryTag(true);
        Expresso.enableLevelTag(true);
        Expresso.enableDiagnosticTags(false);

        Expresso.resetErrorStyle();
        Expresso.resetHereStyle();
    }

    private static void testClassesExist() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing if Classes Exist" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        check("Expresso class exists", Expresso.class != null);
        check("Exposure class exists", Exposure.class != null);
    }

    private static void testExposureCreation() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Exposure Creation With & Without Identity" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();
        
        Exposure testExposure = Expresso.exposure();

        check("Create Exposure", testExposure != null);
        check("Default identity is null", testExposure.getIdentity() == null);

        Exposure exposureWithIdentity = Expresso.exposure("TestApp");

        check("Create exposure with Identity", exposureWithIdentity != null);
        check("Identity is retrievable", "TestApp".equals(exposureWithIdentity.getIdentity()));
    }

    private static void testDefaultLevel() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing the Default Level of Expresso" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        check("Default level is LEVEL1", Expresso.getLevels().contains(ExposureLevel.LEVEL1));
        check("Only LEVEL1 is enabled by default", Expresso.getLevels().size() == 1);
    }

    private static void testCumulativeLevelFiltering() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Level Filtering" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setLevel(ExposureLevel.LEVEL3);

        check("LEVEL1 exposed at LEVEL3", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("LEVEL2 exposed at LEVEL3", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL2));
        check("LEVEL3 exposed at LEVEL3", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL3));
        check("LEVEL4 hidden at LEVEL3", !Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL4));
        check("LEVEL5 hidden at LEVEL3", !Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL5));
        
    }

    private static void testExplicitSelectedLevels() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Hand-Picked Level Filtering" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL5);

        check("LEVEL1 explicitly enabled", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("LEVEL2 explicitly hidden", !Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL2));
        check("LEVEL3 explicitly hidden", !Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL3));
        check("LEVEL4 explicitly hidden", !Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL4));
        check("LEVEL5 explicitly enabled", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL5));
    }

    private static void testCategoryFilter() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Category Filtering" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setCategory(ExposureCategory.DEBUG);

        check("DEBUG category exposed", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("SYSTEMLOG category hidden", !Expresso.isExposed(ExposureCategory.SYSTEMLOG, ExposureLevel.LEVEL1));
    }

    private static void testMultipleCategories() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Multiple Category Filtering" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setCategories(ExposureCategory.DEBUG, ExposureCategory.SYSTEMLOG);

        check("DEBUG category enabled", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("SYSTEMLOG category enabled", Expresso.isExposed(ExposureCategory.SYSTEMLOG, ExposureLevel.LEVEL1));
        check("BITWISE category hidden", !Expresso.isExposed(ExposureCategory.BITWISE, ExposureLevel.LEVEL1));
    }

    private static void testClearCategoryFilter() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Category Filter Clearing" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setCategory(ExposureCategory.DEBUG);
        Expresso.clearAllCategories();

        check("DEBUG exposed after clearing category filter", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("SYSTEMLOG exposed after clearing category filter", Expresso.isExposed(ExposureCategory.SYSTEMLOG, ExposureLevel.LEVEL1));
    }

    private static void testDynamicCategory() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Dynamic Category Creation" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        ExposureCategory physics = ExposureCategory.of("physics");

        check("Dynamic category name is normalized", "PHYSICS".equals(physics.name()));

        Expresso.setCategory(physics);

        check("Dynamic category can be enabled", Expresso.isExposed(physics, ExposureLevel.LEVEL1));
        check("DEBUG hidden by dynamic category filter", !Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
    }

    private static void testGenericExposure() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Generic Exposure Syntax" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Exposure exposure = Expresso.exposure("Test");

        exposure.expose(ExposureCategory.TEST, ExposureLevel.LEVEL1, "Hello %s", "World");

        check("Generic exposure executed", true);
    }

    private static void testLazySupplier() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Lazy Supplier" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Exposure exposure = Expresso.exposure("LazyTest");

        final boolean[] executed = {false};

        Expresso.setLevel(ExposureLevel.LEVEL1);

        exposure.l2(ExposureCategory.TEST, () -> 
                    { 
                        executed[0] = true; 
                        return "This should not execute\n"; 
                    }
                );

        check("Lazy Supplier remains unevaluated when hidden", !executed[0]);

        Expresso.setLevel(ExposureLevel.LEVEL2);

        exposure.l2(ExposureCategory.TEST, () -> 
                    {
                        executed[0] = true;
                        return "Lazy Supplier executed\n";
                    }
                );

        check("Lazy Supplier evaluates when exposed", executed[0]);
    }

    private static void testBridges() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST, Expresso.BOLD + "Testing Multiple Bridges" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        StringBuilder outputA = new StringBuilder();
        StringBuilder outputB = new StringBuilder();
        StringBuilder outputC = new StringBuilder();

        java.util.function.Consumer<String> bridgeA = outputA::append;
        java.util.function.Consumer<String> bridgeB = outputB::append;
        java.util.function.Consumer<String> bridgeC = outputC::append;

        // Test setBridges()
        Expresso.setBridges(bridgeA, bridgeB);
        test.l1(ExposureCategory.TEST, "Multi-bridge test\n");

        boolean setBridgesBroadcast = outputA.length() > 0
                && outputA.toString().equals(outputB.toString());
        boolean setBridgesExcludesUnregistered = outputC.length() == 0;

        // Test addBridge()
        outputA.setLength(0);
        outputB.setLength(0);

        Expresso.addBridge(bridgeC);
        test.l1(ExposureCategory.TEST, "Added bridge test\n");

        boolean addBridgePreservesExisting = outputA.length() > 0 && outputB.length() > 0;
        boolean addBridgeReceivesOutput = outputC.length() > 0;

        // Test removeBridge()
        outputA.setLength(0);
        outputB.setLength(0);
        outputC.setLength(0);

        boolean removed = Expresso.removeBridge(bridgeB);
        test.l1(ExposureCategory.TEST, "Removed bridge test\n");

        boolean removeBridgeStopsOutput = outputB.length() == 0;
        boolean removeBridgePreservesOthers = outputA.length() > 0 && outputC.length() > 0;

        // Test clearBridges()
        Expresso.clearBridges();

        boolean clearBridgesRemovesAll = !Expresso.removeBridge(bridgeA) && !Expresso.removeBridge(bridgeC);

        // Test setBridge()
        outputA.setLength(0);
        outputB.setLength(0);
        outputC.setLength(0);

        Expresso.setBridge(bridgeA);
        test.l1(ExposureCategory.TEST, "Single bridge test\n");

        boolean setBridgeReplacesBridges = outputA.length() > 0 && outputB.length() == 0 && outputC.length() == 0;

        // Restore normal output before reporting assertions.
        Expresso.clearBridges();

        // Test bridge failure isolation
        outputA.setLength(0);
        outputB.setLength(0);
        outputC.setLength(0);

        java.util.function.Consumer<String> failingBridge = message -> { throw new RuntimeException("Intentional bridge failure"); };

        Expresso.setBridges(failingBridge, bridgeA, bridgeB);

        boolean dispatchSurvivedFailure;

        try {
            test.l1(ExposureCategory.TEST, "Bridge failure isolation test\n");
            dispatchSurvivedFailure = true;
        } catch (RuntimeException exception) {
            dispatchSurvivedFailure = false;
        }

        boolean bridgeAReceivedOutput = outputA.length() > 0;
        boolean bridgeBReceivedOutput = outputB.length() > 0;

        Expresso.clearBridges();

        check("bridge failure does not abort dispatch", dispatchSurvivedFailure);
        check("bridge A receives output after bridge failure", bridgeAReceivedOutput);
        check("bridge B receives output after bridge failure", bridgeBReceivedOutput);
        check("setBridges sends output to every bridge", setBridgesBroadcast);
        check("setBridges excludes unregistered bridges", setBridgesExcludesUnregistered);
        check("addBridge preserves existing bridges", addBridgePreservesExisting);
        check("addBridge sends output to the new bridge", addBridgeReceivesOutput);
        check("removeBridge returns true for a registered bridge", removed);
        check("removeBridge stops output to the removed bridge", removeBridgeStopsOutput);
        check("removeBridge preserves other bridges", removeBridgePreservesOthers);
        check("clearBridges removes all bridges", clearBridgesRemovesAll);
        check("setBridge replaces bridges with one bridge", setBridgeReplacesBridges);

        reset();
    }



    private static void testNewline() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing NewLine Utility Function" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);

        check("NEWLINE executed", true);
    }

    private static void testLinebreak() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing LineBreak Utility Function" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        check("LINEBREAK executed", true);
    }

    private static void testError() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Error Utility" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.err("Something went wrong.");

        check("ERROR executed", true);
    }

    private static void testErrorWithException() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Error Utility with Exception" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        RuntimeException exception = new RuntimeException("Test exception");

        test.err("Failure occurred.", exception);

        check("ERROR with exception executed", true);
    }

    private static void testHere() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Here Utility" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.here();

        check("HERE executed", true);
    }

    private static void testHereWithLabel() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Here Utility with Label" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.here("Application started");
        check("HERE with label executed", true);
    }

    private static void testCustomHereStyle() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Customizability of Here" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.here(Expresso.FG_CYAN, "==> [TRACE]", "Reached checkpoint");

        test.here("Testing regular [here] once more");

        check("Custom HERE style executed", true);
    }

    private static void testTimestampAndElapsedTime() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Timestamp and Elapsed Time Tags" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.enableTimestamp(true);
        Expresso.enableElapsedTime(true);

        test.l1(ExposureCategory.TEST, "Timing test\n");

        check("Timestamp and elapsed time executed", true);
    }

    private static void testEventCounter() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Event Counter" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        long beforeEvents = Expresso.getCurrentEventCount();

        test.l1(ExposureCategory.TEST, "First event\n");

        test.l1(ExposureCategory.TEST, "Second event\n");

        long afterEvents = Expresso.getCurrentEventCount();

        check("Event counter increases by two", afterEvents - beforeEvents == 2);
    }

    private static void testIndexFilter() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Index Filter" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setIndexRange(2, 3);

        test.l1(ExposureCategory.TEST, "Event 1\n");
        test.l1(ExposureCategory.TEST, "Event 2\n");
        test.l1(ExposureCategory.TEST, "Event 3\n");
        test.l1(ExposureCategory.TEST, "Event 4\n");


        check("Index counter includes all four events", Expresso.getCurrentEventCount() == 4);
    }

    private static void testPIDTag() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(
            ExposureCategory.TEST,
            Expresso.BOLD + "Testing Process ID (PID) Tag" + Expresso.RESET
        );
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        StringBuilder output = new StringBuilder();
        long expectedPID = ProcessHandle.current().pid();

        Expresso.setBridge(output::append);
        Expresso.enablePIDTag(true);

        test.l1(ExposureCategory.TEST, "PID test\n");

        boolean containsPID = output.toString().contains("[PID:" + expectedPID + "]");

        Expresso.clearBridges();

        check("PID tag contains current process ID", containsPID);

        reset();
    }


    private static void testTIDTag() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(
            ExposureCategory.TEST,
            Expresso.BOLD + "Testing Thread ID (TID) Tag" + Expresso.RESET
        );
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        StringBuilder output = new StringBuilder();
        long expectedTID = Thread.currentThread().threadId();

        Expresso.setBridge(output::append);
        Expresso.enableTIDTag(true);

        test.l1(ExposureCategory.TEST, "TID test\n");

        boolean containsTID = output.toString().contains("[TID:" + expectedTID + "]");

        Expresso.clearBridges();

        check("TID tag contains current thread ID", containsTID);

        reset();
    }


    private static void testTIDFilter() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(
            ExposureCategory.TEST,
            Expresso.BOLD + "Testing Thread ID (TID) Filtering" + Expresso.RESET
        );
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        StringBuilder output = new StringBuilder();
        long currentTID = Thread.currentThread().threadId();

        Expresso.setBridge(output::append);
        Expresso.setTIDFilter(currentTID + 1);

        test.l1(ExposureCategory.TEST, "Filtered message\n");

        boolean rejectedUnselectedThread = output.length() == 0;

        Expresso.clearTIDFilter();
        test.l1(ExposureCategory.TEST, "Allowed message\n");

        boolean acceptedAfterClearingFilter = output.toString().contains("Allowed message");

        boolean filteredMessageAbsent = !output.toString().contains("Filtered message");

        Expresso.clearBridges();

        check(
            "TID filter rejects an unselected thread",
            rejectedUnselectedThread
        );

        check(
            "Filtered message remains absent",
            filteredMessageAbsent
        );

        check(
            "Clearing TID filter restores output",
            acceptedAfterClearingFilter
        );

        reset();
    }


    private static void testPIDTIDReset() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(
            ExposureCategory.TEST,
            Expresso.BOLD + "Testing PID and TID Reset Behavior" + Expresso.RESET
        );
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.enablePIDTag(true);
        Expresso.enableTIDTag(true);
        Expresso.setTIDFilter(Thread.currentThread().threadId());

        Expresso.reset();

        StringBuilder output = new StringBuilder();
        Expresso.setBridge(output::append);

        test.l1(ExposureCategory.TEST, "After reset\n");

        String result = output.toString();

        boolean pidTagDisabled = !result.contains("[PID:");
        boolean tidTagDisabled = !result.contains("[TID:");
        boolean tidFilterCleared = result.contains("After reset");

        Expresso.clearBridges();

        check("PID tag disabled after reset", pidTagDisabled);
        check("TID tag disabled after reset", tidTagDisabled);
        check("TID filter cleared after reset", tidFilterCleared);

        reset();
    }

    private static void testResetState() {
        reset();
        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "Testing Reset Function" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setCategories(ExposureCategory.DEBUG, ExposureCategory.SYSTEMLOG);

        Expresso.setLevel(ExposureLevel.LEVEL5);
        Expresso.setIndexRange(10, 20);
        Expresso.enableTimestamp(true);
        Expresso.enableElapsedTime(true);
        Expresso.reset();

        long countAfterReset = Expresso.getCurrentEventCount();

        check("Categories reset", Expresso.getCategories().isEmpty());
        check("Level reset to LEVEL1", Expresso.getLevels().contains(ExposureLevel.LEVEL1));
        check("Event counter was reset", countAfterReset == 0);
        check("LEVEL1 exposed after reset", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("LEVEL2 hidden after reset", !Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL2));
    }

}