class ExposureCategory:
    def __init__(self, name): self.name = name

    @staticmethod
    def of(customName):
        normalized = customName.strip().upper()
        return ExposureCategory(normalized)

    def __eq__(self, other):
        if isinstance(other, ExposureCategory):
            return self.name == other.name
        return False

    def __hash__(self): return hash(self.name)

    def __str__(self): return self.name

ExposureCategory.VANILLA = ExposureCategory("VANILLA")
ExposureCategory.SYSTEMLOG = ExposureCategory("SYSTEMLOG")
ExposureCategory.DEBUG = ExposureCategory("DEBUG")
ExposureCategory.COMPONENTIAL = ExposureCategory("COMPONENTIAL")
ExposureCategory.LOWERLEVEL = ExposureCategory("LOWERLEVEL")
ExposureCategory.BITWISE = ExposureCategory("BITWISE")
ExposureCategory.TEST = ExposureCategory("TEST")
