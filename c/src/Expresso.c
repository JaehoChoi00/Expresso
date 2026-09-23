#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <stdbool.h>
#include <stdatomic.h>
#include <pthread.h>
#include <time.h>
#include <limits.h>

#include "expresso/Expresso.h"
#include "expresso/ExposureCategory.h"
#include "expresso/ExposureLevel.h"
#include "expresso/ExposureTimeSource.h"
#include "expresso/VariableConstants.h"

#define MAX_ENABLED_CATEGORIES 255
#define MAX_ENABLED_LEVELS 5
#define EXPOSURE_BUFFER_SIZE 4096

#define END_CATEGORY ((ExposureCategory){NONE, NULL})
#define END_LEVEL ((ExposureLevel)0)

static atomic_llong globalEventCounter = 0;

static long long minIndex = 0;
static long long maxIndex = LLONG_MAX;

static bool showIndexTag = true;
static bool showIdentityTag = true;
static bool showCategoryTag = true;
static bool showLevelTag = true;

static bool showTimestampTag = false;
static bool showElapsedTimeTag = false;
static bool showApplicationTimeTag = false;
static bool showDiagnosticTags = false;

static ExposureCategory enabledCategories[MAX_ENABLED_CATEGORIES];
static size_t enabledCategoryCount = 0;

static ExposureLevel enabledLevels[MAX_ENABLED_LEVELS] = {LEVEL1};
static size_t enabledLevelCount = 1;

static ExposureTimeSource timeSource = NULL;

static void (*externalBridge)(const char *) = NULL;

static char errorColor[128] = BOLD FG_RED;
static char errorPrefix[128] = "[ERROR]: ";

static char hereColor[128] = BOLD FG_MAGENTA;
static char hereHeader[128] = "--> [EXPRESSO REPORT HERE]";

static struct timespec runtimeStart;

static pthread_once_t runtimeInitOnce = PTHREAD_ONCE_INIT;
static pthread_mutex_t stateMutex = PTHREAD_MUTEX_INITIALIZER;

static void initializeRuntime(void) { clock_gettime(CLOCK_MONOTONIC, &runtimeStart); }

static void ensureRuntimeInitialized(void) { pthread_once(&runtimeInitOnce, initializeRuntime); }

/**
 * Emits a double newline if the specified category and level are exposed.
 *
 * @param category The category of the newline.
 * @param requiredLevel The required exposure level.
 */
void expressoNewline(ExposureCategory category, ExposureLevel requiredLevel) {
    if (!isExposed(category, requiredLevel)) return;
    emit("\n\n");
}

/**
 * Emits a visual line break if the specified category and level are exposed.
 *
 * @param category The category of the line break.
 * @param requiredLevel The required exposure level.
 */
void expressoLinebreak(ExposureCategory category, ExposureLevel requiredLevel) {
    if (!isExposed(category, requiredLevel)) return;
    emit("\n--------------------------------\n");
}

/**
 * Toggles the display of the event index tag.
 *
 * @param enable Whether to display the index tag.
 */
void enableIndexTag(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showIndexTag = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Toggles the display of the identity tag.
 *
 * @param enable Whether to display the identity tag.
 */
void enableIdentityTag(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showIdentityTag = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Toggles the display of the category tag.
 *
 * @param enable Whether to display the category tag.
 */
void enableCategoryTag(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showCategoryTag = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Toggles the display of the exposure level tag.
 *
 * @param enable Whether to display the level tag.
 */
void enableLevelTag(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showLevelTag = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Toggles the display of the timestamp tag.
 *
 * @param enable Whether to display timestamps.
 */
void enableTimestamp(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showTimestampTag = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Toggles the display of the elapsed time tag.
 *
 * @param enable Whether to display elapsed time.
 */
void enableElapsedTime(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showElapsedTimeTag = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Toggles the display of the application time tag.
 *
 * @param enable Whether to display application time.
 */
void enableApplicationTime(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showApplicationTimeTag = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Toggles the display of diagnostic tags.
 *
 * @param enable Whether to display diagnostic tags.
 */
void enableDiagnosticTags(bool enable) {
    pthread_mutex_lock(&stateMutex);
    showDiagnosticTags = enable;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Sets the active event index range.
 *
 * @param start The inclusive starting index.
 * @param end The inclusive ending index.
 */
void setIndexRange(long long start, long long end) {
    pthread_mutex_lock(&stateMutex);
    minIndex = start < 0 ? 0 : start;
    maxIndex = end < start ? start : end;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Sets the maximum event index.
 *
 * @param end The inclusive ending index.
 */
void setIndexRangeMax(long long end) {
    pthread_mutex_lock(&stateMutex);
    minIndex = 0;
    maxIndex = end;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Restricts output to a single event index.
 *
 * @param targetIndex The event index to allow.
 */
void setSingleIndex(long long targetIndex) {
    setIndexRange(targetIndex, targetIndex);
}

/**
 * Customizes the error color and prefix.
 *
 * @param color The ANSI color string. Set NULL to keep the current color.
 * @param prefix The error prefix. Set NULL to keep the current prefix.
 */
void setErrorStyle(const char *color, const char *prefix) {
    pthread_mutex_lock(&stateMutex);

    if (color != NULL) {
        snprintf(errorColor, sizeof(errorColor), "%s", color);
    }

    if (prefix != NULL) {
        snprintf(errorPrefix, sizeof(errorPrefix), "%s", prefix);
    }

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Customizes the HERE marker color and header.
 *
 * @param color The ANSI color string. Set NULL to keep the current color.
 * @param header The HERE header. Set NULL to keep the current header.
 */
void setHereStyle(const char *color, const char *header) {
    pthread_mutex_lock(&stateMutex);

    if (color != NULL) {
        snprintf(hereColor, sizeof(hereColor), "%s", color);
    }

    if (header != NULL) {
        snprintf(hereHeader, sizeof(hereHeader), "%s", header);
    }

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Resets the error style to its default values.
 */
void resetErrorStyle(void) {
    pthread_mutex_lock(&stateMutex);

    snprintf(errorColor, sizeof(errorColor), "%s", BOLD FG_RED);
    snprintf(errorPrefix, sizeof(errorPrefix), "%s", "[ERROR]: ");

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Resets the HERE style to its default values.
 */
void resetHereStyle(void) {
    pthread_mutex_lock(&stateMutex);

    snprintf(hereColor, sizeof(hereColor), "%s", BOLD FG_MAGENTA);
    snprintf(hereHeader, sizeof(hereHeader), "%s", "--> [EXPRESSO REPORT HERE]");

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Clears the active event index filter.
 */
void clearIndexFilter(void) {
    pthread_mutex_lock(&stateMutex);

    minIndex = 0;
    maxIndex = LLONG_MAX;

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Resets the global event counter to zero.
 */
void resetEventCounter(void) {
    atomic_store(&globalEventCounter, 0);
}

/**
 * Returns the current global event count.
 *
 * @return The current event count.
 */
long long getCurrentEventCount(void) {
    return atomic_load(&globalEventCounter);
}

/**
 * Compares two exposure categories.
 *
 * @param first The first category.
 * @param second The second category.
 * @return Whether the categories are equal.
 */
static bool categoriesEqual(ExposureCategory first, ExposureCategory second) {
    if (first.name == NULL || second.name == NULL) {
        return first.type == second.type;
    }

    return strcmp(first.name, second.name) == 0;
}

/**
 * Clears all enabled categories.
 */
void clearAllCategories(void) {
    pthread_mutex_lock(&stateMutex);
    enabledCategoryCount = 0;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Enables a category.
 *
 * @param category The category to enable.
 */
void enableCategory(ExposureCategory category) {
    pthread_mutex_lock(&stateMutex);

    for (size_t i = 0; i < enabledCategoryCount; i++) {
        if (categoriesEqual(enabledCategories[i], category)) {
            pthread_mutex_unlock(&stateMutex);
            return;
        }
    }

    if (enabledCategoryCount < MAX_ENABLED_CATEGORIES) {
        enabledCategories[enabledCategoryCount++] = category;
    }

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Disables a category.
 *
 * @param category The category to disable.
 */
void disableCategory(ExposureCategory category) {
    pthread_mutex_lock(&stateMutex);

    for (size_t i = 0; i < enabledCategoryCount; i++) {
        if (categoriesEqual(enabledCategories[i], category)) {
            for (size_t j = i; j + 1 < enabledCategoryCount; j++) {
                enabledCategories[j] = enabledCategories[j + 1];
            }

            enabledCategoryCount--;
            break;
        }
    }

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Sets a single enabled category.
 *
 * @param category The category to enable.
 */
void setCategory(ExposureCategory category) {
    pthread_mutex_lock(&stateMutex);

    enabledCategoryCount = 0;

    if (category.name != NULL) {
        enabledCategories[enabledCategoryCount++] = category;
    }

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Sets a single enabled exposure level.
 *
 * @param level The exposure level to enable.
 */
void setLevel(ExposureLevel level) {
    pthread_mutex_lock(&stateMutex);

    enabledLevelCount = 1;
    enabledLevels[0] = level;

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Sets multiple explicitly enabled exposure levels.
 *
 * @param level The first exposure level.
 * @param ... Additional exposure levels terminated by END_LEVEL.
 */
void setLevels(ExposureLevel level, ...) {
    va_list arguments;

    pthread_mutex_lock(&stateMutex);
    enabledLevelCount = 0;

    va_start(arguments, level);

    while (level != END_LEVEL && enabledLevelCount < MAX_ENABLED_LEVELS) {
        if (level >= LEVEL1 && level <= LEVEL5) {
            enabledLevels[enabledLevelCount++] = level;
        }

        level = va_arg(arguments, ExposureLevel);
    }

    va_end(arguments);

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Sets multiple enabled categories.
 *
 * @param category The first category.
 * @param ... Additional categories terminated by END_CATEGORY.
 */
void setCategories(ExposureCategory category, ...) {
    va_list arguments;

    pthread_mutex_lock(&stateMutex);
    enabledCategoryCount = 0;

    va_start(arguments, category);

    while (category.name != NULL && enabledCategoryCount < MAX_ENABLED_CATEGORIES) {
        enabledCategories[enabledCategoryCount++] = category;
        category = va_arg(arguments, ExposureCategory);
    }

    va_end(arguments);

    pthread_mutex_unlock(&stateMutex);
}

/**
 * Sets the application time source.
 *
 * @param source The application time source.
 */
void setTimeSource(ExposureTimeSource source) {
    pthread_mutex_lock(&stateMutex);
    timeSource = source;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Resets Expresso to its default runtime state.
 */
void reset(void) {
    pthread_mutex_lock(&stateMutex);

    enabledCategoryCount = 0;

    enabledLevelCount = 1;
    enabledLevels[0] = LEVEL1;

    minIndex = 0;
    maxIndex = LLONG_MAX;

    showTimestampTag = false;
    showElapsedTimeTag = false;
    showApplicationTimeTag = false;
    showDiagnosticTags = false;

    timeSource = NULL;
    externalBridge = NULL;

    pthread_mutex_unlock(&stateMutex);

    atomic_store(&globalEventCounter, 0);
}

/**
 * Sets the external output bridge.
 *
 * @param outsideSystem The function that receives emitted text.
 */
void setBridge(void (*outsideSystem)(const char *)) {
    pthread_mutex_lock(&stateMutex);
    externalBridge = outsideSystem;
    pthread_mutex_unlock(&stateMutex);
}

/**
 * Checks whether an exposure passes the active filters.
 *
 * @param category The exposure category.
 * @param requiredLevel The required exposure level.
 * @return Whether the exposure is enabled.
 */
bool isExposed(ExposureCategory category, ExposureLevel requiredLevel) {
    if (category.name == NULL) {
        return false;
    }

    if (requiredLevel < LEVEL1 || requiredLevel > LEVEL5) {
        return false;
    }

    pthread_mutex_lock(&stateMutex);

    if (enabledCategoryCount > 0) {
        bool categoryFound = false;

        for (size_t i = 0; i < enabledCategoryCount; i++) {
            if (categoriesEqual(enabledCategories[i], category)) {
                categoryFound = true;
                break;
            }
        }

        if (!categoryFound) {
            pthread_mutex_unlock(&stateMutex);
            return false;
        }
    }

    if (enabledLevelCount == 0) {
        pthread_mutex_unlock(&stateMutex);
        return false;
    }

    if (enabledLevelCount == 1) {
        if (enabledLevels[0] < requiredLevel) {
            pthread_mutex_unlock(&stateMutex);
            return false;
        }
    } else {
        bool levelFound = false;

        for (size_t i = 0; i < enabledLevelCount; i++) {
            if (enabledLevels[i] == requiredLevel) {
                levelFound = true;
                break;
            }
        }

        if (!levelFound) {
            pthread_mutex_unlock(&stateMutex);
            return false;
        }
    }

    pthread_mutex_unlock(&stateMutex);
    return true;
}

/**
 * Formats and emits an exposure if it passes the active filters.
 *
 * @param identity The identity associated with the exposure.
 * @param category The exposure category.
 * @param requiredLevel The required exposure level.
 * @param format The message format.
 * @param ... Arguments for the message format.
 */
void expressoPrintf(const char *identity, ExposureCategory category, ExposureLevel requiredLevel, const char *format, ...) {
    if (!isExposed(category, requiredLevel)) {
        return;
    }

    long long currentIndex = atomic_fetch_add(&globalEventCounter, 1) + 1;

    pthread_mutex_lock(&stateMutex);

    long long currentMinIndex = minIndex;
    long long currentMaxIndex = maxIndex;

    bool indexTag = showIndexTag;
    bool identityTag = showIdentityTag;
    bool categoryTag = showCategoryTag;
    bool levelTag = showLevelTag;

    pthread_mutex_unlock(&stateMutex);

    if (currentIndex < currentMinIndex || currentIndex > currentMaxIndex) {
        return;
    }

    ensureRuntimeInitialized();

    char output[EXPOSURE_BUFFER_SIZE];
    size_t offset = 0;

    pthread_mutex_lock(&stateMutex);

    bool timestampTag = showTimestampTag;
    bool elapsedTimeTag = showElapsedTimeTag;
    bool applicationTimeTag = showApplicationTimeTag;
    ExposureTimeSource currentTimeSource = timeSource;

    pthread_mutex_unlock(&stateMutex);

    bool hasMetadata = applicationTimeTag || timestampTag || elapsedTimeTag || indexTag || identityTag || categoryTag || levelTag;

    if (hasMetadata) {
        offset += snprintf(output + offset, sizeof(output) - offset, "%s", DIM);
    }

    if (applicationTimeTag && currentTimeSource != NULL) {
        const char *applicationTime = currentTimeSource();

        if (applicationTime != NULL) {
            offset += snprintf(output + offset, sizeof(output) - offset, "[%s] ", applicationTime);
        }
    }

    if (timestampTag) {
        struct timespec timestamp;
        struct tm localTime;

        clock_gettime(CLOCK_REALTIME, &timestamp);
        localtime_r(&timestamp.tv_sec, &localTime);

        offset += strftime(output + offset, sizeof(output) - offset, "[%Y-%m-%d %H:%M:%S", &localTime);
        offset += snprintf(output + offset, sizeof(output) - offset, ".%03ld] ", timestamp.tv_nsec / 1000000);
    }

    if (elapsedTimeTag) {
        struct timespec currentTime;
        clock_gettime(CLOCK_MONOTONIC, &currentTime);

        long long seconds = currentTime.tv_sec - runtimeStart.tv_sec;
        long long nanoseconds = currentTime.tv_nsec - runtimeStart.tv_nsec;

        if (nanoseconds < 0) {
            seconds--;
            nanoseconds += 1000000000LL;
        }

        double elapsed = (double)seconds + ((double)nanoseconds / 1000000000.0);

        offset += snprintf(output + offset, sizeof(output) - offset, "[%.3fs] ", elapsed);
    }

    if (indexTag) {
        offset += snprintf(output + offset, sizeof(output) - offset, "[#%lld] ", currentIndex);
    }

    if (identityTag && identity != NULL && identity[0] != '\0') {
        offset += snprintf(output + offset, sizeof(output) - offset, "[%s] ", identity);
    }

    if (categoryTag && category.name != NULL) {
        offset += snprintf(output + offset, sizeof(output) - offset, "[%s] ", category.name);
    }

    if (levelTag) {
        offset += snprintf(output + offset, sizeof(output) - offset, "[LEVEL%d] ", requiredLevel);
    }

    if (hasMetadata) {
        offset += snprintf(output + offset, sizeof(output) - offset, "%s", RESET);
    }

    if (offset >= sizeof(output)) {
        output[sizeof(output) - 1] = '\0';
        emit(output);
        return;
    }

    va_list arguments;

    va_start(arguments, format);
    vsnprintf(output + offset, sizeof(output) - offset, format, arguments);
    va_end(arguments);

    emit(output);
}

/**
 * Emits text through the external bridge or standard output.
 *
 * @param text The text to emit.
 */
void emit(const char *text) {
    if (text == NULL) {
        return;
    }

    pthread_mutex_lock(&stateMutex);
    void (*bridge)(const char *) = externalBridge;
    pthread_mutex_unlock(&stateMutex);

    if (bridge != NULL) {
        bridge(text);
    } else {
        fputs(text, stdout);
        fflush(stdout);
    }
}

/**
 * Appends diagnostic tags to a buffer.
 *
 * @param str_buffer The destination buffer.
 * @param max_size The size of the destination buffer.
 */
static void appendDiagnosticTags(char *str_buffer, size_t max_size) {
    if (str_buffer == NULL || max_size == 0) {
        return;
    }

    str_buffer[0] = '\0';

    pthread_mutex_lock(&stateMutex);

    bool diagnostics = showDiagnosticTags;
    bool timestampTag = showTimestampTag;
    bool elapsedTimeTag = showElapsedTimeTag;
    bool applicationTimeTag = showApplicationTimeTag;
    ExposureTimeSource currentTimeSource = timeSource;

    pthread_mutex_unlock(&stateMutex);

    if (!diagnostics) {
        return;
    }

    size_t offset = 0;

    if (applicationTimeTag && currentTimeSource != NULL) {
        const char *applicationTime = currentTimeSource();

        if (applicationTime != NULL) {
            offset += snprintf(str_buffer + offset, max_size - offset, "[%s] ", applicationTime);
        }
    }

    if (timestampTag && offset < max_size) {
        struct timespec timestamp;
        struct tm localTime;

        clock_gettime(CLOCK_REALTIME, &timestamp);
        localtime_r(&timestamp.tv_sec, &localTime);

        offset += strftime(str_buffer + offset, max_size - offset, "[%Y-%m-%d %H:%M:%S", &localTime);
        offset += snprintf(str_buffer + offset, max_size - offset, ".%03ld] ", timestamp.tv_nsec / 1000000);
    }

    if (elapsedTimeTag && offset < max_size) {
        ensureRuntimeInitialized();

        struct timespec currentTime;
        clock_gettime(CLOCK_MONOTONIC, &currentTime);

        long long seconds = currentTime.tv_sec - runtimeStart.tv_sec;
        long long nanoseconds = currentTime.tv_nsec - runtimeStart.tv_nsec;

        if (nanoseconds < 0) {
            seconds--;
            nanoseconds += 1000000000LL;
        }

        double elapsed = (double)seconds + ((double)nanoseconds / 1000000000.0);

        offset += snprintf(str_buffer + offset, max_size - offset, "[%.3fs] ", elapsed);
    }
}

/**
 * Emits an unrestricted error message.
 *
 * @param format The message format.
 * @param ... Arguments for the message format.
 */
void error(const char *format, ...) {
    char output[EXPOSURE_BUFFER_SIZE];
    char diagnostics[1024];

    appendDiagnosticTags(diagnostics, sizeof(diagnostics));

    pthread_mutex_lock(&stateMutex);

    char color[128];
    char prefix[128];

    snprintf(color, sizeof(color), "%s", errorColor);
    snprintf(prefix, sizeof(prefix), "%s", errorPrefix);

    pthread_mutex_unlock(&stateMutex);

    size_t offset = 0;

    offset += snprintf(output + offset, sizeof(output) - offset, "%s%s%s", diagnostics, color, prefix);

    va_list arguments;

    va_start(arguments, format);
    vsnprintf(output + offset, sizeof(output) - offset, format, arguments);
    va_end(arguments);

    offset = strlen(output);

    snprintf(output + offset, sizeof(output) - offset, "%s\n", RESET);

    emit(output);
}

/**
 * Emits an execution marker.
 *
 * @param identity The identity associated with the marker.
 * @param label The optional marker label.
 */
void hereAnnounce(const char *identity, const char *label) {
    char output[EXPOSURE_BUFFER_SIZE];
    char diagnostics[1024];

    appendDiagnosticTags(diagnostics, sizeof(diagnostics));

    pthread_mutex_lock(&stateMutex);

    char color[128];
    char header[128];

    snprintf(color, sizeof(color), "%s", hereColor);
    snprintf(header, sizeof(header), "%s", hereHeader);

    pthread_mutex_unlock(&stateMutex);

    size_t offset = 0;

    offset += snprintf(output + offset, sizeof(output) - offset, "%s%s%s", diagnostics, color, header);

    if (identity != NULL && identity[0] != '\0') {
        offset += snprintf(output + offset, sizeof(output) - offset, " [%s]", identity);
    }

    if (label != NULL && label[0] != '\0') {
        offset += snprintf(output + offset, sizeof(output) - offset, ": %s", label);
    }

    snprintf(output + offset, sizeof(output) - offset, "%s\n", RESET);

    emit(output);
}

/**
 * Emits an execution marker with custom styling.
 *
 * @param identity The identity associated with the marker.
 * @param customColor The optional ANSI color string.
 * @param customHeader The optional marker header.
 * @param label The optional marker label.
 */
void hereAnnounceCustom(const char *identity, const char *customColor, const char *customHeader, const char *label) {
    char output[EXPOSURE_BUFFER_SIZE];
    char diagnostics[1024];

    appendDiagnosticTags(diagnostics, sizeof(diagnostics));

    const char *color = customColor != NULL ? customColor : hereColor;
    const char *header = customHeader != NULL ? customHeader : hereHeader;

    size_t offset = 0;

    offset += snprintf(output + offset, sizeof(output) - offset, "%s%s%s", diagnostics, color, header);

    if (identity != NULL && identity[0] != '\0') {
        offset += snprintf(output + offset, sizeof(output) - offset, " [%s]", identity);
    }

    if (label != NULL && label[0] != '\0') {
        offset += snprintf(output + offset, sizeof(output) - offset, ": %s", label);
    }

    snprintf(output + offset, sizeof(output) - offset, "%s\n", RESET);

    emit(output);
}
