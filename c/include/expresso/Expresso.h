#ifndef EXPRESSO_H

#define EXPRESSO_H

#include <stdbool.h>

#include "ExposureCategory.h"
#include "ExposureLevel.h"
#include "ExposureTimeSource.h"
#include "VariableConstants.h"

#define MAX_ENABLED_CATEGORIES 255

void expressoNewline(ExposureCategory category, ExposureLevel requiredLevel);
void expressoLinebreak(ExposureCategory category, ExposureLevel requiredLevel);

void enableIndexTag(bool enable);
void enableIdentityTag(bool enable);
void enableCategoryTag(bool enable);
void enableLevelTag(bool enable);
void enableTimestamp(bool enable);
void enableElapsedTime(bool enable);
void enableApplicationTime(bool enable);
void enableDiagnosticTags(bool enable);

void setIndexRange(long long start, long long end);
void setIndexRangeMax(long long end);
void setSingleIndex(long long targetIndex);

void setErrorStyle(const char *color, const char *prefix);
void setHereStyle(const char *color, const char *header);
void resetErrorStyle(void);
void resetHereStyle(void);

void clearIndexFilter(void);
void resetEventCounter(void);
long long getCurrentEventCount(void);

void clearAllCategories(void);
void enableCategory(ExposureCategory category);
void disableCategory(ExposureCategory category);
void setCategory(ExposureCategory category);
void setCategories(ExposureCategory category, ...);

void setLevel(ExposureLevel level);
void setLevels(ExposureLevel level, ...);

void setTimeSource(ExposureTimeSource source);

void reset(void);

void setBridge(void (*outsideSystem)(const char *));

bool isExposed(ExposureCategory category, ExposureLevel requiredLevel);

void expressoPrintf(const char *identity, ExposureCategory category, ExposureLevel requiredLevel, const char *format, ...);

void emit(const char *text);

void error(const char *format, ...);

void hereAnnounce(const char *identity, const char *label);
void hereAnnounceCustom(const char *identity, const char *customColor, const char *customHeader, const char *label);

#endif

