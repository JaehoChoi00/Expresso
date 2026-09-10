package shared.javaUtil;

import java.util.function.Supplier;
import shared.javaUtil.enums.ExposureCategory;
import shared.javaUtil.enums.ExposureLevel;
import static shared.javaUtil.enums.ExposureLevel.*;

/**
 * An instance-bound facade and DSL handle for {@link Expresso} logging routines.
 * <p>
 * An {@code Exposure} handle encapsulates an optional identity tag (e.g., component or class name)
 * and provides shorthand methods ({@code l1}–{@code l5}, {@code err}, {@code here}) for logging events
 * with automatic identity tag injection and hot-loop safe lazy message evaluation.
 */
public final class Exposure {

    private final String identity;

    /**
     * Constructs a default package-private {@code Exposure} instance without an assigned identity tag.
     */
    Exposure() { this(null); }

    /**
     * Constructs a package-private {@code Exposure} instance bound to a specific component identity.
     *
     * @param identity The identifier tag representing the source component (e.g., "RenderEngine").
     */
    Exposure(String identity) { this.identity = identity; }

    /**
     * Conditionally formats and emits a log message using the bound identity context.
     *
     * @param category The logging category.
     * @param level    The required exposure level.
     * @param format   The string format template.
     * @param args     The format template arguments.
     */
    public void expose(ExposureCategory category, ExposureLevel level, String format, Object... args) {
        Expresso.printf(identity, category, level, format, args);
    }
/**
     * Shorthand for logging a formatted message at {@link ExposureLevel#LEVEL1}.
     *
     * @param category The logging category.
     * @param format   The string format template.
     * @param args     The format template arguments.
     */
    public void l1(ExposureCategory category, String format, Object... args) {
        Expresso.printf(identity, category, LEVEL1, format, args);
    }

    /**
     * Shorthand for logging a formatted message at {@link ExposureLevel#LEVEL2}.
     *
     * @param category The logging category.
     * @param format   The string format template.
     * @param args     The format template arguments.
     */
    public void l2(ExposureCategory category, String format, Object... args) {
        Expresso.printf(identity, category, LEVEL2, format, args);
    }

    /**
     * Shorthand for logging a formatted message at {@link ExposureLevel#LEVEL3}.
     *
     * @param category The logging category.
     * @param format   The string format template.
     * @param args     The format template arguments.
     */
    public void l3(ExposureCategory category, String format, Object... args) {
        Expresso.printf(identity, category, LEVEL3, format, args);
    }

    /**
     * Shorthand for logging a formatted message at {@link ExposureLevel#LEVEL4}.
     *
     * @param category The logging category.
     * @param format   The string format template.
     * @param args     The format template arguments.
     */
    public void l4(ExposureCategory category, String format, Object... args) {
        Expresso.printf(identity, category, LEVEL4, format, args);
    }

    /**
     * Shorthand for logging a formatted message at {@link ExposureLevel#LEVEL5}.
     *
     * @param category The logging category.
     * @param format   The string format template.
     * @param args     The format template arguments.
     */
    public void l5(ExposureCategory category, String format, Object... args) {
        Expresso.printf(identity, category, LEVEL5, format, args);
    }

    // --- Lazy Supplier Evaluation Logging Methods ---

    /**
     * Conditionally evaluates and emits a lazy log message using the bound identity context.
     * <p>
     * The {@link Supplier} is evaluated only if the category and level pass active exposure filters,
     * preventing unnecessary string allocations in performance-critical code paths.
     *
     * @param category The logging category.
     * @param level    The required exposure level.
     * @param message  A supplier yielding the log string.
     */
    public void expose(ExposureCategory category, ExposureLevel level, Supplier<String> message) {
        if (!Expresso.isExposed(category, level)) return;
        Expresso.printf(identity, category, level, "%s", message.get());
    }

    /**
     * Lazily evaluates and logs a message at {@link ExposureLevel#LEVEL1}.
     *
     * @param category The logging category.
     * @param message  A supplier yielding the log string.
     */
    public void l1(ExposureCategory category, Supplier<String> message) {
        if (!Expresso.isExposed(category, LEVEL1)) return;
        Expresso.printf(identity, category, LEVEL1, "%s", message.get());
    }

    /**
     * Lazily evaluates and logs a message at {@link ExposureLevel#LEVEL2}.
     *
     * @param category The logging category.
     * @param message  A supplier yielding the log string.
     */
    public void l2(ExposureCategory category, Supplier<String> message) {
        if (!Expresso.isExposed(category, LEVEL2)) return;
        Expresso.printf(identity, category, LEVEL2, "%s", message.get());
    }

    /**
     * Lazily evaluates and logs a message at {@link ExposureLevel#LEVEL3}.
     *
     * @param category The logging category.
     * @param message  A supplier yielding the log string.
     */
    public void l3(ExposureCategory category, Supplier<String> message) {
        if (!Expresso.isExposed(category, LEVEL3)) return;
        Expresso.printf(identity, category, LEVEL3, "%s", message.get());
    }

    /**
     * Lazily evaluates and logs a message at {@link ExposureLevel#LEVEL4}.
     *
     * @param category The logging category.
     * @param message  A supplier yielding the log string.
     */
    public void l4(ExposureCategory category, Supplier<String> message) {
        if (!Expresso.isExposed(category, LEVEL4)) return;
        Expresso.printf(identity, category, LEVEL4, "%s", message.get());
    }

    /**
     * Lazily evaluates and logs a message at {@link ExposureLevel#LEVEL5}.
     *
     * @param category The logging category.
     * @param message  A supplier yielding the log string.
     */
    public void l5(ExposureCategory category, Supplier<String> message) {
        if (!Expresso.isExposed(category, LEVEL5)) return;
        Expresso.printf(identity, category, LEVEL5, "%s", message.get());
    }

    // --- Helpers & Execution Utilities ---

    /**
     * Checks whether a specific category and level combination is currently active in Expresso.
     *
     * @param category The category to evaluate.
     * @param level    The level to evaluate.
     * @return {@code true} if exposed, {@code false} otherwise.
     */
    public boolean enabled(ExposureCategory category, ExposureLevel level) { 
        return Expresso.isExposed(category, level); 
    }

    /**
     * Emits a double Expresso.NEWLINE to output if the category and level criteria are met.
     *
     * @param category      The target category.
     * @param requiredLevel The required severity level.
     */
    public void nl(ExposureCategory category, ExposureLevel requiredLevel) {
        Expresso.NEWLINE(category, requiredLevel);
    }

    /**
     * Emits a visual Expresso.LINEBREAK separator if the category and level criteria are met.
     *
     * @param category      The target category.
     * @param requiredLevel The required severity level.
     */
    public void lbr(ExposureCategory category, ExposureLevel requiredLevel) {
        Expresso.LINEBREAK(category, requiredLevel);
    }

    /**
     * Prints an unrestricted Expresso.error message. Prefixes the format template with 
     * the bound identity tag if present.
     *
     * @param format The format template.
     * @param args   Arguments or optional trailing {@link Throwable}.
     */
    public void err(String format, Object... args) { 
        if (identity != null && !identity.isBlank()) {
            Expresso.error("[" + identity + "] " + format, args);
        } else {
            Expresso.error(format, args);
        }
    }

    /**
     * Lazily evaluates and prints an unrestricted Expresso.error message with the bound identity tag if present.
     *
     * @param message A supplier yielding the Expresso.error string.
     */
    public void err(Supplier<String> message) {
        if (message == null) return;
        if (identity != null && !identity.isBlank()) {
            Expresso.error("[" + identity + "] " + message.get());
        } else {
            Expresso.error(message.get());
        }
    }

    /**
     * Rapid debugging utility that prints a high-visibility execution marker tag, 
     * automatically injecting the bound identity tag if present.
     */
    public void here() { 
        if (identity != null && !identity.isBlank()) {
            Expresso.hereAnnounce(identity);
        } else {
            Expresso.hereAnnounce(); 
        }
    }

    /**
     * Rapid debugging utility that prints a high-visibility execution marker tag 
     * along with a custom label, automatically injecting the bound identity tag if present.
     *
     * @param label Additional context or descriptive text.
     */
    public void here(String label) { 
        if (identity != null && !identity.isBlank()) {
            Expresso.hereAnnounce(identity, label);
        } else {
            Expresso.hereAnnounce("", label);
        }
    }

    /**
     * Rapid debugging utility allowing a single-use custom color and header override.
     * 
     * @param customColor  single-use ANSI color code.
     * @param customHeader single-use marker header string.
     * @param label        Additional context or identifying text.
     */
    public void here(String customColor, String customHeader, String label) { 
        if (identity != null && !identity.isBlank()) {
            Expresso.hereAnnounce(identity, customColor, customHeader, label);
        } else {
            Expresso.hereAnnounce(customColor, customHeader, label);
        }
    }

    /**
     * Retrieves the identity tag bound to this Exposure instance.
     *
     * @return The identity string, or {@code null} if no identity was set.
     */
    public String getIdentity() { return identity; }
}