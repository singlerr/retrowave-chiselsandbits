package mod.chiselsandbits.chiseledblock;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ReflectionHelperBlock extends Block {
    private final ThreadLocal<String> lastInvokedThreadLocalMethodName = ThreadLocal.withInitial(() -> "unknown");

    private void markMethod()
    {
        setLastInvokedThreadLocalMethodName(
                StackWalker.getInstance().walk(stream -> stream.filter(frame -> !frame.toString().contains("idea.debugger")).skip(1).findFirst().map(StackWalker.StackFrame::getMethodName).orElse("unknown"))
        );
    }

    public ReflectionHelperBlock()
    {
        super( Properties.of() );
    }

    @Nullable
    @Override
    public VoxelShape getOcclusionShape(@Nullable final BlockState state, @Nullable final BlockGetter worldIn, @Nullable final BlockPos pos)
    {
        markMethod();
        return Shapes.empty();
    }

    @Nullable
    @Override
    public VoxelShape getBlockSupportShape(@Nullable final BlockState state, @Nullable final BlockGetter reader, @Nullable final BlockPos pos)
    {
        markMethod();
        return Shapes.empty();
    }

    @Nullable
    @Override
    public VoxelShape getShape(@Nullable final BlockState state, @Nullable final BlockGetter worldIn, @Nullable final BlockPos pos, @Nullable final CollisionContext context)
    {
        markMethod();
        return Shapes.empty();
    }

    @Nullable
    @Override
    public VoxelShape getCollisionShape(@Nullable final BlockState state, @Nullable final BlockGetter worldIn, @Nullable final BlockPos pos, @Nullable final CollisionContext context)
    {
        markMethod();
        return Shapes.empty();
    }

    @Override
    public float getDestroyProgress(@Nullable final BlockState state, @Nullable final Player player, @Nullable final BlockGetter worldIn, @Nullable final BlockPos pos)
    {
        markMethod();
        return 0;
    }

    @Override
    public float getExplosionResistance()
    {
        markMethod();
        return 0;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        markMethod();
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        markMethod();
        return blockState;
    }

    @Override
    public SoundType getSoundType(BlockState blockState) {
        markMethod();
        return SoundType.AMETHYST;
    }


    @Override
    public List<ItemStack> getDrops(BlockState p_287732_, LootParams.Builder p_287596_) {
        markMethod();
        return Lists.newArrayList();
    }

    public String getLastInvokedThreadLocalMethodName() {
        return lastInvokedThreadLocalMethodName.get();
    }

    public void setLastInvokedThreadLocalMethodName(String lastInvokedThreadLocalMethodName) {
        this.lastInvokedThreadLocalMethodName.set(lastInvokedThreadLocalMethodName);
    }
}
