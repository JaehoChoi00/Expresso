#include <stdio.h>
#include <string.h>

#include "expresso/Expresso.h"
#include "expresso/Exposure.h"
#include "expresso/ExposureCategory.h"
#include "expresso/VariableConstants.h"

static void testBridge(const char *rawText) {
    if (rawText == NULL) {
        return;
    }

    printf("[BRIDGE PIPELINE LOG]: %s", rawText);
}

int main(void) {
    Exposure alu = createWithIdentity("ALU");

    reset();

    setCategories(
        EXPOSURE_COMPONENTIAL,
        EXPOSURE_LOWERLEVEL,
        (ExposureCategory){NONE, NULL}
    );

    setLevels(
        LEVEL1,
        LEVEL5,
        (ExposureLevel)0
    );

    enableTimestamp(true);
    enableElapsedTime(true);
    enablePIDTag(true);
    enableTIDTag(true);

    setBridge(testBridge);

    l1(&alu, EXPOSURE_COMPONENTIAL, BOLD "ALU Unit Initialized -> " RESET FG_GREEN "READY" RESET "\n");

    l3(&alu, EXPOSURE_COMPONENTIAL, "Intermediate Bus Clock: 400MHz\n");

    l5(&alu, EXPOSURE_LOWERLEVEL, "NAND Gate Transistor Logic Validated.\n");

    lbr(&alu, EXPOSURE_COMPONENTIAL, LEVEL1);

    here(&alu);

    hereLabel(&alu, "Reached ALU validation stage");

    err(&alu, "Execution failed during register calculation [%s]", "DIV_ZERO");

    reset();

    return 0;
}