package karashokleo.spawner_soul;

import karashokleo.spawner_soul.command.SpawnerSoulCommands;
import karashokleo.spawner_soul.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/** Forge registrations and shared bootstrap; NightConfig is supplied by Forge itself. */
@Mod(SpawnerSoul.MOD_ID)
public final class SpawnerSoulForge {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SpawnerSoul.MOD_ID);

    public SpawnerSoulForge() {
        SpawnerSoul.init(FMLPaths.CONFIGDIR.get());
        ITEMS.register("spawner_soul", () -> ModItems.SPAWNER_SOUL);
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.addListener(SpawnerSoulForge::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        SpawnerSoulCommands.register(event.getDispatcher());
    }
}
