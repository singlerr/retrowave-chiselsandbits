package mod.chiselsandbits.block.entity;

import mod.chiselsandbits.registry.ModBlockEntityRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChiseledBlockEntity extends BlockEntity {
    public ChiseledBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityRegistries.CHISELED_BLOCK_ENTITY.get(), blockPos, blockState);
    }
}
