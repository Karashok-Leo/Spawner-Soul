package karashokleo.spawner_soul.command;

import com.mojang.brigadier.CommandDispatcher;
import karashokleo.spawner_soul.SpawnerSoul;
import karashokleo.spawner_soul.config.SpawnerSoulConfigManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/** Reloads on the server thread, with the same permission level as other game-master commands. */
public final class SpawnerSoulCommands {

    private SpawnerSoulCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(SpawnerSoul.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("reload").executes(context -> reload(context.getSource()))));
    }

    private static int reload(CommandSourceStack source) {
        try {
            SpawnerSoul.CONFIG.reload();
            source.sendSuccess(() -> Component.translatable("command.spawner_soul.reload.success"), true);
            return 1;
        } catch (SpawnerSoulConfigManager.ConfigException exception) {
            source.sendFailure(Component.translatable("command.spawner_soul.reload.failure", exception.getMessage()));
            SpawnerSoul.LOGGER.warn("Spawner Soul reload rejected; keeping previous configuration and original file", exception);
            return 0;
        }
    }
}
