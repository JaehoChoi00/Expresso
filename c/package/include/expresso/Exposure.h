#ifndef EXPOSURE_H
#define EXPOSURE_H

#include <stdbool.h>

#include "ExposureCategory.h"
#include "ExposureLevel.h"

typedef struct
{
    const char *identity;
} Exposure;

Exposure create(void);
Exposure createWithIdentity(const char *identity);

void expose(Exposure *exposure, ExposureCategory category, ExposureLevel level, const char *format, ...);

void l1(Exposure *exposure, ExposureCategory category, const char *format, ...);
void l2(Exposure *exposure, ExposureCategory category, const char *format, ...);
void l3(Exposure *exposure, ExposureCategory category, const char *format, ...);
void l4(Exposure *exposure, ExposureCategory category, const char *format, ...);
void l5(Exposure *exposure, ExposureCategory category, const char *format, ...);

void exposeLazy(Exposure *exposure, ExposureCategory category, ExposureLevel level, const char *(*message)(void));

void l1Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void));
void l2Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void));
void l3Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void));
void l4Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void));
void l5Lazy(Exposure *exposure, ExposureCategory category, const char *(*message)(void));

bool enabled(Exposure *exposure, ExposureCategory category, ExposureLevel level);

void nl(Exposure *exposure, ExposureCategory category, ExposureLevel requiredLevel);
void lbr(Exposure *exposure, ExposureCategory category, ExposureLevel requiredLevel);

void err(Exposure *exposure, const char *format, ...);
void errLazy(Exposure *exposure, const char *(*message)(void));

void here(Exposure *exposure);
void hereLabel(Exposure *exposure, const char *label);
void hereCustom(Exposure *exposure, const char *customColor, const char *customHeader, const char *label);

const char *getIdentity(Exposure *exposure);

#endif
