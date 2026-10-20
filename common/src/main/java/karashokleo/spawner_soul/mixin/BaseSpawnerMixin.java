package karashokleo.spawner_soul.mixin;

import karashokleo.spawner_soul.SpawnerSoul;
import karashokleo.spawner_soul.config.SpawnerSoulConfig;
import karashokleo.spawner_soul.spawner.SpawnerOperations;
import karashokleo.spawner_soul.spawner.SpawnerState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/** Counts committed spawns, not attempts; minecart and unrelated custom spawners are untouched. */
@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

    @Unique
    @Nullable
    private SpawnerBlockEntity spawnerSoul$tickingBlock;

    @Unique
    @Nullable
    private SpawnerBlockEntity spawnerSoul$block(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof SpawnerBlockEntity block && block.getSpawner() == (Object) this
                ? block : null;
    }

    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void spawnerSoul$beforeTick(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        spawnerSoul$tickingBlock = spawnerSoul$block(level, pos);
        if (spawnerSoul$tickingBlock != null
                && ((SpawnerState) spawnerSoul$tickingBlock).spawnerSoul$getRemainingSpawns() == 0) {
            ci.cancel();
        }
    }

    @Redirect(method = "serverTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean spawnerSoul$commitSpawn(ServerLevel level, Entity entity) {
        SpawnerBlockEntity block = spawnerSoul$tickingBlock;
        if (block == null) {
            return level.tryAddFreshEntityWithPassengers(entity);
        }
        SpawnerState state = (SpawnerState) block;
        int remaining = state.spawnerSoul$getRemainingSpawns();
        // A vanilla batch can contain several attempts. Stop it after the last paid spawn.
        if (remaining == 0 || block.isRemoved()) {
            return false;
        }
        boolean accepted = level.tryAddFreshEntityWithPassengers(entity);
        // Forge can cancel an entity join even when the outer vanilla helper returns true.
        boolean spawned = accepted && level.getEntity(entity.getUUID()) == entity;
        if (spawned && remaining > 0) {
            state.spawnerSoul$setRemainingSpawns(remaining - 1);
            if (remaining == 1 && SpawnerSoul.CONFIG.current().exhaustionBehavior()
                    == SpawnerSoulConfig.ExhaustionBehavior.DESTROY) {
                level.destroyBlock(block.getBlockPos(), false);
            } else {
                SpawnerOperations.sync(block);
            }
        }
        return spawned;
    }

    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private void spawnerSoul$hideEmptyParticles(Level level, BlockPos pos, CallbackInfo ci) {
        SpawnerBlockEntity block = spawnerSoul$block(level, pos);
        if (block != null && ((SpawnerState) block).spawnerSoul$getRemainingSpawns() == 0) {
            ci.cancel();
        }
    }

    @Inject(method = "getOrCreateDisplayEntity", at = @At("HEAD"), cancellable = true)
    private void spawnerSoul$hideEmptyEntity(Level level, RandomSource random, BlockPos pos,
                                            CallbackInfoReturnable<Entity> cir) {
        SpawnerBlockEntity block = spawnerSoul$block(level, pos);
        if (block != null && ((SpawnerState) block).spawnerSoul$getRemainingSpawns() == 0) {
            cir.setReturnValue(null);
        }
    }
}
