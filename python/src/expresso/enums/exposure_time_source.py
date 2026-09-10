from abc import ABC, abstractmethod

class ExposureTimeSource(ABC):
    @abstractmethod
    def getTime(self) -> str: pass