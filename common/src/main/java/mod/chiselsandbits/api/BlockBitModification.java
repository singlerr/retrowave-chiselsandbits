package mod.chiselsandbits.api;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface BlockBitModification {
    EventResult handle(
            Level level, BlockPos blockPos, Player player, InteractionHand hand, ItemStack stackUsed, boolean placing);
}
