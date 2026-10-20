package karashokleo.spawner_soul.item;

import karashokleo.spawner_soul.SpawnerSoul;
import karashokleo.spawner_soul.spawner.SpawnCounts;
import karashokleo.spawner_soul.spawner.SpawnerOperations;
import karashokleo.spawner_soul.spawner.SpawnerState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/** Applies a stored soul without ever changing state or consuming items on a rejected use. */
public final class SpawnerSoulItem extends Item {

    public SpawnerSoulItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level.getBlockEntity(context.getClickedPos()) instanceof SpawnerBlockEntity spawner)) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        Optional<SoulData> stored = SoulData.read(stack);
        Player player = context.getPlayer();
        if (stored.isEmpty() || (player != null && (!level.mayInteract(player, context.getClickedPos())
                || !player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)))) {
            return InteractionResult.FAIL;
        }
        // Never evaluate client-local configuration as authority for a server interaction.
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        SoulData soul = stored.get();
        SpawnerState state = (SpawnerState) spawner;
        Optional<ResourceLocation> oldEntity = SpawnerOperations.entityId(spawner);
        boolean sameEntity = oldEntity.filter(soul.entityId()::equals).isPresent();
        int current = oldEntity.isEmpty() ? 0 : state.spawnerSoul$getRemainingSpawns();
        OptionalInt result = SpawnCounts.apply(current, soul.remainingSpawns(),
                sameEntity, SpawnerSoul.CONFIG.current());
        if (result.isEmpty()) {
            if (player != null) {
                player.displayClientMessage(Component.translatable("message.spawner_soul.rejected"), true);
            }
            return InteractionResult.FAIL;
        }
        SpawnerOperations.apply(spawner, soul.entityId(), result.getAsInt());
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Optional<SoulData> stored = SoulData.read(stack);
        if (stored.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.spawner_soul.empty").withStyle(ChatFormatting.GRAY));
            return;
        }
        SoulData soul = stored.get();
        tooltip.add(Component.translatable("tooltip.spawner_soul.entity",
                BuiltInRegistries.ENTITY_TYPE.get(soul.entityId()).getDescription()).withStyle(ChatFormatting.GRAY));
        Component count = soul.remainingSpawns() == SpawnCounts.INFINITE
                ? Component.translatable("tooltip.spawner_soul.infinite")
                : Component.literal(Integer.toString(soul.remainingSpawns()));
        tooltip.add(Component.translatable("tooltip.spawner_soul.remaining", count).withStyle(ChatFormatting.GRAY));
    }
}
