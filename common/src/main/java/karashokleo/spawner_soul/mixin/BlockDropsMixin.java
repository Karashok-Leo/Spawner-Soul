package karashokleo.spawner_soul.mixin;

import java.util.List;
import karashokleo.spawner_soul.spawner.SpawnerDrops;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Intercepts loot emission, not generic loot-table queries. */
@Mixin(Block.class)
public abstract class BlockDropsMixin {
    @Redirect(
            method = {
                "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
                "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;)Ljava/util/List;"),
            require = 2)
    private static List<ItemStack> spawnerSoul$dropWithoutActor(
            BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity) {
        return SpawnerDrops.appendSoul(
                Block.getDrops(state, level, pos, blockEntity),
                level, pos, blockEntity, null);
    }

    // Forge moves this call into its own seven-argument overload below.
    @Group(name = "spawnerSoul$actorDrops", min = 1, max = 1)
    @Redirect(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"),
            require = 0)
    private static List<ItemStack> spawnerSoul$dropWithActor(
            BlockState state, ServerLevel level, BlockPos pos,
            BlockEntity blockEntity, Entity actor, ItemStack tool) {
        return SpawnerDrops.appendSoul(
                Block.getDrops(state, level, pos, blockEntity, actor, tool),
                level, pos, blockEntity, actor);
    }

    /**
     * Forge's added method retains its source name at runtime. Only the selector
     * is unmapped; its invocation of vanilla Block.getDrops must still be mapped.
     */
    @Dynamic("Forge adds the dropXp overload of Block.dropResources.")
    @Group(name = "spawnerSoul$actorDrops", min = 1, max = 1)
    @Redirect(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Z)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
                    remap = true),
            require = 0)
    private static List<ItemStack> spawnerSoul$dropWithForgeActor(
            BlockState state, ServerLevel level, BlockPos pos,
            BlockEntity blockEntity, Entity actor, ItemStack tool) {
        return SpawnerDrops.appendSoul(
                Block.getDrops(state, level, pos, blockEntity, actor, tool),
                level, pos, blockEntity, actor);
    }
}
