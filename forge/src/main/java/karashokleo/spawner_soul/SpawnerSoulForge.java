package karashokleo.spawner_soul;

import net.minecraftforge.fml.common.Mod;

/** Forge entry point for the shared mod bootstrap. */
@Mod(SpawnerSoul.MOD_ID)
public final class SpawnerSoulForge {

    /** Called by Forge during mod construction. */
    public SpawnerSoulForge() {
        SpawnerSoul.init();
    }
}
