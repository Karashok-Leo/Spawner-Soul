package karashokleo.spawner_soul;

import karashokleo.spawner_soul.command.SpawnerSoulCommands;
import karashokleo.spawner_soul.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/** Fabric registrations and shared bootstrap. */
public final class SpawnerSoulFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SpawnerSoul.init(FabricLoader.getInstance().getConfigDir());
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(SpawnerSoul.MOD_ID, "spawner_soul"), ModItems.SPAWNER_SOUL);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SpawnerSoulCommands.register(dispatcher));
    }
}
