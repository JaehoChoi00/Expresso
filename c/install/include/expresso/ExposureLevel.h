#ifndef EXPOSURELEVEL_H
#define EXPOSURELEVEL_H

typedef enum {
    LEVEL1 = 1, // Final & essential result
    LEVEL2 = 2, // Major operation being performed
    LEVEL3 = 3, // Important intermediate values
    LEVEL4 = 4, // Internal operations of the component
    LEVEL5 = 5  // Lowest-level operations & individual transformations
} ExposureLevel;

#endif
