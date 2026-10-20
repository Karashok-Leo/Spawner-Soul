package karashokleo.spawner_soul.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;

import java.io.IOException;
import java.io.Reader;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Thin NightConfig TOML/object-mapping wrapper with transactional, read-only reloads.
 *
 * <p>This deliberately does not use FileConfig autosave, correction, or a file
 * watcher. A rejected edit must remain byte-for-byte untouched on disk, and a
 * failed reload must leave the previously published snapshot in use.</p>
 */
public final class SpawnerSoulConfigManager {

    private static final ObjectConverter MAPPER = new ObjectConverter(false, false);

    private volatile SpawnerSoulConfig current = new SpawnerSoulConfig();
    private Path configFile;

    /** Returns one complete snapshot; callers should retain it for an entire operation. */
    public SpawnerSoulConfig current() {
        return current;
    }

    /**
     * Initializes this manager once, creating documented defaults only if the file is absent.
     *
     * <p>On failure, the in-memory defaults remain available and the path remains
     * initialized, so a later {@link #reload()} can load a manually repaired file.
     * The caller should log the error without replacing the existing file.</p>
     */
    public synchronized void initialize(Path file) throws ConfigException {
        if (configFile != null) {
            throw new IllegalStateException("Spawner Soul configuration is already initialized");
        }
        configFile = Objects.requireNonNull(file, "file").toAbsolutePath().normalize();
        try {
            createDefaultsIfMissing();
            current = readCandidate();
        } catch (IOException | RuntimeException exception) {
            throw loadFailure(exception);
        }
    }

    /**
     * Reads, maps and validates a new object before a single snapshot swap.
     * This method never creates or writes any file, including when the file is missing.
     */
    public synchronized void reload() throws ConfigException {
        if (configFile == null) {
            throw new ConfigException("Spawner Soul configuration has not been initialized", null);
        }
        try {
            SpawnerSoulConfig candidate = readCandidate();
            current = candidate;
        } catch (IOException | RuntimeException exception) {
            throw loadFailure(exception);
        }
    }

    private SpawnerSoulConfig readCandidate() throws IOException {
        CommentedConfig parsed;
        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            parsed = new TomlParser().parse(reader);
        }
        validateShape(parsed, defaultDocument());
        SpawnerSoulConfig candidate = MAPPER.toObject(parsed, SpawnerSoulConfig::new);
        candidate.validate();
        return candidate;
    }

    /**
     * Validates keys and TOML types against the mapped defaults, without manually
     * fetching or converting individual settings. Enum values are checked by
     * NightConfig's {@code @SpecEnum} during object mapping.
     */
    private static void validateShape(UnmodifiableConfig parsed, UnmodifiableConfig defaults) {
        for (UnmodifiableConfig.Entry entry : parsed.entrySet()) {
            List<String> path = List.of(entry.getKey());
            if (!defaults.contains(path)) {
                throw new IllegalArgumentException("Unknown configuration key: " + entry.getKey());
            }
            Object expected = defaults.getRaw(path);
            Object actual = entry.getRawValue();
            boolean validType = expected instanceof Enum<?>
                    ? actual instanceof String
                    : actual != null && expected.getClass().isInstance(actual);
            if (!validType) {
                String expectedType = expected instanceof Enum<?> ? "String (enum name)"
                        : expected.getClass().getSimpleName();
                throw new IllegalArgumentException(
                        entry.getKey() + " must be " + expectedType);
            }
        }
    }

    private void createDefaultsIfMissing() throws IOException {
        if (!Files.notExists(configFile)) {
            return;
        }
        // Serialize fully before opening a file. CREATE_NEW prevents overwriting
        // a file created by the user or another process after the existence check.
        String contents = new TomlWriter().writeToString(defaultDocument());
        Path parent = configFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try {
            Files.writeString(configFile, contents, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (FileAlreadyExistsException ignored) {
            // Another writer won the race; read its file without changing it.
        }
    }

    private static CommentedConfig defaultDocument() {
        CommentedConfig document = TomlFormat.newConfig(LinkedHashMap::new);
        MAPPER.toConfig(new SpawnerSoulConfig(), document);
        document.setComment("defaultSpawnCount",
                " Initial spawn count for newly initialized spawners. Default: 20.\n"
                        + " -1 means infinite; otherwise use an integer >= 0.\n"
                        + " Reloading this setting does not change already initialized spawners.");
        document.setComment("exhaustionBehavior",
                " When a finite spawner is exhausted: KEEP_EMPTY (default) or DESTROY.");
        document.setComment("dropsEnabled",
                " Allow soul drops through normal spawner block loot. Default: true.");
        document.setComment("dropOnlyPlayerMining",
                " Require player mining for soul drops. Default: true.\n"
                        + " false also allows other destruction that uses normal block loot.");
        document.setComment("sameEntityBehavior",
                " Applying a soul of the same entity: REPLACE (default) or ADD.");
        document.setComment("allowDifferentEntityReplacement",
                " Allow a soul to replace a different entity type. Default: true.");
        document.setComment("allowInfiniteToFinite",
                " Allow replacing an infinite spawner with a finite spawn count. Default: true.");
        return document;
    }

    private ConfigException loadFailure(Exception cause) {
        String detail = cause.getMessage();
        if (detail == null || detail.isBlank()) {
            detail = cause.getClass().getSimpleName();
        }
        return new ConfigException(
                "Could not load " + configFile + ": " + detail
                        + ". The previous configuration remains active; existing file contents were not changed.",
                cause);
    }

    /** A load failure suitable for startup logging or a reload-command error. */
    public static final class ConfigException extends Exception {
        @Serial
        private static final long serialVersionUID = 1L;

        private ConfigException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
