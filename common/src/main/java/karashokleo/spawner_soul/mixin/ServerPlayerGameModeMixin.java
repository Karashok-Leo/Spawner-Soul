package karashokleo.spawner_soul.mixin;

import karashokleo.spawner_soul.spawner.SpawnerDrops;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow
    public abstract GameType getGameModeForPlayer();

    /**
     * Both loaders call this only after successful removal and harvest checks.
     * Forge's cancellation, permissions and onBlockStartBreak checks run earlier.
     */
    @Redirect(
            method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V"))
    private void spawnerSoul$successfulPlayerMining(
            Block block, Level level, Player player, BlockPos pos,
            BlockState state, BlockEntity blockEntity, ItemStack tool) {
        if (getGameModeForPlayer() == GameType.SURVIVAL
                && blockEntity instanceof SpawnerBlockEntity) {
            SpawnerDrops.duringPlayerMining(
                    level, player, pos, blockEntity,
                    () -> block.playerDestroy(level, player, pos, state, blockEntity, tool));
        } else {
            block.playerDestroy(level, player, pos, state, blockEntity, tool);
        }
    }
}
