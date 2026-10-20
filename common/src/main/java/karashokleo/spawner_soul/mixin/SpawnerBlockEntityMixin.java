package karashokleo.spawner_soul.mixin;

import karashokleo.spawner_soul.SpawnerSoul;
import karashokleo.spawner_soul.spawner.SpawnerState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stores the count on the block entity, including vanilla update tags and chunk saves. */
@Mixin(SpawnerBlockEntity.class)
public abstract class SpawnerBlockEntityMixin implements SpawnerState {

    @Unique
    private static final String SPAWNER_SOUL_REMAINING = "spawner_soul:remaining_spawns";
    @Unique
    private int spawnerSoul$remaining = -2;
    @Unique
    private boolean spawnerSoul$needsSave = true;

    @Override
    public int spawnerSoul$getRemainingSpawns() {
        if (spawnerSoul$remaining == -2) {
            spawnerSoul$remaining = SpawnerSoul.CONFIG.current().defaultSpawnCount();
            spawnerSoul$needsSave = true;
        }
        spawnerSoul$persistInitialization();
        return spawnerSoul$remaining;
    }

    @Override
    public void spawnerSoul$setRemainingSpawns(int remainingSpawns) {
        if (remainingSpawns < -1) {
            throw new IllegalArgumentException("Remaining spawns must be -1 or nonnegative");
        }
        spawnerSoul$remaining = remainingSpawns;
        spawnerSoul$needsSave = true;
        spawnerSoul$persistInitialization();
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void spawnerSoul$load(CompoundTag tag, CallbackInfo ci) {
        int stored = tag.contains(SPAWNER_SOUL_REMAINING, Tag.TAG_INT) ? tag.getInt(SPAWNER_SOUL_REMAINING) : -2;
        spawnerSoul$remaining = stored >= -1 ? stored : SpawnerSoul.CONFIG.current().defaultSpawnCount();
        spawnerSoul$needsSave = stored < -1;
    }

    @Unique
    private void spawnerSoul$persistInitialization() {
        SpawnerBlockEntity block = (SpawnerBlockEntity) (Object) this;
        // Chunk loading calls load before setLevel. Keep this pending until a server
        // tick/use can mark the owning chunk dirty, even if every spawn attempt fails.
        if (spawnerSoul$needsSave && block.getLevel() != null && !block.getLevel().isClientSide) {
            block.setChanged();
            spawnerSoul$needsSave = false;
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void spawnerSoul$save(CompoundTag tag, CallbackInfo ci) {
        tag.putInt(SPAWNER_SOUL_REMAINING, spawnerSoul$getRemainingSpawns());
    }
}
