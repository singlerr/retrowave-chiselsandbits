package mod.chiselsandbits.api;

import dev.architectury.event.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface FullBlockRestoration {
    EventResult handle(Level level, BlockPos blockPos, BlockState restoredState);
}
