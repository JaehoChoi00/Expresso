from expresso import Expresso, ExposureCategory, ExposureLevel

exposure = Expresso.exposure("ALU")

Expresso.setCategories(
    ExposureCategory.COMPONENTIAL,
    ExposureCategory.LOWERLEVEL
)

Expresso.setLevels(
    ExposureLevel.LEVEL1,
    ExposureLevel.LEVEL5
)

Expresso.enableIndexTag(True)
Expresso.enableIdentityTag(True)
Expresso.enableCategoryTag(True)
Expresso.enableLevelTag(True)

Expresso.enablePIDTag(True)
Expresso.enableTIDTag(True)

Expresso.enableTimestamp(True)
Expresso.enableElapsedTime(True)
Expresso.enableApplicationTime(False)

Expresso.setBridge(lambda rawText: print("[BRIDGE PIPELINE LOG]: " + rawText))

exposure.l1(
    ExposureCategory.COMPONENTIAL, Expresso.BOLD + "ALU Unit Initialized -> " + Expresso.RESET + 
    Expresso.FG_GREEN + "READY" + Expresso.RESET + "\n"
)

exposure.l3(ExposureCategory.COMPONENTIAL, "Intermediate Bus Clock: 400MHz\n")

exposure.l5(ExposureCategory.LOWERLEVEL, "NAND Gate Transistor Logic Validated.\n")

exposure.lbr(ExposureCategory.COMPONENTIAL, ExposureLevel.LEVEL1)

exposure.here()
exposure.here("This is the label.")

try:
    invalidOperation = 1 / 0
except Exception as exception:
    exposure.err("Execution failed during register calculation [%s]:\n", "DIV_ZERO", exception)

Expresso.clearTIDFilter()
Expresso.clearIndexFilter()
Expresso.clearBridges()

Expresso.reset()