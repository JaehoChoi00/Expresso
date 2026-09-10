package shared.javaUtil.enums;

/**
 * ExposureLevel
 * Defines the depth of information exposed by Expresso.
 */
public enum ExposureLevel {
    
    /** Conceptually: Essential execution output and user-facing information. */
    LEVEL1(1),

    /** Conceptually: Major operations, stages, and structural boundaries. */
    LEVEL2(2),

    /** Conceptually: Intermediate data, transformations, and execution flow. */
    LEVEL3(3),

    /** Conceptually: Internal calculations, component behavior, and operational states. */
    LEVEL4(4),

    /** Conceptually: Deepest meaningful operations and internal execution details. */
    LEVEL5(5);

    private final int level;

    /**
     * @param level The integer depth representation (1 to 5).
     */
    ExposureLevel(int level) {
        this.level = level;
    }

    /**
     * @return The integer level value.
     */
    public int getLevel() {
        return level;
    }
}
