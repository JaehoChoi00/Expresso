#include <stdio.h>
#include <stdarg.h>

#include "expresso/Exposure.h"
#include "expresso/Expresso.h"

#define EXPOSURE_BUFFER_SIZE 4096

Exposure create(void) {
    Exposure exposure = {NULL};
    return exposure;
}

Exposure createWithIdentity(const char *identity) {
    Exposure exposure = {identity};
    return exposure;
}

/**
 * Conditionally formats and emits an exposure message using the bound identity.
 *
 * The message is emitted only when the specified category and exposure level
 * are currently enabled.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param level The required exposure level.
 * @param format The message format string.
 * @param ... Arguments used by the format string.
 */
void expose(Exposure *exposure, ExposureCategory category, ExposureLevel level, const char *format, ...) {
    if (exposure == NULL || format == NULL) return;
    if (!isExposed(category, level)) return;

    char message[EXPOSURE_BUFFER_SIZE];

    va_list arguments;
    va_start(arguments, format);
    vsnprintf(message, sizeof(message), format, arguments);
    va_end(arguments);

    expressoPrintf(exposure->identity, category, level, "%s", message);
}

/**
 * Shorthand for exposing a formatted message at LEVEL1.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param format The message format string.
 * @param ... Arguments used by the format string.
 */
void l1(Exposure *exposure, ExposureCategory category, const char *format, ...) {
    if (exposure == NULL || format == NULL) return;
    if (!isExposed(category, LEVEL1)) return;

    char message[EXPOSURE_BUFFER_SIZE];

    va_list arguments;
    va_start(arguments, format);
    vsnprintf(message, sizeof(message), format, arguments);
    va_end(arguments);

    expressoPrintf(exposure->identity, category, LEVEL1, "%s", message);
}

/**
 * Shorthand for exposing a formatted message at LEVEL2.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param format The message format string.
 * @param ... Arguments used by the format string.
 */
void l2(Exposure *exposure, ExposureCategory category, const char *format, ...) {
    if (exposure == NULL || format == NULL) return;
    if (!isExposed(category, LEVEL2)) return;

    char message[EXPOSURE_BUFFER_SIZE];

    va_list arguments;
    va_start(arguments, format);
    vsnprintf(message, sizeof(message), format, arguments);
    va_end(arguments);

    expressoPrintf(exposure->identity, category, LEVEL2, "%s", message);
}

/**
 * Shorthand for exposing a formatted message at LEVEL3.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param format The message format string.
 * @param ... Arguments used by the format string.
 */
void l3(Exposure *exposure, ExposureCategory category, const char *format, ...) {
    if (exposure == NULL || format == NULL) return;
    if (!isExposed(category, LEVEL3)) return;

    char message[EXPOSURE_BUFFER_SIZE];

    va_list arguments;
    va_start(arguments, format);
    vsnprintf(message, sizeof(message), format, arguments);
    va_end(arguments);

    expressoPrintf(exposure->identity, category, LEVEL3, "%s", message);
}

/**
 * Shorthand for exposing a formatted message at LEVEL4.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param format The message format string.
 * @param ... Arguments used by the format string.
 */
void l4(Exposure *exposure, ExposureCategory category, const char *format, ...) {
    if (exposure == NULL || format == NULL) return;
    if (!isExposed(category, LEVEL4)) return;

    char message[EXPOSURE_BUFFER_SIZE];

    va_list arguments;
    va_start(arguments, format);
    vsnprintf(message, sizeof(message), format, arguments);
    va_end(arguments);

    expressoPrintf(exposure->identity, category, LEVEL4, "%s", message);
}

/**
 * Shorthand for exposing a formatted message at LEVEL5.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param format The message format string.
 * @param ... Arguments used by the format string.
 */
void l5(Exposure *exposure, ExposureCategory category, const char *format, ...) {
    if (exposure == NULL || format == NULL) return;
    if (!isExposed(category, LEVEL5)) return;

    char message[EXPOSURE_BUFFER_SIZE];

    va_list arguments;
    va_start(arguments, format);
    vsnprintf(message, sizeof(message), format, arguments);
    va_end(arguments);

    expressoPrintf(exposure->identity, category, LEVEL5, "%s", message);
}

/**
 * @name Lazy Exposure
 * @{
 */

/**
 * Conditionally evaluates and emits a lazy exposure message using the bound identity.
 *
 * The callback is evaluated only when the specified category and exposure level
 * pass the active exposure filters. This avoids unnecessary message construction
 * when the exposure is disabled.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param level The required exposure level.
 * @param message Callback that produces the exposure message.
 */
void exposeLazy(Exposure *exposure, ExposureCategory category, ExposureLevel level, const char *(*message)(void)) {
    if (exposure == NULL || message == NULL) return;
    if (!isExposed(category, level)) return;

    const char *result = message();
    if (result == NULL) return;

    expressoPrintf(exposure->identity, category, level, "%s", result);
}

/**
 * Lazily evaluates and exposes a message at LEVEL1.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param message Callback that produces the exposure message.
 */
void l1Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void)) {
    if (exposure == NULL || message == NULL) return;
    if (!isExposed(category, LEVEL1)) return;

    const char *result = message();
    if (result == NULL) return;

    expressoPrintf(exposure->identity, category, LEVEL1, "%s", result);
}

/**
 * Lazily evaluates and exposes a message at LEVEL2.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param message Callback that produces the exposure message.
 */
void l2Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void)) {
    if (exposure == NULL || message == NULL) return;
    if (!isExposed(category, LEVEL2)) return;

    const char *result = message();
    if (result == NULL) return;

    expressoPrintf(exposure->identity, category, LEVEL2, "%s", result);
}

/**
 * Lazily evaluates and exposes a message at LEVEL3.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param message Callback that produces the exposure message.
 */
void l3Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void)) {
    if (exposure == NULL || message == NULL) return;
    if (!isExposed(category, LEVEL3)) return;

    const char *result = message();
    if (result == NULL) return;

    expressoPrintf(exposure->identity, category, LEVEL3, "%s", result);
}

/**
 * Lazily evaluates and exposes a message at LEVEL4.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param message Callback that produces the exposure message.
 */
void l4Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void)) {
    if (exposure == NULL || message == NULL) return;
    if (!isExposed(category, LEVEL4)) return;

    const char *result = message();
    if (result == NULL) return;

    expressoPrintf(exposure->identity, category, LEVEL4, "%s", result);
}

/**
 * Lazily evaluates and exposes a message at LEVEL5.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param message Callback that produces the exposure message.
 */
void l5Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void)) {
    if (exposure == NULL || message == NULL) return;
    if (!isExposed(category, LEVEL5)) return;

    const char *result = message();
    if (result == NULL) return;

    expressoPrintf(exposure->identity, category, LEVEL5, "%s", result);
}

/** @} */

/**
 * Checks whether a specific category and exposure level are currently enabled.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param level The exposure level to check.
 * @return true if the category and level are currently exposed; false otherwise.
 */
bool enabled(Exposure *exposure, ExposureCategory category, ExposureLevel level) {
    if (exposure == NULL) return false;
    return isExposed(category, level);
}

/**
 * Emits a blank line when the specified category and exposure level are enabled.
 *
 * This produces the same output as the Expresso newline helper and does not
 * require a message.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param requiredLevel The required exposure level.
 */
void nl(Exposure *exposure, ExposureCategory category, ExposureLevel requiredLevel) {
    if (exposure == NULL) return;
    expressoNewline(category, requiredLevel);
}

/**
 * Emits a visual line-break separator when the specified category and exposure
 * level are enabled.
 *
 * @param exposure The exposure context.
 * @param category The exposure category.
 * @param requiredLevel The required exposure level.
 */
void lbr(Exposure *exposure, ExposureCategory category, ExposureLevel requiredLevel) {
    if (exposure == NULL) return;
    expressoLinebreak(category, requiredLevel);
}

/**
 * Reports an unrestricted Expresso error message.
 *
 * The bound identity is included in the error message when an identity is set.
 * Error messages are not affected by category or exposure-level filters.
 *
 * @param exposure The exposure context.
 * @param format The error message format string.
 * @param ... Arguments used by the format string.
 */
void err(Exposure *exposure, const char *format, ...) {
    if (exposure == NULL || format == NULL) return;

    char message[EXPOSURE_BUFFER_SIZE];

    va_list arguments;
    va_start(arguments, format);
    vsnprintf(message, sizeof(message), format, arguments);
    va_end(arguments);

    if (exposure->identity != NULL && exposure->identity[0] != '\0') {
        error("[%s] %s", exposure->identity, message);
    } else {
        error("%s", message);
    }
}

/**
 * Lazily evaluates and reports an unrestricted Expresso error message.
 *
 * The callback is evaluated immediately because errors are not subject to
 * category or exposure-level filtering.
 *
 * The bound identity is included in the error message when an identity is set.
 *
 * @param exposure The exposure context.
 * @param message Callback that produces the error message.
 */
void errLazy(Exposure *exposure, const char *(*message)(void)) {
    if (exposure == NULL || message == NULL) return;

    const char *result = message();
    if (result == NULL) return;

    if (exposure->identity != NULL && exposure->identity[0] != '\0') {
        error("[%s] %s", exposure->identity, result);
    } else {
        error("%s", result);
    }
}

/**
 * Emits a high-visibility Expresso execution marker.
 *
 * The bound identity is included when an identity is set.
 */
void here(Exposure *exposure) {
    if (exposure == NULL) return;
    hereAnnounce(exposure->identity, NULL);
}

/**
 * Emits a high-visibility Expresso execution marker with a custom label.
 *
 * The bound identity is included when an identity is set.
 *
 * @param exposure The exposure context.
 * @param label Additional context or descriptive text.
 */
void hereLabel(Exposure *exposure, const char *label) {
    if (exposure == NULL) return;
    hereAnnounce(exposure->identity, label);
}

/**
 * Emits a high-visibility Expresso execution marker with custom formatting.
 *
 * The custom color and header apply only to this invocation. The bound identity
 * is included when an identity is set.
 *
 * @param exposure The exposure context.
 * @param customColor ANSI color/style code for the marker.
 * @param customHeader Header text for the marker.
 * @param label Additional context or descriptive text.
 */
void hereCustom(Exposure *exposure, const char *customColor, const char *customHeader, const char *label) {
    if (exposure == NULL) return;
    hereAnnounceCustom(exposure->identity, customColor, customHeader, label);
}

/**
 * Retrieves the identity bound to this Exposure instance.
 *
 * @param exposure The exposure context.
 * @return The identity string, or NULL if no identity is set.
 */
const char *getIdentity(Exposure *exposure) {
    if (exposure == NULL) return NULL;
    return exposure->identity;
}
