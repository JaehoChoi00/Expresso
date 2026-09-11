from .expresso import Expresso
from .enums.exposure_level import ExposureLevel

class Exposure:

    def __init__(self, identity = None): self.identity = identity

    def enabled(self, category, level): return Expresso.isExposed(category, level)

    def expose(self, category, level, formatString, *args):
        if callable(formatString):
            if not Expresso.isExposed(category, level):
                return
            Expresso.printf(self.identity, category, level, "%s", formatString())
        else:
            Expresso.printf(self.identity, category, level, formatString, *args)

    def l1(self, category, formatString, *args):
        if callable(formatString):
            if not Expresso.isExposed(category, ExposureLevel.LEVEL1): return
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL1, "%s", formatString())
        else:
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL1, formatString, *args)

    def l2(self, category, formatString, *args):
        if callable(formatString):
            if not Expresso.isExposed(category, ExposureLevel.LEVEL2): return
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL2, "%s", formatString())
        else:
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL2, formatString, *args)

    def l3(self, category, formatString, *args):
        if callable(formatString):
            if not Expresso.isExposed(category, ExposureLevel.LEVEL3): return
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL3, "%s", formatString())
        else:
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL3, formatString, *args)

    def l4(self, category, formatString, *args):
        if callable(formatString):
            if not Expresso.isExposed(category, ExposureLevel.LEVEL4): return
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL4, "%s", formatString())
        else:
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL4, formatString, *args)

    def l5(self, category, formatString, *args):
        if callable(formatString):
            if not Expresso.isExposed(category, ExposureLevel.LEVEL5): return
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL5, "%s", formatString())
        else:
            Expresso.printf(self.identity, category, ExposureLevel.LEVEL5, formatString, *args)

    def nl(self, category, requiredLevel):
        Expresso.NEWLINE(category, requiredLevel)

    def lbr(self, category, requiredLevel):
        Expresso.LINEBREAK(category, requiredLevel)

    def err(self, formatString, *args):
        if callable(formatString) and not args:
            formatString = formatString()

        if self.identity is not None and str(self.identity).strip():
            formatString = "[" + str(self.identity) + "] " + formatString

        Expresso.error(formatString, *args)

    def here(self, label=None, customColor=None, customHeader=None):
        Expresso.hereAnnounce(identity=self.identity, customColor=customColor, customHeader=customHeader, label=label)

    def getIdentity(self):
        return self.identity