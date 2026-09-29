#ifndef EXPOSURETIMESOURCE_H
#define EXPOSURETIMESOURCE_H

/**
 * Supplies the application's current time representation.
 *
 * The returned value may represent any concept of time:
 * real world time, game time, simulation time, ticks, frames, etc.
 */
typedef const char *(*ExposureTimeSource)(void);

#endif
