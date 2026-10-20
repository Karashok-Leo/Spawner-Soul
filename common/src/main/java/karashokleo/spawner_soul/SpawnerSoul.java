package karashokleo.spawner_soul;

import karashokleo.spawner_soul.config.SpawnerSoulConfigManager;
import karashokleo.spawner_soul.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/** Shared bootstrap and one instance-wide configuration manager for both loaders. */
public final class SpawnerSoul {

    public static final String MOD_ID = "spawner_soul";
    public static final String MOD_NAME = "Spawner Soul";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final SpawnerSoulConfigManager CONFIG = new SpawnerSoulConfigManager();

    private SpawnerSoul() {
    }

    public static void init(Path configDirectory) {
        try {
            CONFIG.initialize(configDirectory.resolve(MOD_ID + ".toml"));
        } catch (SpawnerSoulConfigManager.ConfigException exception) {
            LOGGER.error("Unable to load Spawner Soul configuration; using in-memory defaults. "
                    + "The existing file has not been changed. Fix it and run /spawner_soul reload.", exception);
        }
        LOGGER.info("Initializing {} on {}", MOD_NAME, Services.PLATFORM.getPlatformName());
    }
}
