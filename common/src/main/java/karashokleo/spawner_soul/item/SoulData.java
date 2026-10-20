package karashokleo.spawner_soul.item;

import karashokleo.spawner_soul.SpawnerSoul;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/** A soul carries only a registered entity type and its unspent spawn count. */
public record SoulData(ResourceLocation entityId, int remainingSpawns) {

    private static final String ENTITY = "Entity";
    private static final String REMAINING = "RemainingSpawns";

    public static Optional<SoulData> read(ItemStack stack) {
        CompoundTag data = stack.getTagElement(SpawnerSoul.MOD_ID);
        if (data == null || !data.contains(ENTITY, Tag.TAG_STRING) || !data.contains(REMAINING, Tag.TAG_INT)) {
            return Optional.empty();
        }
        ResourceLocation id = ResourceLocation.tryParse(data.getString(ENTITY));
        int remaining = data.getInt(REMAINING);
        if (id == null || remaining < -1 || remaining == 0
                || BuiltInRegistries.ENTITY_TYPE.getOptional(id).filter(EntityType::canSummon).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new SoulData(id, remaining));
    }

    public void write(ItemStack stack) {
        CompoundTag data = stack.getOrCreateTagElement(SpawnerSoul.MOD_ID);
        data.putString(ENTITY, entityId.toString());
        data.putInt(REMAINING, remainingSpawns);
    }
}
