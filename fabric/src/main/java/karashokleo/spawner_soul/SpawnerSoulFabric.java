package karashokleo.spawner_soul;

import net.fabricmc.api.ModInitializer;

/** Fabric entry point for the shared mod bootstrap. */
public final class SpawnerSoulFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SpawnerSoul.init();
    }
}
