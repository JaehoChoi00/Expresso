from expresso import Expresso, Exposure, ExposureCategory, ExposureLevel

print("\nDEFAULT: ")

Expresso.printf(ExposureCategory.VANILLA, ExposureLevel.LEVEL1, "VANILLA L1\n")
Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG L1\n")
Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE L1\n")
Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL2, "TEST L2\n")


print("\nLEVEL 2: ")

Expresso.setLevel(ExposureLevel.LEVEL2)

Expresso.printf(ExposureCategory.VANILLA, ExposureLevel.LEVEL1, "VANILLA L1\n")
Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL2, "DEBUG L2\n")
Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL3, "BITWISE L3\n")


print("\nSELECTED LEVELS: ")

Expresso.setLevels(ExposureLevel.LEVEL1, ExposureLevel.LEVEL3, ExposureLevel.LEVEL5)

Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG L1\n")
Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL2, "DEBUG L2\n")
Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL3, "DEBUG L3\n")
Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL4, "DEBUG L4\n")
Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL5, "DEBUG L5\n")


print("\nCATEGORY FILTER: ")

Expresso.setLevel(ExposureLevel.LEVEL5)
Expresso.setCategory(ExposureCategory.DEBUG)

Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG\n")
Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE\n")
Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL1, "TEST\n")


print("\nMULTIPLE CATEGORIES: ")

Expresso.setCategories(ExposureCategory.DEBUG, ExposureCategory.BITWISE)

Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG\n")
Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE\n")
Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL1, "TEST\n")


print("\nCLEAR CATEGORY FILTER: ")

Expresso.clearAllCategories()
Expresso.setLevel(ExposureLevel.LEVEL1)

Expresso.printf(ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "DEBUG\n")
Expresso.printf(ExposureCategory.BITWISE, ExposureLevel.LEVEL1, "BITWISE\n")
Expresso.printf(ExposureCategory.TEST, ExposureLevel.LEVEL1, "TEST\n")


print("\nIDENTITY: ")

exposure = Expresso.exposure("TestEngine")

exposure.l1(ExposureCategory.SYSTEMLOG, "Engine started\n")
exposure.l2(ExposureCategory.COMPONENTIAL, "Loading component: %s\n", "Renderer")
exposure.l3(ExposureCategory.LOWERLEVEL, "Internal value: %d\n", 42)


print("\nGENERIC EXPOSURE: ")

exposure.expose( ExposureCategory.DEBUG, ExposureLevel.LEVEL1, "Generic exposure\n")


print("\nLAZY: ")

exposure.l1(ExposureCategory.DEBUG, lambda: "Lazy message\n")
exposure.l5(ExposureCategory.DEBUG, lambda: "Lazy deep message\n")


print("\nNEWLINE: ")

exposure.nl(ExposureCategory.DEBUG, ExposureLevel.LEVEL1)
exposure.lbr(ExposureCategory.DEBUG, ExposureLevel.LEVEL1)


print("\nERROR: ")

exposure.err("Something went wrong: %s\n", "test")

try:
    1 / 0
except Exception as exception:
    exposure.err("Division failed\n", exception)


print("\nHERE: ")

exposure.here()
exposure.here("Reached renderer initialization")


print("\nHERE CUSTOM: ")

exposure.here(
    label="Custom diagnostic marker",
    customColor=Expresso.FG_RED,
    customHeader="--> [CUSTOM REPORT]"
)


print("\nSTATIC HERE: ")

Expresso.hereAnnounce()

Expresso.hereAnnounce("StaticTest")

Expresso.hereAnnounce(identity="StaticTest", label="Static marker with identity")

Expresso.hereAnnounce(
    customColor=Expresso.FG_GREEN, customHeader="--> [GREEN REPORT]", label="Static custom marker"
)

Expresso.hereAnnounce(
    identity="StaticTest", customColor=Expresso.FG_YELLOW,
    customHeader="--> [CUSTOM REPORT]", label="Identity + custom styling"
)


print("\nTAGS: ")

Expresso.enableTimestamp(True)
Expresso.enableElapsedTime(True)

exposure.l1(ExposureCategory.SYSTEMLOG, "Timestamp and elapsed time\n")


print("\nINDEX: ")

Expresso.resetEventCounter()

exposure.l1(ExposureCategory.DEBUG, "Event 1\n")
exposure.l1(ExposureCategory.DEBUG, "Event 2\n")
exposure.l1(ExposureCategory.DEBUG, "Event 3\n")

print("Current event count:", Expresso.getCurrentEventCount())


print("\nINDEX FILTER: ")

Expresso.clearIndexFilter()
Expresso.setSingleIndex(5)

exposure.l1(ExposureCategory.DEBUG, "Event 4\n")
exposure.l1(ExposureCategory.DEBUG, "Event 5\n")
exposure.l1(ExposureCategory.DEBUG, "Event 6\n")


print("\nRESET: ")

Expresso.clearIndexFilter()
Expresso.clearAllCategories()
Expresso.setLevel(ExposureLevel.LEVEL1)

Expresso.enableTimestamp(False)
Expresso.enableElapsedTime(False)
Expresso.enableApplicationTime(False)

Expresso.setBridge(None)

print("Categories:", Expresso.getCategories())
print("Levels:", Expresso.getLevels())
print("Event count:", Expresso.getCurrentEventCount())
