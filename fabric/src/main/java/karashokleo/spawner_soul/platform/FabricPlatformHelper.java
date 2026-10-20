package karashokleo.spawner_soul.platform;

import karashokleo.spawner_soul.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

/** Fabric implementation of the shared platform operations. */
public final class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
