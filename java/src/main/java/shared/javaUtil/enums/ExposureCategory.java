package shared.javaUtil.enums;

@FunctionalInterface
public interface ExposureCategory {
    String name();

    /** Regular unfiltered raw output mode. */
    ExposureCategory VANILLA = () -> "VANILLA";

    /** System lifecycle events, runtime initialization, and low-level engine calls. */
    ExposureCategory SYSTEMLOG = () -> "SYSTEMLOG";

    /** Standard debugging traces and temporary developmental observations. */
    ExposureCategory DEBUG = () -> "DEBUG";

    /** Major architectural building blocks, stage boundaries, and subsystem pipelines (e.g., SHA-256 stages, ALU, Registers). */
    ExposureCategory COMPONENTIAL = () -> "COMPONENTIAL";

    /** Structural data transformations, intermediate arrays, 32-bit schedule dumps, and byte-level state vectors. */
    ExposureCategory LOWERLEVEL = () -> "LOWERLEVEL";

    /** Atomic bit-level operations, logic gates (XOR, AND, OR), bit shifts, and carry bit propagations. */
    ExposureCategory BITWISE = () -> "BITWISE";

    /** Test execution suites, assertions, and verification diagnostic traces. */
    ExposureCategory TEST = () -> "TEST";

    /**
     * @param categoryName The string identifier for the dynamic category (e.g., "PHYSICS", "NEURAL").
     * @return A new {@link ExposureCategory} instance bound to the normalized category name.
     */
    static ExposureCategory of(String customName) {
        String normalized = customName.trim().toUpperCase();
        return () -> normalized;
    }
}