#include "expresso/ExposureCategory.h"

/** Regular unfiltered raw output mode. */
const ExposureCategory EXPOSURE_VANILLA = {VANILLA, "VANILLA"};
/** System lifecycle events, runtime initialization, and low-level engine calls. */
const ExposureCategory EXPOSURE_SYSTEMLOG = {SYSTEMLOG, "SYSTEMLOG"};
/** Standard debugging traces and temporary developmental observations. */
const ExposureCategory EXPOSURE_DEBUG = {DEBUG, "DEBUG"};
/** Major architectural building blocks, stage boundaries, and subsystem pipelines. */
const ExposureCategory EXPOSURE_COMPONENTIAL = {COMPONENTIAL, "COMPONENTIAL"};
/** Structural data transformations, intermediate arrays, 32-bit schedule dumps, and byte-level state vectors. */
const ExposureCategory EXPOSURE_LOWERLEVEL = {LOWERLEVEL, "LOWERLEVEL"};
/** Atomic bit-level operations, logic gates (XOR, AND, OR), bit shifts, and carry bit propagations. */
const ExposureCategory EXPOSURE_BITWISE = {BITWISE, "BITWISE"};
/** Test execution suites, assertions, and verification diagnostic traces. */
const ExposureCategory EXPOSURE_TEST = {TEST, "TEST"};
