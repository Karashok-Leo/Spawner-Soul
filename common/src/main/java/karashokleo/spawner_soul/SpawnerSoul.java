package karashokleo.spawner_soul;

import karashokleo.spawner_soul.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Shared bootstrap for both loaders. Gameplay is added in later stages. */
public final class SpawnerSoul {

    /** Stable registry and resource namespace. */
    public static final String MOD_ID = "spawner_soul";
    /** Human-readable mod name. */
    public static final String MOD_NAME = "Spawner Soul";
    /** Logger shared by both loader entry points. */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private SpawnerSoul() {
    }

    /** Initializes the shared portion of the mod. */
    public static void init() {
        LOGGER.info("Initializing {} on {}", MOD_NAME, Services.PLATFORM.getPlatformName());
    }
}
