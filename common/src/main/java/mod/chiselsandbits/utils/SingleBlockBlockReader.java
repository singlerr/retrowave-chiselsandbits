package mod.chiselsandbits.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class SingleBlockBlockReader implements BlockGetter {

    private final BlockState state;
    private final Block blk;

    public SingleBlockBlockReader(final BlockState state, final Block blk) {
        this.state = state;
        this.blk = blk;
    }

    public SingleBlockBlockReader(final BlockState state) {
        this.state = state;
        this.blk = state.getBlock();
    }

    @Override
    public BlockState getBlockState(final BlockPos pos) {
        if (pos == BlockPos.ZERO) {
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(final BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos) {
        if (blockPos.equals(BlockPos.ZERO) && state.hasBlockEntity()) {
            return ((EntityBlock) blk).newBlockEntity(blockPos, state);
        }

        return null;    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }
}
