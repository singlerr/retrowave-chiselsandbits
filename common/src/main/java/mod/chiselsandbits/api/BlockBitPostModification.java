package mod.chiselsandbits.api;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface BlockBitPostModification {
    EventResult handle(Level level, BlockPos blockPos);
}
