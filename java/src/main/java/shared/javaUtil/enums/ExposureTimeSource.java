package shared.javaUtil.enums;

@FunctionalInterface
public interface ExposureTimeSource {
    /**
     * Supplies the application's current time representation.
     *
     * The returned value 
     * may represent any concept of time:
     * real world time, game time, simulation time, ticks, frames, etc.
     */
    String getTime();
}

