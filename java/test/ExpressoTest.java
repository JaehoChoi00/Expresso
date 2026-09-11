import shared.javaUtil.Expresso;
import shared.javaUtil.Exposure;
import shared.javaUtil.enums.ExposureCategory;
import shared.javaUtil.enums.ExposureLevel;

public class ExpressoTest {

    private static int passed = 0;
    private static int failed = 0;

    private static final Exposure test = Expresso.exposure("ExpressoTest");

    public static void main(String[] args) {

        Expresso.enableIdentityTag(false);
        Expresso.enableLevelTag(false);
        Expresso.enableIdentityTag(true);
        Expresso.enableTimestamp(true);

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
        testResetState();

        test.here("\nTesting complete");

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
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting if Classes Exist" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        check("Expresso class exists", Expresso.class != null);
        check("Exposure class exists", Exposure.class != null);
    }

    private static void testExposureCreation() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Exposure Creation With & Without Identity" + Expresso.RESET);
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
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting the Default Level of Expresso" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        check("Default level is LEVEL1", Expresso.getLevels().contains(ExposureLevel.LEVEL1));
        check("Only LEVEL1 is enabled by default", Expresso.getLevels().size() == 1);
    }

    private static void testCumulativeLevelFiltering() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Level Filtering" + Expresso.RESET);
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
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Hand-Picked Level Filtering" + Expresso.RESET);
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
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Category Filtering" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setCategory(ExposureCategory.DEBUG);

        check("DEBUG category exposed", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("SYSTEMLOG category hidden", !Expresso.isExposed(ExposureCategory.SYSTEMLOG, ExposureLevel.LEVEL1));
    }

    private static void testMultipleCategories() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Multiple Category Filtering" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setCategories(ExposureCategory.DEBUG, ExposureCategory.SYSTEMLOG);

        check("DEBUG category enabled", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("SYSTEMLOG category enabled", Expresso.isExposed(ExposureCategory.SYSTEMLOG, ExposureLevel.LEVEL1));
        check("BITWISE category hidden", !Expresso.isExposed(ExposureCategory.BITWISE, ExposureLevel.LEVEL1));
    }

    private static void testClearCategoryFilter() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Category Filter Clearing" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setCategory(ExposureCategory.DEBUG);
        Expresso.clearAllCategories();

        check("DEBUG exposed after clearing category filter", Expresso.isExposed(ExposureCategory.DEBUG, ExposureLevel.LEVEL1));
        check("SYSTEMLOG exposed after clearing category filter", Expresso.isExposed(ExposureCategory.SYSTEMLOG, ExposureLevel.LEVEL1));
    }

    private static void testDynamicCategory() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Dynamic Category Creation" + Expresso.RESET);
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
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Generic Exposure Syntax" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Exposure exposure = Expresso.exposure("Test");

        exposure.expose(ExposureCategory.TEST, ExposureLevel.LEVEL1, "Hello %s", "World");

        check("Generic exposure executed", true);
    }

    private static void testLazySupplier() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Lazy Supplier" + Expresso.RESET);
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

    private static void testNewline() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting NewLine Utility Function" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.nl(ExposureCategory.TEST, ExposureLevel.LEVEL1);

        check("NEWLINE executed", true);
    }

    private static void testLinebreak() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting LineBreak Utility Function" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        check("LINEBREAK executed", true);
    }

    private static void testError() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Error Utility" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.err("Something went wrong.");

        check("ERROR executed", true);
    }

    private static void testErrorWithException() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Error Utility with Exception" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        RuntimeException exception = new RuntimeException("Test exception");

        test.err("Failure occurred.", exception);

        check("ERROR with exception executed", true);
    }

    private static void testHere() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Here Utility" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.here();

        check("HERE executed", true);
    }

    private static void testHereWithLabel() {
        reset();

        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Here Utility with Label" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.here("Application started");
        check("HERE with label executed", true);
    }

    private static void testCustomHereStyle() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Customizability of Here" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        test.here(Expresso.FG_CYAN, "==> [TRACE]", "Reached checkpoint");

        test.here("Testing regular [here] once more");

        check("Custom HERE style executed", true);
    }

    private static void testTimestampAndElapsedTime() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Timestamp and Elapsed Time Tags" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.enableTimestamp(true);
        Expresso.enableElapsedTime(true);

        test.l1(ExposureCategory.TEST, "Timing test\n");

        check("Timestamp and elapsed time executed", true);
    }

    private static void testEventCounter() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Event Counter" + Expresso.RESET);
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
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Index Filter" + Expresso.RESET);
        test.lbr(ExposureCategory.TEST, ExposureLevel.LEVEL1);
        reset();

        Expresso.setIndexRange(2, 3);

        test.l1(ExposureCategory.TEST, "Event 1\n");
        test.l1(ExposureCategory.TEST, "Event 2\n");
        test.l1(ExposureCategory.TEST, "Event 3\n");
        test.l1(ExposureCategory.TEST, "Event 4\n");


        check("Index counter includes all four events", Expresso.getCurrentEventCount() == 4);
    }

    private static void testResetState() {
        reset();
        test.l1(ExposureCategory.TEST,  Expresso.BOLD + "\nTesting Reset Function" + Expresso.RESET);
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