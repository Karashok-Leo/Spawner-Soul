package karashokleo.spawner_soul.mixin;

import java.util.List;
import karashokleo.spawner_soul.spawner.SpawnerDrops;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Keeps explosion eligibility and item emission in vanilla/Forge's own path. */
@Mixin(Explosion.class)
public abstract class ExplosionDropsMixin {
    @Redirect(
            method = "finalizeExplosion(Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getDrops(Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"))
    private List<ItemStack> spawnerSoul$explosionDrops(BlockState state, LootParams.Builder params) {
        return SpawnerDrops.appendSoul(
                state.getDrops(params),
                params.getLevel(),
                BlockPos.containing(params.getParameter(LootContextParams.ORIGIN)),
                params.getOptionalParameter(LootContextParams.BLOCK_ENTITY),
                params.getOptionalParameter(LootContextParams.THIS_ENTITY));
    }
}
