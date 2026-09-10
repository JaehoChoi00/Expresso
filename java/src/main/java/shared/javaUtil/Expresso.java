package shared.javaUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import shared.javaUtil.enums.*;

/**
 * A central utility class for structured application logging.
 * <p>
 * {@code Expresso} provides granular control over logging output through the use of 
 * categories, exposure levels, and event index tracking. It includes built-in support 
 * for customizable tags (timestamp, elapsed time, identity, etc.), ANSI terminal 
 * color formatting, and can route text to external systems via a bridge mechanism.
 */
public final class Expresso {

    private static final java.util.concurrent.atomic.AtomicLong globalEventCounter = new java.util.concurrent.atomic.AtomicLong(0);

    private static volatile long minIndex = 0;
    private static volatile long maxIndex = Long.MAX_VALUE;
    
    private static volatile boolean showIndexTag = true;
    private static volatile boolean showIdentityTag = true;
    private static volatile boolean showCategoryTag = true;
    private static volatile boolean showLevelTag = true;

    private static volatile ExposureTimeSource timeSource = null;
    private static volatile boolean showApplicationTimeTag = false;
    private static volatile boolean showTimestampTag = false;
    private static volatile boolean showElapsedTimeTag = false;

    private static final String DEFAULT_ERROR_PREFIX = "[ERROR]: ";
    private static final String DEFAULT_ERROR_COLOR = VariableConstants.BOLD + VariableConstants.FG_RED;

    private static final String DEFAULT_HERE_HEADER = "--> [EXPRESSO REPORT HERE]";
    private static final String DEFAULT_HERE_COLOR = VariableConstants.BOLD + VariableConstants.FG_MAGENTA;

    // Mutable variables initialized to defaults
    private static volatile String errorPrefix = DEFAULT_ERROR_PREFIX;
    private static volatile String errorColor = DEFAULT_ERROR_COLOR;

    private static volatile String hereHeader = DEFAULT_HERE_HEADER;
    private static volatile String hereColor = DEFAULT_HERE_COLOR;

    private static volatile boolean showDiagnosticTags = false;

    // Volatile reference write enables thread-safe atomic swapping in setCategory/setCategories
    private static volatile Set<String> enabledCategories = ConcurrentHashMap.newKeySet(); 
    private static final Set<ExposureLevel> enabledLevels = Collections.synchronizedSet(EnumSet.of(ExposureLevel.LEVEL1)); 

    // The bridge hook to route text to outside systems like UI or Files
    private static volatile Consumer<String> externalBridge = null;

    private static final long RUNTIME_START = System.nanoTime();
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    // Direct alias mappings from the absolute source of truth
    public static final String RESET = VariableConstants.RESET;
    public static final String BOLD = VariableConstants.BOLD;
    public static final String DIM = VariableConstants.DIM;
    public static final String UNDERLINE = VariableConstants.UNDERLINE;
    public static final int LINEFEED = VariableConstants.LINEFEED;

    public static final String FG_BLACK = VariableConstants.FG_BLACK;
    public static final String FG_RED = VariableConstants.FG_RED;
    public static final String FG_GREEN = VariableConstants.FG_GREEN;
    public static final String FG_YELLOW = VariableConstants.FG_YELLOW;
    public static final String FG_BLUE = VariableConstants.FG_BLUE;
    public static final String FG_MAGENTA = VariableConstants.FG_MAGENTA;
    public static final String FG_CYAN = VariableConstants.FG_CYAN;
    public static final String FG_WHITE = VariableConstants.FG_WHITE;

    public static final String BG_BLACK = VariableConstants.BG_BLACK;
    public static final String BG_RED = VariableConstants.BG_RED;
    public static final String BG_GREEN = VariableConstants.BG_GREEN;
    public static final String BG_YELLOW = VariableConstants.BG_YELLOW;
    public static final String BG_BLUE = VariableConstants.BG_BLUE;
    public static final String BG_MAGENTA = VariableConstants.BG_MAGENTA;
    public static final String BG_CYAN = VariableConstants.BG_CYAN;
    public static final String BG_WHITE = VariableConstants.BG_WHITE; 

    /**
     * Resolves a dynamic foreground ANSI color string based on an ID.
     * 
     * @param id The color identifier.
     * @return The corresponding ANSI foreground color string.
     */
    public static String FG_COLOR(int id) { return VariableConstants.FG_COLOR(id); }
    
    /**
     * Resolves a dynamic background ANSI color string based on an ID.
     * 
     * @param id The color identifier.
     * @return The corresponding ANSI background color string.
     */
    public static String BG_COLOR(int id) { return VariableConstants.BG_COLOR(id); }

    /** Prevent instantiation of utility class. */
    private Expresso() {}

    /**
     * Creates and returns a new {@link Exposure} builder instance.
     * 
     * @return A new Exposure instance.
     */
    public static Exposure exposure() { return new Exposure(); }

    /**
     * Creates and returns a new {@link Exposure} builder instance with a specific identity.
     * 
     * @param identity The identity tag to attach to this exposure context.
     * @return A new Exposure instance.
     */
    public static Exposure exposure(String identity) { return new Exposure(identity); }

    /**
     * Toggles the display of the global event index tag in the log output.
     * 
     * @param enable Default is set to {@code true}.
     */
    public static void enableIndexTag(boolean enable) { showIndexTag = enable; }
    
    /**
     * Toggles the display of the identity tag in the log output.
     * 
     * @param enable Default is set to {@code true}.
     */
    public static void enableIdentityTag(boolean enable) { showIdentityTag = enable; }

    /**
     * Toggles the display of the category tag in the log output.
     * 
     * @param enable Default is set to {@code true}.
     */
    public static void enableCategoryTag(boolean enable) { showCategoryTag = enable; }
    
    /**
     * Toggles the display of the exposure level tag in the log output.
     * 
     * @param enable Default is set to {@code true}.
     */
    public static void enableLevelTag(boolean enable) { showLevelTag = enable; }

    /**
     * Toggles the display of the absolute timestamp tag in the log output.
     * 
     * @param enable {@code true} to show timestamps.
     */
    public static void enableTimestamp(boolean enable) { showTimestampTag = enable; }

    /**
     * Toggles the display of the elapsed application time tag in the log output.
     * 
     * @param enable {@code true} to show time elapsed since initialization.
     */
    public static void enableElapsedTime(boolean enable) { showElapsedTimeTag = enable; }

    /**
     * Sets an active window of event indices that are permitted to be logged.
     * Logs falling outside this index range will be suppressed.
     * 
     * @param start The inclusive starting index.
     * @param end   The inclusive ending index.
     */
    public static void setIndexRange(long start, long end) {
        minIndex = Math.max(0, start);
        maxIndex = Math.max(start, end);
    }

    /**
     * Sets an active window of event indices from 0 up to a specified end index.
     * 
     * @param end The inclusive ending index.
     */
    public static void setIndexRange(long end) {
        minIndex = 0;
        maxIndex = end;
    }

    /**
     * Restricts logging output to a single specific event index.
     * 
     * @param targetIndex The exact global event index to allow.
     */
    public static void setSingleIndex(long targetIndex) { setIndexRange(targetIndex, targetIndex); }

    /**
     * Customizes the visual style and header prefix for error messages.
     * 
     * @param color  The ANSI color string (e.g., Expresso.FG_RED, Expresso.BOLD). Set null to keep current.
     * @param prefix The prefix label (e.g., "[CRITICAL]: "). Set null to keep current.
     */
    public static void setErrorStyle(String color, String prefix) {
        if (color != null) errorColor = color;
        if (prefix != null) errorPrefix = prefix;
    }

    /**
     * Customizes the visual style and header string for execution markers (hereAnnounce).
     * 
     * @param color  The ANSI color string. Set null to keep current.
     * @param header The header text (e.g., "==> [TRACE]"). Set null to keep current.
     */
    public static void setHereStyle(String color, String header) {
        if (color != null) hereColor = color;
        if (header != null) hereHeader = header;
    }
    
    /** Resets error styling back to default values. */
    public static void resetErrorStyle() {
        errorColor = DEFAULT_ERROR_COLOR;
        errorPrefix = DEFAULT_ERROR_PREFIX;
    }

    /** Resets execution marker styling back to default values. */
    public static void resetHereStyle() {
        hereColor = DEFAULT_HERE_COLOR;
        hereHeader = DEFAULT_HERE_HEADER;
    }

    /**
     * Toggles whether diagnostic messages (error and hereAnnounce) should include 
     * active Expresso metadata tags (elapsed time, timestamp, index, identity).
     * 
     * @param enable Default is {@code false}.
     */
    public static void enableDiagnosticTags(boolean enable) {
        showDiagnosticTags = enable;
    }

    /**
     * Clears any active index filters, allowing all events to be logged regardless of their index.
     */
    public static void clearIndexFilter() {
        minIndex = 0;
        maxIndex = Long.MAX_VALUE;
    }

    /**
     * Resets the global atomic event counter back to zero.
     */
    public static void resetEventCounter() { globalEventCounter.set(0); }

    /**
     * Clears all currently enabled categories. If no categories are enabled, 
     * logs requiring a specific category will fail to output.
     */
    public static void clearAllCategories() { enabledCategories = ConcurrentHashMap.newKeySet(); }

    /**
     * Adds a specific category to the list of currently enabled categories.
     * 
     * @param category The category to enable.
     */
    public static void enableCategory(ExposureCategory category) { 
        if (category != null) enabledCategories.add(category.name()); 
    }
    
    /**
     * Removes a specific category from the list of currently enabled categories.
     * 
     * @param category The category to disable.
     */
    public static void disableCategory(ExposureCategory category) { 
        if (category != null) enabledCategories.remove(category.name()); 
    }

    /**
     * Emits a double newline to the output if the specified category and level criteria are met.
     * 
     * @param category      The category the newline belongs to.
     * @param requiredLevel The severity level required.
     */
    public static void NEWLINE(ExposureCategory category, ExposureLevel requiredLevel) { 
        if (isExposed(category, requiredLevel)) { emit("\n\n"); }
    }

    /**
     * Emits a formatted visual line break separator if the specified category and level criteria are met.
     * 
     * @param category      The category the linebreak belongs to.
     * @param requiredLevel The severity level required.
     */
    public static void LINEBREAK(ExposureCategory category, ExposureLevel requiredLevel) { 
        if (isExposed(category, requiredLevel)) { emit("\n--------------------------------\n"); }
    }

    // Exposure configuration methods

    /**
     * Sets a single enabled exposure level.
     * Clears any previously set levels.
     * 
     * @param newLevel The single level to set as enabled.
     */
    public static void setLevel(ExposureLevel newLevel) { 
        synchronized (enabledLevels) {
            enabledLevels.clear();
            if (newLevel != null) enabledLevels.add(newLevel);
        }
    }
    
    /**
     * Specifically enables an arbitrary collection of hand-picked exposure levels.
     * Clears any previously set levels.
     * 
     * @param handPickedLevels A variable-length list of levels to enable.
     */
    public static void setLevels(ExposureLevel... handPickedLevels) {
        synchronized (enabledLevels) {
            enabledLevels.clear();
            if (handPickedLevels != null) {
                for (ExposureLevel level : handPickedLevels) {
                    if (level != null) enabledLevels.add(level);
                }
            }
        }
    }

    /**
     * Atomically clears all currently enabled categories and enables only the specified category.
     * 
     * @param category The sole category to enable.
     */
    public static void setCategory(ExposureCategory category) { 
        Set<String> nextSet = ConcurrentHashMap.newKeySet();
        if (category != null) nextSet.add(category.name());
        enabledCategories = nextSet;
    }

    /**
     * Atomically clears all currently enabled categories and enables a hand-picked list of categories.
     * 
     * @param handPickedCategories A variable-length list of categories to enable.
     */
    public static void setCategories(ExposureCategory... handPickedCategories) {
        Set<String> nextSet = ConcurrentHashMap.newKeySet();
        if (handPickedCategories != null) {
            Arrays.stream(handPickedCategories).filter(c -> c != null).forEach(c -> nextSet.add(c.name()));
        }
        enabledCategories = nextSet;
    }

    /**
     * Retrieves the total number of events that have been logged (or attempted) 
     * since application start or the last reset.
     * 
     * @return The current global event count.
     */
    public static long getCurrentEventCount() { return globalEventCounter.get(); }

    /**
     * Retrieves an unmodifiable view of the currently enabled categories.
     * 
     * @return A read-only Set of enabled category string names.
     */
    public static Set<String> getCategories() { return Collections.unmodifiableSet(new HashSet<>(enabledCategories)); }
    
    /**
     * Retrieves an unmodifiable view of the currently enabled exposure levels.
     * 
     * @return A read-only Set of enabled ExposureLevels.
     */
    public static Set<ExposureLevel> getLevels() { 
        synchronized (enabledLevels) {
            return Collections.unmodifiableSet(new HashSet<>(enabledLevels)); 
        }
    }

    /**
     * Sets an external source for retrieving custom application time metrics.
     * 
     * @param source The time source implementation to use.
     */
    public static void setTimeSource(ExposureTimeSource source) { timeSource = source; }

    /**
     * Toggles the display of the custom application time tag (provided by the {@link ExposureTimeSource}).
     * 
     * @param enable {@code true} to show application time tags.
     */
    public static void enableApplicationTime(boolean enable) { showApplicationTimeTag = enable; }

    /**
     * Sets a custom external bridge (consumer) to capture all logged text, 
     * redirecting it from the default standard output.
     * 
     * @param outsideSystem A consumer (e.g. file writer, GUI panel) that will receive output strings.
     */
    public static void setBridge(Consumer<String> outsideSystem) { externalBridge = outsideSystem; }

    /**
     * Resets Expresso into a fresh state.
     * 
     * Clears category and index filters, restores LEVEL1, resets the event counter, disables optional time tags,
     * and removes the external bridge.
     */
    public static void reset() {
        clearAllCategories();
        setLevel(ExposureLevel.LEVEL1);
        clearIndexFilter();
        resetEventCounter();
        enableTimestamp(false);
        enableElapsedTime(false);
        enableApplicationTime(false);
        setBridge(null);
    }

    /**
     * Evaluates whether a log event with the specified category and level 
     * is permitted to be emitted based on current system filters.
     * 
     * @param category      The category of the event.
     * @param requiredLevel The level of the event.
     * @return {@code true} if the event passes all active filters, {@code false} otherwise.
     */
    public static boolean isExposed(ExposureCategory category, ExposureLevel requiredLevel) {
        if (category == null || requiredLevel == null) return false;
        
        Set<String> activeCategories = enabledCategories;
        if (!activeCategories.isEmpty() && !activeCategories.contains(category.name())) {
            return false;
        }
        
        synchronized (enabledLevels) {
            int levelsCount = enabledLevels.size();
            if (levelsCount == 0) return false;

            if (levelsCount == 1) {
                ExposureLevel activeLevel = enabledLevels.iterator().next();
                if (activeLevel.getLevel() < requiredLevel.getLevel()) return false;
            } else {
                if (!enabledLevels.contains(requiredLevel)) return false;
            }
        }

        return true;
    }

    /**
     * Conditionally formats and emits a log message if it passes the active exposure filters.
     * 
     * @param category      The category this message belongs to.
     * @param requiredLevel The severity level of this message.
     * @param format        The string format template.
     * @param args          The arguments applied to the format template.
     */
    public static void printf(ExposureCategory category, ExposureLevel requiredLevel, String format, Object... args) {
        printf("", category, requiredLevel, format, args);
    }

    /**
     * Conditionally formats and emits a log message with a specific identity context 
     * if it passes the active exposure filters.
     * 
     * @param identity      An identifier representing the source of the message.
     * @param category      The category this message belongs to.
     * @param requiredLevel The severity level of this message.
     * @param format        The string format template.
     * @param args          The arguments applied to the format template.
     */
    public static void printf(String identity, ExposureCategory category, ExposureLevel requiredLevel, String format, Object... args) {
        if (!isExposed(category, requiredLevel)) return;

        long currentIndex = globalEventCounter.incrementAndGet();
        if (currentIndex < minIndex || currentIndex > maxIndex) return;

        StringBuilder stringBuilder = new StringBuilder(128);

        if (showApplicationTimeTag) {
            ExposureTimeSource source = timeSource;
            if (source != null) { 
                String applicationTime = source.getTime();
                if (applicationTime != null && !applicationTime.isBlank()) {
                    stringBuilder.append(DIM).append("[").append(applicationTime).append("] ").append(RESET);
                }
            }
        }

        if (showTimestampTag) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            stringBuilder.append(DIM).append("[").append(timestamp).append("] ").append(RESET);
        }
        if (showElapsedTimeTag) {
            double elapsedMillis = (System.nanoTime() - RUNTIME_START) / 1000000.0; 
            stringBuilder.append(String.format(DIM + "[+%.3fms] " + RESET, elapsedMillis));
        }

        if (showIndexTag) { stringBuilder.append(DIM).append("[#").append(currentIndex).append("] ").append(RESET); }
        if (showIdentityTag && identity != null && !identity.isBlank()) { stringBuilder.append(DIM).append("[").append(identity).append("] ").append(RESET); }
        if (showCategoryTag) { stringBuilder.append(DIM).append("[").append(category.name()).append("] ").append(RESET); }
        if (showLevelTag) { stringBuilder.append(DIM).append("[").append(requiredLevel).append("] ").append(RESET); }
                
        stringBuilder.append(String.format(format, args));
        emit(stringBuilder.toString());
    }

    /**
     * Core emission pipeline. Pushes the constructed text string either to 
     * the external bridge (if configured) or defaults to {@code System.out}.
     * 
     * @param text The exact text block to emit.
     */
    private static void emit(String text) {
        Consumer<String> bridge = externalBridge;
        if (bridge != null) {
            bridge.accept(text);
        } else {
            System.out.print(text);
        }
    }

    /**
     * Appends active Expresso metadata tags (elapsed time, timestamp, application time)
     * directly into the provided buffer if diagnostic tag tracking is explicitly enabled.
     * 
     * @param stringBuilder The buffer to populate.
     */
    private static void appendDiagnosticTags(StringBuilder stringBuilder) {
        if (!showDiagnosticTags) return;

        if (showApplicationTimeTag && timeSource != null) {
            String appTime = timeSource.getTime();
            if (appTime != null && !appTime.isBlank()) {
                stringBuilder.append(DIM).append("[").append(appTime).append("] ").append(RESET);
            }
        }
        if (showTimestampTag) {
            stringBuilder.append(DIM).append("[").append(LocalDateTime.now().format(TIMESTAMP_FORMAT)).append("] ").append(RESET);
        }
        if (showElapsedTimeTag) {
            double elapsedMillis = (System.nanoTime() - RUNTIME_START) / 1000000.0;
            stringBuilder.append(String.format(DIM + "[+%.3fms] " + RESET, elapsedMillis));
        }
    }

    /**
     * Prints an unrestricted error message to the output stream. 
     * If the final argument provided is an instance of {@link Throwable}, it will be 
     * extracted and printed with its full stack trace.
     * 
     * @param format The string format template.
     * @param args   The arguments to format.
     */
    public static void error(String format, Object... args) {
        Throwable exception = null;
        Object[] formattingArgs = args;

        if (args != null && args.length > 0 && args[args.length - 1] instanceof Throwable) {
            exception = (Throwable) args[args.length - 1];
            formattingArgs = new Object[args.length - 1];
            System.arraycopy(args, 0, formattingArgs, 0, args.length - 1);
        }

        StringBuilder stringBuilder = new StringBuilder(128);
        appendDiagnosticTags(stringBuilder);
        stringBuilder.append(errorColor).append(errorPrefix).append(RESET).append(String.format(format, formattingArgs));

        if (exception != null) {
            stringBuilder.append("\n");
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            stringBuilder.append(sw.toString());
        }

        emit(stringBuilder.toString());
    }

    /**
     * Debugging utility that forcibly prints a highly visible marker 
     * to trace code execution paths (e.g. "--> [EXPRESSO REPORT HERE]").
     */
    public static void hereAnnounce() {
        StringBuilder stringBuilder = new StringBuilder(64);
        appendDiagnosticTags(stringBuilder);
        stringBuilder.append(hereColor).append(hereHeader).append(RESET).append("\n");
        emit(stringBuilder.toString());
    }

    /**
     * Debugging utility that forcibly prints a highly visible marker
     * with an optional identity tag to trace code execution paths.
     *
     * @param identity Identity associated with the execution marker.
     */
    public static void hereAnnounce(String identity) {
        StringBuilder stringBuilder = new StringBuilder(64);
        appendDiagnosticTags(stringBuilder);

        if (identity != null && !identity.isBlank()) {
            stringBuilder.append("[").append(identity).append("] ");
        }

        stringBuilder.append(hereColor).append(hereHeader).append(RESET).append("\n");
        emit(stringBuilder.toString());
    }

    /**
     * Debugging utility that forcibly prints a highly visible marker
     * alongside an optional identity and specific label to trace code execution paths.
     *
     * @param identity Identity associated with the exposure.
     * @param label    Additional context or identifying text.
     */
    public static void hereAnnounce(String identity, String label) {
        StringBuilder stringBuilder = new StringBuilder(64);
        appendDiagnosticTags(stringBuilder);

        if (identity != null && !identity.isBlank()) {
            stringBuilder.append("[").append(identity).append("] ");
        }

        stringBuilder.append(hereColor).append(hereHeader).append(RESET);

        if (label != null && !label.isBlank()) { stringBuilder.append(": ").append(label); }
        
        stringBuilder.append("\n");
        emit(stringBuilder.toString());
        
    }

    /**
     * Rapid debugging utility allowing a single-use custom color and header override.
     * 
     * @param customColor  single-use ANSI color code.
     * @param customHeader single-use marker header string.
     * @param label        Additional context or identifying text.
     */
    public static void hereAnnounce(String customColor, String customHeader, String label) {
        hereAnnounce("", customColor, customHeader, label);
    }

    /**
     * Rapid debugging utility allowing a single-use custom color and header override.
     * 
     * @param identity     the identity tag
     * @param customColor  single-use ANSI color code.
     * @param customHeader single-use marker header string.
     * @param label        Additional context or identifying text.
     */
    public static void hereAnnounce(String identity, String customColor, String customHeader, String label) {
        StringBuilder stringBuilder = new StringBuilder(64);
        appendDiagnosticTags(stringBuilder);
        String color = (customColor != null) ? customColor : hereColor;
        String header = (customHeader != null) ? customHeader : hereHeader;
        
        if (identity != null && !identity.isBlank()) {
            stringBuilder.append("[").append(identity).append("] ");
        }

        if (label != null && !label.isBlank()) {
            stringBuilder.append(color).append(header).append(": ").append(RESET).append(label).append("\n");
        } else {
            stringBuilder.append(color).append(header).append(RESET).append("\n");
        }
        emit(stringBuilder.toString());
    }

}