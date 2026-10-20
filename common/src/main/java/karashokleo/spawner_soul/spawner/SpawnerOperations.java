package karashokleo.spawner_soul.spawner;

import karashokleo.spawner_soul.item.ModItems;
import karashokleo.spawner_soul.item.SoulData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/** Shared block-entity operations, deliberately independent of either loader. */
public final class SpawnerOperations {

    private SpawnerOperations() {
    }

    public static Optional<ResourceLocation> entityId(SpawnerBlockEntity blockEntity) {
        CompoundTag saved = blockEntity.getSpawner().save(new CompoundTag());
        return EntityType.by(saved.getCompound("SpawnData").getCompound("entity"))
                .filter(EntityType::canSummon).map(BuiltInRegistries.ENTITY_TYPE::getKey);
    }

    public static ItemStack createSoul(SpawnerBlockEntity blockEntity) {
        int remaining = ((SpawnerState) blockEntity).spawnerSoul$getRemainingSpawns();
        Optional<ResourceLocation> id = entityId(blockEntity);
        if (remaining == 0 || id.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack soul = new ItemStack(ModItems.SPAWNER_SOUL);
        new SoulData(id.get(), remaining).write(soul);
        return soul;
    }

    public static void apply(SpawnerBlockEntity blockEntity, ResourceLocation entityId, int remaining) {
        Level level = blockEntity.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }
        CompoundTag saved = blockEntity.getSpawner().save(new CompoundTag());
        CompoundTag entity = new CompoundTag();
        entity.putString("id", entityId.toString());
        CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);
        saved.put("SpawnData", spawnData);
        // Do not let a former weighted spawn pool silently restore the old entity type.
        saved.put("SpawnPotentials", new ListTag());
        ((SpawnerState) blockEntity).spawnerSoul$setRemainingSpawns(remaining);
        blockEntity.getSpawner().load(level, blockEntity.getBlockPos(), saved);
        sync(blockEntity);
    }

    public static void sync(SpawnerBlockEntity blockEntity) {
        blockEntity.setChanged();
        Level level = blockEntity.getLevel();
        if (level != null && !level.isClientSide) {
            BlockState blockState = blockEntity.getBlockState();
            level.sendBlockUpdated(blockEntity.getBlockPos(), blockState, blockState, Block.UPDATE_CLIENTS);
        }
    }
}
