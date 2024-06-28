package mod.chiselsandbits.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReflectionHelperBlock extends Block {
    public String MethodName;

    private void markMethod() {
        MethodName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
    }

    public ReflectionHelperBlock() {
        super(Properties.of().air());
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        markMethod();
        return null;
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        markMethod();
        return null;
    }

    @Override
    public VoxelShape getShape(
            BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        markMethod();
        return null;
    }

    @Override
    public float getExplosionResistance() {
        markMethod();
        return 0;
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        markMethod();
        return Lists.newArrayList();
    }
}
