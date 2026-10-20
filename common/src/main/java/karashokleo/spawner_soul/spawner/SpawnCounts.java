package karashokleo.spawner_soul.spawner;

import karashokleo.spawner_soul.config.SpawnerSoulConfig;

import java.util.OptionalInt;

/** Pure count rules; an empty result means rejection or no actual change. */
public final class SpawnCounts {

    public static final int INFINITE = -1;

    private SpawnCounts() {
    }

    public static OptionalInt apply(int current, int incoming, boolean sameEntity, SpawnerSoulConfig config) {
        if (current < INFINITE || incoming < INFINITE || incoming == 0) {
            return OptionalInt.empty();
        }
        // An empty cage has no effective entity type and can always be filled.
        if (current == 0) {
            return OptionalInt.of(incoming);
        }
        if (!sameEntity && !config.allowDifferentEntityReplacement()) {
            return OptionalInt.empty();
        }
        int result = incoming;
        if (sameEntity && config.sameEntityBehavior() == SpawnerSoulConfig.SameEntityBehavior.ADD) {
            if (current == INFINITE || incoming == INFINITE) {
                result = INFINITE;
            } else {
                try {
                    result = Math.addExact(current, incoming);
                } catch (ArithmeticException exception) {
                    // Reject overflow instead of silently losing or manufacturing spawns.
                    return OptionalInt.empty();
                }
            }
        }
        if (current == INFINITE && result != INFINITE && !config.allowInfiniteToFinite()) {
            return OptionalInt.empty();
        }
        return sameEntity && current == result ? OptionalInt.empty() : OptionalInt.of(result);
    }
}
