package karashokleo.spawner_soul.spawner;

import java.util.ArrayList;
import java.util.List;
import karashokleo.spawner_soul.SpawnerSoul;
import karashokleo.spawner_soul.config.SpawnerSoulConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

/**
 * Adds a soul only to loot being emitted by a normal block-drop operation.
 *
 * <p>Do not call this from {@code BlockBehaviour.getDrops}: querying a loot table,
 * including {@code /loot ... mine}, must not extract a soul from a live spawner.</p>
 */
public final class SpawnerDrops {
    private static final ThreadLocal<MiningContext> PLAYER_MINING = new ThreadLocal<>();

    private SpawnerDrops() {
    }

    /**
     * Runs only the already-approved, successful survival harvesting operation.
     * Restoring the previous context also handles nested harvesting and exceptions.
     */
    public static void duringPlayerMining(
            Level level, Player player, BlockPos pos, BlockEntity blockEntity, Runnable operation) {
        MiningContext previous = PLAYER_MINING.get();
        PLAYER_MINING.set(new MiningContext(level, player, pos.immutable(), blockEntity));
        try {
            operation.run();
        } finally {
            if (previous == null) {
                PLAYER_MINING.remove();
            } else {
                PLAYER_MINING.set(previous);
            }
        }
    }

    /**
     * Preserves all ordinary loot and appends one soul carrying the entire balance.
     * The caller must pass loot from an actual dropResources/explosion drop call.
     */
    public static List<ItemStack> appendSoul(
            List<ItemStack> drops,
            ServerLevel level,
            BlockPos pos,
            BlockEntity blockEntity,
            Entity actor) {
        SpawnerSoulConfig config = SpawnerSoul.CONFIG.current();
        if (!config.dropsEnabled()
                || !(blockEntity instanceof SpawnerBlockEntity spawner)
                || (actor instanceof Player player && (player.isCreative() || player.isSpectator()))
                || !level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            return drops;
        }

        MiningContext mining = PLAYER_MINING.get();
        boolean sameMining = mining != null && mining.matches(level, actor, pos, spawner);
        if (config.dropOnlyPlayerMining() && !sameMining) {
            return drops;
        }
        if (sameMining && mining.soulAdded) {
            return drops;
        }

        ItemStack soul = SpawnerOperations.createSoul(spawner);
        if (soul.isEmpty()) {
            return drops;
        }

        // Empty vanilla loot lists can be immutable. Never alter the original list.
        List<ItemStack> result = new ArrayList<>(drops.size() + 1);
        result.addAll(drops);
        result.add(soul);
        if (sameMining) {
            mining.soulAdded = true;
        }
        return result;
    }

    private static final class MiningContext {
        private final Level level;
        private final Player player;
        private final BlockPos pos;
        private final BlockEntity blockEntity;
        private boolean soulAdded;

        private MiningContext(Level level, Player player, BlockPos pos, BlockEntity blockEntity) {
            this.level = level;
            this.player = player;
            this.pos = pos;
            this.blockEntity = blockEntity;
        }

        private boolean matches(Level level, Entity actor, BlockPos pos, BlockEntity blockEntity) {
            return this.level == level
                    // The four-argument dropResources overload has no actor.
                    // Position and exact BE identity still bind it to this harvest.
                    && (actor == null || this.player == actor)
                    && this.pos.equals(pos)
                    && this.blockEntity == blockEntity;
        }
    }
}
