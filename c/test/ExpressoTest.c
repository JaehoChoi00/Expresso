#include <stdio.h>

#include "expresso/Exposure.h"
#include "expresso/Expresso.h"
#include "expresso/ExposureCategory.h"
#include "expresso/VariableConstants.h"

CUSTOM_CATEGORY(CUSTOM_PHYSICS, "PHYSICS");

static int lazyCallCount = 0;
static int bridgeCallCount = 0;

static const char *lazyMessage(void) {
    lazyCallCount++;
    return "Lazy exposure\n";
}

static const char *lazyDebugMessage(void) {
    lazyCallCount++;
    return "This should not execute\n";
}

static const char *testTimeSource(void) { return "TEST-TIME"; }

static void testBridge(const char *text) {
    bridgeCallCount++;
    printf("[BRIDGE] %s", text);
}

int main(void) {
    Exposure exposure = createWithIdentity("Test");
    Exposure anonymous = create();

    printf("EXPRESSO C TEST SUITE\n");

    printf(" Identity \n\n");

    printf("Named identity: %s\n", getIdentity(&exposure));
    printf("Anonymous identity: %s\n", getIdentity(&anonymous) != NULL ? getIdentity(&anonymous) : "(NULL)");

    l1(&exposure, EXPOSURE_TEST, "Named exposure\n");
    l1(&anonymous, EXPOSURE_TEST, "Anonymous exposure\n");

    printf("\n");

    printf(" Basic Exposure \n\n");

    setLevel(LEVEL3);
    setCategories(EXPOSURE_TEST, EXPOSURE_DEBUG, (ExposureCategory){NONE, NULL});

    l1(&exposure, EXPOSURE_TEST, "Level 1 exposure\n");
    l2(&exposure, EXPOSURE_TEST, "Level 2 exposure\n");
    l3(&exposure, EXPOSURE_TEST, "Level 3 exposure\n");
    l4(&exposure, EXPOSURE_TEST, "Level 4 filtered\n");
    l5(&exposure, EXPOSURE_TEST, "Level 5 filtered\n");

    l1(&exposure, EXPOSURE_DEBUG, "DEBUG category - Level 1\n");
    l2(&exposure, EXPOSURE_DEBUG, "DEBUG category - Level 2\n");

    l1(&exposure, EXPOSURE_TEST, "TEST category\n");
    l1(&exposure, EXPOSURE_DEBUG, "DEBUG category\n");
    l1(&exposure, EXPOSURE_SYSTEMLOG, "SYSTEMLOG filtered\n");
    l1(&exposure, EXPOSURE_VANILLA, "VANILLA filtered\n");

    printf("\nCurrent event count: %lld\n\n", getCurrentEventCount());

    printf(" Formatted Exposure \n\n");

    setLevel(LEVEL5);
    setCategory(EXPOSURE_TEST);

    expose(&exposure, EXPOSURE_TEST, LEVEL1, "Integer: %d\n", 42);
    expose(&exposure, EXPOSURE_TEST, LEVEL2, "String: %s\n", "Hello");
    expose(&exposure, EXPOSURE_TEST, LEVEL3, "Float: %.2f\n", 3.14159);
    expose(&exposure, EXPOSURE_TEST, LEVEL4, "Multiple: %d %s %.1f\n", 7, "items", 2.5);

    printf("\n");

    printf(" Raw Message Preservation \n\n");

    l1(&exposure, EXPOSURE_TEST, "One line\n");
    l1(&exposure, EXPOSURE_TEST, "No newline");
    l1(&exposure, EXPOSURE_TEST, "Two\nexplicit\nnewlines\n");
    l1(&exposure, EXPOSURE_TEST, "Embedded\nmessage\ncontent");

    printf("\n");

    printf(" Category Control \n\n");

    clearAllCategories();

    l1(&exposure, EXPOSURE_TEST, "All categories enabled - TEST\n");
    l1(&exposure, EXPOSURE_DEBUG, "All categories enabled - DEBUG\n");
    l1(&exposure, EXPOSURE_SYSTEMLOG, "All categories enabled - SYSTEMLOG\n");
    l1(&exposure, EXPOSURE_VANILLA, "All categories enabled - VANILLA\n");

    setCategory(EXPOSURE_TEST);

    l1(&exposure, EXPOSURE_TEST, "TEST enabled\n");
    l1(&exposure, EXPOSURE_DEBUG, "DEBUG filtered\n");

    enableCategory(EXPOSURE_DEBUG);

    l1(&exposure, EXPOSURE_TEST, "TEST still enabled\n");
    l1(&exposure, EXPOSURE_DEBUG, "DEBUG enabled\n");

    disableCategory(EXPOSURE_TEST);

    l1(&exposure, EXPOSURE_TEST, "TEST filtered\n");
    l1(&exposure, EXPOSURE_DEBUG, "DEBUG still enabled\n");

    clearAllCategories();

    printf("\n");

    printf(" Multiple Category Selection \n\n");

    setCategories(EXPOSURE_TEST, EXPOSURE_DEBUG, EXPOSURE_SYSTEMLOG, (ExposureCategory){NONE, NULL});

    l1(&exposure, EXPOSURE_TEST, "TEST selected\n");
    l1(&exposure, EXPOSURE_DEBUG, "DEBUG selected\n");
    l1(&exposure, EXPOSURE_SYSTEMLOG, "SYSTEMLOG selected\n");
    l1(&exposure, EXPOSURE_VANILLA, "VANILLA filtered\n");

    printf("\n");

    printf(" Custom Category \n\n");

    setCategory(CUSTOM_PHYSICS);

    l1(&exposure, CUSTOM_PHYSICS, "Custom category enabled\n");
    l1(&exposure, EXPOSURE_TEST, "TEST filtered\n");

    enableCategory(EXPOSURE_TEST);

    l1(&exposure, CUSTOM_PHYSICS, "Custom category still enabled\n");
    l1(&exposure, EXPOSURE_TEST, "TEST enabled\n");

    printf("\n");

    printf(" Level Control \n\n");

    clearAllCategories();

    setLevel(LEVEL1);

    l1(&exposure, EXPOSURE_TEST, "LEVEL1 enabled\n");
    l2(&exposure, EXPOSURE_TEST, "LEVEL2 filtered\n");

    setLevel(LEVEL3);

    l1(&exposure, EXPOSURE_TEST, "LEVEL1 enabled at LEVEL3\n");
    l2(&exposure, EXPOSURE_TEST, "LEVEL2 enabled at LEVEL3\n");
    l3(&exposure, EXPOSURE_TEST, "LEVEL3 enabled at LEVEL3\n");
    l4(&exposure, EXPOSURE_TEST, "LEVEL4 filtered at LEVEL3\n");

    setLevel(LEVEL5);

    l1(&exposure, EXPOSURE_TEST, "LEVEL1 enabled at LEVEL5\n");
    l3(&exposure, EXPOSURE_TEST, "LEVEL3 enabled at LEVEL5\n");
    l5(&exposure, EXPOSURE_TEST, "LEVEL5 enabled at LEVEL5\n");

    printf("\n");

    printf(" Multiple Level Selection \n\n");

    setLevels(LEVEL1, LEVEL3, (ExposureLevel)0);

    l1(&exposure, EXPOSURE_TEST, "LEVEL1 explicitly enabled\n");
    l2(&exposure, EXPOSURE_TEST, "LEVEL2 filtered\n");
    l3(&exposure, EXPOSURE_TEST, "LEVEL3 explicitly enabled\n");
    l4(&exposure, EXPOSURE_TEST, "LEVEL4 filtered\n");
    l5(&exposure, EXPOSURE_TEST, "LEVEL5 filtered\n");

    printf("\n");

    printf(" Enabled \n\n");

    setLevel(LEVEL3);
    setCategory(EXPOSURE_TEST);

    printf("TEST / LEVEL1: %s\n", enabled(&exposure, EXPOSURE_TEST, LEVEL1) ? "YES" : "NO");
    printf("TEST / LEVEL2: %s\n", enabled(&exposure, EXPOSURE_TEST, LEVEL2) ? "YES" : "NO");
    printf("TEST / LEVEL3: %s\n", enabled(&exposure, EXPOSURE_TEST, LEVEL3) ? "YES" : "NO");
    printf("TEST / LEVEL4: %s\n", enabled(&exposure, EXPOSURE_TEST, LEVEL4) ? "YES" : "NO");
    printf("DEBUG / LEVEL1: %s\n", enabled(&exposure, EXPOSURE_DEBUG, LEVEL1) ? "YES" : "NO");

    printf("\n");

    printf(" Lazy Exposure \n\n");

    lazyCallCount = 0;

    setLevel(LEVEL3);
    setCategory(EXPOSURE_TEST);

    l1Lazy(&exposure, EXPOSURE_TEST, lazyMessage);
    l2Lazy(&exposure, EXPOSURE_TEST, lazyMessage);
    l3Lazy(&exposure, EXPOSURE_TEST, lazyMessage);
    l4Lazy(&exposure, EXPOSURE_TEST, lazyDebugMessage);

    printf("Lazy callback executions: %d\n", lazyCallCount);

    printf("\n");

    printf(" Index Filtering \n\n");

    reset();
    setLevel(LEVEL5);

    printf("--- Full sequence ---\n");

    l1(&exposure, EXPOSURE_TEST, "Index 1\n");
    l1(&exposure, EXPOSURE_TEST, "Index 2\n");
    l1(&exposure, EXPOSURE_TEST, "Index 3\n");
    l1(&exposure, EXPOSURE_TEST, "Index 4\n");
    l1(&exposure, EXPOSURE_TEST, "Index 5\n");

    printf("Total event count: %lld\n\n", getCurrentEventCount());

    printf("--- Range 2-4 ---\n");

    resetEventCounter();
    clearIndexFilter();
    setIndexRange(2, 4);

    l1(&exposure, EXPOSURE_TEST, "Range 1\n");
    l1(&exposure, EXPOSURE_TEST, "Range 2\n");
    l1(&exposure, EXPOSURE_TEST, "Range 3\n");
    l1(&exposure, EXPOSURE_TEST, "Range 4\n");
    l1(&exposure, EXPOSURE_TEST, "Range 5\n");

    printf("Event count after range: %lld\n\n", getCurrentEventCount());

    printf("--- Single Index 3 ---\n");

    resetEventCounter();
    clearIndexFilter();
    setSingleIndex(3);

    l1(&exposure, EXPOSURE_TEST, "Single 1\n");
    l1(&exposure, EXPOSURE_TEST, "Single 2\n");
    l1(&exposure, EXPOSURE_TEST, "Single 3\n");
    l1(&exposure, EXPOSURE_TEST, "Single 4\n");
    l1(&exposure, EXPOSURE_TEST, "Single 5\n");

    printf("Event count after single index: %lld\n\n", getCurrentEventCount());

    printf("--- Maximum Index 3 ---\n");

    resetEventCounter();
    clearIndexFilter();
    setIndexRangeMax(3);

    l1(&exposure, EXPOSURE_TEST, "Maximum 1\n");
    l1(&exposure, EXPOSURE_TEST, "Maximum 2\n");
    l1(&exposure, EXPOSURE_TEST, "Maximum 3\n");
    l1(&exposure, EXPOSURE_TEST, "Maximum 4\n");
    l1(&exposure, EXPOSURE_TEST, "Maximum 5\n");

    printf("Event count after maximum: %lld\n\n", getCurrentEventCount());

    clearIndexFilter();

    printf(" Newline / Line Break Helpers \n\n");

    l1(&exposure, EXPOSURE_TEST, "Before nl");
    nl(&exposure, EXPOSURE_TEST, LEVEL1);
    l1(&exposure, EXPOSURE_TEST, "After nl");

    l1(&exposure, EXPOSURE_TEST, "Before lbr");
    lbr(&exposure, EXPOSURE_TEST, LEVEL1);
    l1(&exposure, EXPOSURE_TEST, "After lbr");

    printf("\n");

    printf(" Error \n\n");

    err(&exposure, "Something went wrong");
    err(&exposure, "Invalid value: %d", 42);
    err(&anonymous, "Anonymous exposure error");

    printf("\n");

    printf(" Error Lazy \n\n");

    lazyCallCount = 0;

    errLazy(&exposure, lazyMessage);

    printf("Lazy error callback executions: %d\n", lazyCallCount);

    printf("\n");

    printf(" Error Style Customization \n\n");

    setErrorStyle(BOLD FG_MAGENTA, "[CUSTOM ERROR]: ");

    err(&exposure, "Custom error style");
    err(&exposure, "Formatted custom error: %d", 123);

    resetErrorStyle();

    err(&exposure, "Default error style restored");

    printf("\n");

    printf(" HERE \n\n");

    here(&exposure);
    hereLabel(&exposure, "Custom checkpoint");

    printf("\n");

    printf(" HERE Customization \n\n");

    hereCustom(&exposure, BOLD FG_RED, "--> [CUSTOM REPORT]", "Custom HERE checkpoint");

    resetHereStyle();

    here(&exposure);

    printf("\n");

    printf(" Tag Controls \n\n");

    enableIndexTag(false);
    enableIdentityTag(false);
    enableCategoryTag(false);
    enableLevelTag(false);

    l1(&exposure, EXPOSURE_TEST, "All normal tags disabled\n");

    enableIndexTag(true);
    enableIdentityTag(true);
    enableCategoryTag(true);
    enableLevelTag(true);

    l1(&exposure, EXPOSURE_TEST, "All normal tags enabled\n");

    printf("\n");

    printf(" Individual Tag Combinations \n\n");

    enableIndexTag(false);
    l1(&exposure, EXPOSURE_TEST, "Index hidden\n");

    enableIndexTag(true);
    enableIdentityTag(false);
    l1(&exposure, EXPOSURE_TEST, "Identity hidden\n");

    enableIdentityTag(true);
    enableCategoryTag(false);
    l1(&exposure, EXPOSURE_TEST, "Category hidden\n");

    enableCategoryTag(true);
    enableLevelTag(false);
    l1(&exposure, EXPOSURE_TEST, "Level hidden\n");

    enableLevelTag(true);

    printf("\n");

    printf(" Diagnostic Tags \n\n");

    enableDiagnosticTags(true);
    enableTimestamp(true);
    enableElapsedTime(true);
    enableApplicationTime(true);

    setTimeSource(testTimeSource);

    l1(&exposure, EXPOSURE_TEST, "All diagnostic tags enabled\n");

    enableTimestamp(false);

    l1(&exposure, EXPOSURE_TEST, "Timestamp disabled\n");

    enableElapsedTime(false);

    l1(&exposure, EXPOSURE_TEST, "Elapsed time disabled\n");

    enableApplicationTime(false);
    enableDiagnosticTags(false);

    printf("\n");

    printf(" Direct Emit \n\n");

    emit("Direct emit test\n");
    emit("Direct emit without newline");

    printf("\n\n External Bridge \n\n");

    bridgeCallCount = 0;

    setBridge(testBridge);

    l1(&exposure, EXPOSURE_TEST, "Bridge exposure\n");
    err(&exposure, "Bridge error");
    hereLabel(&exposure, "Bridge HERE");

    printf("Bridge calls: %d\n", bridgeCallCount);

    setBridge(NULL);

    printf("Bridge disabled; stdout restored.\n");

    l1(&exposure, EXPOSURE_TEST, "After bridge restored\n");

    printf("\n");

    printf(" Event Counter \n\n");

    resetEventCounter();

    printf("After reset: %lld\n", getCurrentEventCount());

    l1(&exposure, EXPOSURE_TEST, "Counter 1\n");
    l1(&exposure, EXPOSURE_TEST, "Counter 2\n");

    printf("After two exposures: %lld\n", getCurrentEventCount());

    resetEventCounter();

    printf("After second reset: %lld\n", getCurrentEventCount());

    printf("\n");

    printf(" Reset Semantics \n\n");

    setLevel(LEVEL5);
    setCategory(EXPOSURE_DEBUG);

    enableIndexTag(false);
    enableIdentityTag(false);
    enableCategoryTag(false);
    enableLevelTag(false);

    enableDiagnosticTags(true);
    enableTimestamp(true);
    enableElapsedTime(true);
    enableApplicationTime(true);

    setTimeSource(testTimeSource);
    setIndexRange(5, 10);
    setBridge(testBridge);

    reset();

    printf("Event count after reset: %lld\n", getCurrentEventCount());
    printf("TEST / LEVEL1 after reset: %s\n", enabled(&exposure, EXPOSURE_TEST, LEVEL1) ? "YES" : "NO");
    printf("TEST / LEVEL2 after reset: %s\n", enabled(&exposure, EXPOSURE_TEST, LEVEL2) ? "YES" : "NO");
    printf("TEST / LEVEL5 after reset: %s\n", enabled(&exposure, EXPOSURE_TEST, LEVEL5) ? "YES" : "NO");
    printf("DEBUG / LEVEL1 after reset: %s\n", enabled(&exposure, EXPOSURE_DEBUG, LEVEL1) ? "YES" : "NO");

    enableIndexTag(true);
    enableIdentityTag(true);
    enableCategoryTag(true);
    enableLevelTag(true);

    l1(&exposure, EXPOSURE_TEST, "Default state after reset\n");

    printf("\n");

    printf(" Custom Styles Survive Reset \n\n");

    setErrorStyle(BOLD FG_MAGENTA, "[PERSISTENT ERROR]: ");
    setHereStyle(BOLD FG_RED, "--> [PERSISTENT HERE]");

    reset();

    err(&exposure, "Custom error style after reset");
    here(&exposure);

    resetErrorStyle();
    resetHereStyle();

    printf("\n");

    printf(" Final Reset \n\n");

    reset();

    printf("Final event count: %lld\n", getCurrentEventCount());

    printf("TEST SUITE COMPLETE\n");

    return 0;
}
