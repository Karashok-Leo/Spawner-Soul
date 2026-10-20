package karashokleo.spawner_soul.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.conversion.PreserveNotNull;
import com.electronwill.nightconfig.core.conversion.SpecEnum;

/**
 * A complete, read-only configuration snapshot for one game instance.
 *
 * <p>NightConfig maps the private fields of a new instance before validation and
 * publication. Published instances are never mapped again or otherwise mutated.
 * Missing TOML keys retain the defaults below.</p>
 */
@PreserveNotNull
public final class SpawnerSoulConfig {

    private int defaultSpawnCount = 20;
    @SpecEnum(method = EnumGetMethod.NAME)
    private ExhaustionBehavior exhaustionBehavior = ExhaustionBehavior.KEEP_EMPTY;
    private boolean dropsEnabled = true;
    private boolean dropOnlyPlayerMining = true;
    @SpecEnum(method = EnumGetMethod.NAME)
    private SameEntityBehavior sameEntityBehavior = SameEntityBehavior.REPLACE;
    private boolean allowDifferentEntityReplacement = true;
    private boolean allowInfiniteToFinite = true;

    SpawnerSoulConfig() {
    }

    /** Initial remaining spawn count for newly initialized spawners; -1 is infinite. */
    public int defaultSpawnCount() {
        return defaultSpawnCount;
    }

    /** What happens when a finite spawner exhausts its remaining spawn count. */
    public ExhaustionBehavior exhaustionBehavior() {
        return exhaustionBehavior;
    }

    /** Whether spawners may drop a soul through the normal block-loot path. */
    public boolean dropsEnabled() {
        return dropsEnabled;
    }

    /** Whether soul drops require a player mining the spawner. */
    public boolean dropOnlyPlayerMining() {
        return dropOnlyPlayerMining;
    }

    /** Whether applying a soul of the same entity replaces or adds its spawn count. */
    public SameEntityBehavior sameEntityBehavior() {
        return sameEntityBehavior;
    }

    /** Whether a soul may replace a spawner's different entity type. */
    public boolean allowDifferentEntityReplacement() {
        return allowDifferentEntityReplacement;
    }

    /** Whether an infinite spawner may be replaced with a finite spawn count. */
    public boolean allowInfiniteToFinite() {
        return allowInfiniteToFinite;
    }

    /** Validates the whole candidate before it becomes visible to any consumer. */
    void validate() {
        if (defaultSpawnCount < -1) {
            throw new IllegalArgumentException("defaultSpawnCount must be -1 (infinite) or at least 0");
        }
        if (exhaustionBehavior == null) {
            throw new IllegalArgumentException("exhaustionBehavior must be KEEP_EMPTY or DESTROY");
        }
        if (sameEntityBehavior == null) {
            throw new IllegalArgumentException("sameEntityBehavior must be REPLACE or ADD");
        }
    }

    public enum ExhaustionBehavior {
        KEEP_EMPTY,
        DESTROY
    }

    public enum SameEntityBehavior {
        REPLACE,
        ADD
    }
}
