#include <stdio.h>

#include "expresso/Expresso.h"
#include "expresso/Exposure.h"
#include "expresso/ExposureCategory.h"

int main(void) {
    Exposure exposure = createWithIdentity("LibraryTest");

    reset();
    setLevel(LEVEL1);
    setCategory(EXPOSURE_TEST);

    l1(&exposure, EXPOSURE_TEST, "Testing libexpresso.a\n");

    return 0;
}
