from enum import Enum

class ExposureLevel(Enum):
    LEVEL1 = 1
    LEVEL2 = 2
    LEVEL3 = 3
    LEVEL4 = 4
    LEVEL5 = 5

    def getLevel(self) -> int: return self.value