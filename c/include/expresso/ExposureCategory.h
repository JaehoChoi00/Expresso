#ifndef EXPOSURECATEGORY_H
#define EXPOSURECATEGORY_H

typedef enum {
    NONE = 0,
    VANILLA = 0x01,
    SYSTEMLOG = 0x02,
    DEBUG = 0x04,
    COMPONENTIAL = 0x08,
    LOWERLEVEL = 16,
    BITWISE = 32,
    TEST = 64,
    CUSTOM = 128
} ExposureType;

typedef struct {
    ExposureType type;
    const char *name;
} ExposureCategory;

extern const ExposureCategory EXPOSURE_VANILLA;
extern const ExposureCategory EXPOSURE_SYSTEMLOG;
extern const ExposureCategory EXPOSURE_DEBUG;
extern const ExposureCategory EXPOSURE_COMPONENTIAL;
extern const ExposureCategory EXPOSURE_LOWERLEVEL;
extern const ExposureCategory EXPOSURE_BITWISE;
extern const ExposureCategory EXPOSURE_TEST;

/**
 * @param variableName The variable name to be declared.
 * @param categoryName The string identifier for the dynamic category (e.g., "PHYSICS", "NEURAL").
 * @return A new {@link ExposureCategory} instance bound to the normalized category name.
 */
#define CUSTOM_CATEGORY(variableName, categoryName) const ExposureCategory variableName = {CUSTOM, categoryName}

#endif
