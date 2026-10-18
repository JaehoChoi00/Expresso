import os
from threading import Thread, get_native_id

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

print("\nMULTIPLE BRIDGES: ")

bridgeOutputA = []
bridgeOutputB = []
bridgeOutputC = []

def bridgeA(text): bridgeOutputA.append(text)

def bridgeB(text): bridgeOutputB.append(text)

def bridgeC(text): bridgeOutputC.append(text)

def failingBridge(text): raise RuntimeError("Intentional bridge failure")

Expresso.setBridges(bridgeA, bridgeB)
exposure.l1(ExposureCategory.DEBUG, "Multiple bridge test\n")

print("setBridges broadcasts:", bool(bridgeOutputA) and bridgeOutputA == bridgeOutputB)
print("Unregistered bridge excluded:", not bridgeOutputC)

bridgeOutputA.clear()
bridgeOutputB.clear()

Expresso.addBridge(bridgeC)
exposure.l1(ExposureCategory.DEBUG, "Added bridge test\n")

print("addBridge preserves existing bridges:", bool(bridgeOutputA) and bool(bridgeOutputB))
print("addBridge receives output:", bool(bridgeOutputC))

bridgeOutputA.clear()
bridgeOutputB.clear()
bridgeOutputC.clear()

removed = Expresso.removeBridge(bridgeB)
exposure.l1(ExposureCategory.DEBUG, "Removed bridge test\n")

print("removeBridge succeeds:", removed)
print("Removed bridge receives no output:", not bridgeOutputB)
print("Other bridges still receive output:", bool(bridgeOutputA) and bool(bridgeOutputC))

bridgeOutputA.clear()
bridgeOutputC.clear()

Expresso.setBridges(failingBridge, bridgeA, bridgeC)
exposure.l1(ExposureCategory.DEBUG, "Failure isolation test\n")

print("Bridge A receives output after failure:", bool(bridgeOutputA))
print("Bridge C receives output after failure:", bool(bridgeOutputC))

Expresso.clearBridges()

print("clearBridges removes all bridges:",
        not Expresso.removeBridge(failingBridge)
        and not Expresso.removeBridge(bridgeA)
        and not Expresso.removeBridge(bridgeC))

bridgeOutputA.clear()
bridgeOutputB.clear()
bridgeOutputC.clear()

Expresso.setBridge(bridgeA)
exposure.l1(ExposureCategory.DEBUG, "Single bridge test\n")

print("setBridge replaces bridges:", bool(bridgeOutputA) and not bridgeOutputB and not bridgeOutputC)

Expresso.clearBridges()


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
exposure.err(lambda: "Lazy error message\n")

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

print("\nPID AND TID TAGS: ")

Expresso.clearIndexFilter()
Expresso.resetEventCounter()

Expresso.enablePIDTag(True)
Expresso.enableTIDTag(True)

exposure.l1(ExposureCategory.SYSTEMLOG, "PID and TID enabled\n")

print("Expected PID:", os.getpid())
print("Expected TID:", get_native_id())


print("\nPID ONLY: ")

Expresso.enablePIDTag(True)
Expresso.enableTIDTag(False)

exposure.l1(ExposureCategory.SYSTEMLOG, "Only PID should appear\n")


print("\nTID ONLY: ")

Expresso.enablePIDTag(False)
Expresso.enableTIDTag(True)

exposure.l1(ExposureCategory.SYSTEMLOG, "Only TID should appear\n")


print("\nPID AND TID DISABLED: ")

Expresso.enablePIDTag(False)
Expresso.enableTIDTag(False)

exposure.l1(ExposureCategory.SYSTEMLOG, "Neither PID nor TID should appear\n")


print("\nTHREAD IDENTITY: ")

Expresso.enablePIDTag(True)
Expresso.enableTIDTag(True)

mainPID = os.getpid()
mainTID = get_native_id()

def worker():
    workerPID = os.getpid()
    workerTID = get_native_id()

    exposure.l1(
        ExposureCategory.SYSTEMLOG,
        "Exposure emitted from worker thread\n"
    )

    print("Main PID:", mainPID)
    print("Worker PID:", workerPID)
    print("Main TID:", mainTID)
    print("Worker TID:", workerTID)

workerThread = Thread(target=worker)
workerThread.start()
workerThread.join()

print("\nTID FILTER TEST: ")

Expresso.reset()

capturedOutput = []
Expresso.setBridge(capturedOutput.append)

mainTID = get_native_id()

Expresso.setTIDFilter(mainTID)

exposure.l1(
    ExposureCategory.SYSTEMLOG,
    "MAIN THREAD EVENT\n"
)

def filteredWorker():
    exposure.l1(
        ExposureCategory.SYSTEMLOG,
        "WORKER THREAD EVENT\n"
    )

workerThread = Thread(target=filteredWorker)
workerThread.start()
workerThread.join()

print("Main event emitted:", any("MAIN THREAD EVENT" in text for text in capturedOutput))

print("Worker event rejected:",not any("WORKER THREAD EVENT" in text for text in capturedOutput))

print("Main TID:", mainTID)

capturedOutput.clear()
Expresso.clearTIDFilter()

workerThread = Thread(target=filteredWorker)
workerThread.start()
workerThread.join()

print("Worker event emitted after clearing filter:",any("WORKER THREAD EVENT" in text for text in capturedOutput))

Expresso.clearBridges()
Expresso.clearTIDFilter()


print("\nRESTORE TAG SETTINGS: ")

Expresso.enablePIDTag(False)
Expresso.enableTIDTag(False)

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
