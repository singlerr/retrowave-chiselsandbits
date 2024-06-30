package mod.chiselsandbits.chiseledblock;

import javax.annotation.Nonnull;
import mod.chiselsandbits.api.BoxType;
import mod.chiselsandbits.api.IMultiStateBlock;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.chiseledblock.data.VoxelShapeCache;
import mod.chiselsandbits.client.CreativeClipboardTab;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.BitOperation;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.ExceptionNoTileEntity;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.utils.SingleBlockBlockReader;
import mod.chiselsandbits.utils.SingleBlockWorldReader;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockChiseled extends Block implements EntityBlock, IMultiStateBlock {

    public static final BlockPos ZERO = BlockPos.ZERO;

    private static ThreadLocal<BlockState> actingAs = new ThreadLocal<BlockState>();

    public static final BooleanProperty FULL_BLOCK = BooleanProperty.create("full_block");

    public final String name;

    public BlockChiseled(final String name, final BlockBehaviour.Properties properties) {
        super(properties.isRedstoneConductor((blockState, blockGetter, blockPos) -> isFullCube(blockState))
                .pushReaction(PushReaction.BLOCK));
        this.name = name;
        registerDefaultState(defaultBlockState().setValue(FULL_BLOCK, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FULL_BLOCK);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (ChiselsAndBits.getConfig()
                .getClient()
                .addBrokenBlocksToCreativeClipboard
                .get()) {

            try {
                final TileEntityBlockChiseled tebc = getTileEntity(level, blockPos);
                CreativeClipboardTab.addItem(tebc.getItemStack(player));

                UndoTracker.getInstance()
                        .add(level, blockPos, tebc.getBlobStateReference(), new VoxelBlobStateReference(0, 0));
            } catch (final ExceptionNoTileEntity e) {
                Log.noTileError(e);
            }
        }

        return super.playerWillDestroy(level, blockPos, blockState, player);
    }




//    @OnlyIn(Dist.CLIENT)
//    @Override
//    public float getAmbientOcclusionLightValue(final BlockState state, final IBlockReader worldIn, final BlockPos pos) {
//        return isFullCube(state) ? 0.2F : 1F;
//    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        try {
            BlockPos target = blockPlaceContext.getClickedPos();
            if (!(blockPlaceContext instanceof DirectionalPlaceContext) && !blockPlaceContext.replacingClickedOnBlock()) {
                target = target.offset(blockPlaceContext.getClickedFace().getOpposite().getNormal());
            }

            return getTileEntity(blockPlaceContext.getLevel(), target).getBlob().filled() == 0;
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            return super.canBeReplaced(blockState, blockPlaceContext);
        }

    }


    @Override
    public float getFriction() {
        return super.getFriction();
    }


//    @Override
//    public float getSlipperiness(
//            final BlockState state, final IWorldReader world, final BlockPos pos, @Nullable final Entity entity) {
//        try {
//            BlockState internalState = getTileEntity(world, pos).getBlockState(Blocks.STONE);
//
//            if (internalState != null) {
//                return internalState
//                        .getBlock()
//                        .getSlipperiness(
//                                internalState,
//                                new SingleBlockWorldReader(internalState, internalState.getBlock(), world),
//                                BlockPos.ZERO,
//                                entity);
//            }
//        } catch (ExceptionNoTileEntity e) {
//            Log.noTileError(e);
//        }
//
//        return super.getSlipperiness(state, world, pos, entity);
//    }

    static ExceptionNoTileEntity noTileEntity = new ExceptionNoTileEntity();

    public static @NotNull TileEntityBlockChiseled getTileEntity(final BlockEntity te) throws ExceptionNoTileEntity {
        if (te == null) {
            throw noTileEntity;
        }

        try {
            return (TileEntityBlockChiseled) te;
        } catch (final ClassCastException e) {
            throw noTileEntity;
        }
    }

    public static @NotNull TileEntityBlockChiseled getTileEntity(
            final @NotNull LevelReader world, final @NotNull BlockPos pos) throws ExceptionNoTileEntity {
        final BlockEntity te = ModUtil.getTileEntitySafely(world, pos);
        return getTileEntity(te);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }


    public static boolean isFullCube(final BlockState state) {
        return state.getValue(FULL_BLOCK);
    }

    @Override
    public float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
        if (ChiselsAndBits.getConfig().getServer().enableToolHarvestLevels.get()) {
            BlockState activeState = actingAs.get();

            if (activeState == null) {
                activeState = getPrimaryState(blockGetter, blockPos);
            }

            return activeState.getDestroyProgress(player,new SingleBlockBlockReader(activeState), blockPos);
        }

        return super.getDestroyProgress(blockState, player, blockGetter, blockPos);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        try {
            spawnAfterBreak(blockState, level, blockPos, getTileEntity(blockEntity).getItemStack(player));
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);

        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState blockState, @Nullable LivingEntity placer, ItemStack stack) {
        try {
            if (stack == null || placer == null || !stack.hasTag()) {
                return;
            }

            final TileEntityBlockChiseled bc = getTileEntity(worldIn, pos);
            if (! worldIn.isClientSide()) {
                bc.getState();
            }
            int rotations = ModUtil.getRotations(placer, ModUtil.getSide(stack));

            VoxelBlob blob = bc.getBlob();
            while (rotations-- > 0) {
                blob = blob.spin(Direction.Axis.Y);
            }
            bc.setBlob(blob);
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        try {
            return getPickBlock(levelReader, blockPos, getTileEntity(levelReader, blockPos));
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            return ModUtil.getEmptyStack();
        }
    }


    /**
     * Client side method.
     */
    private ChiselToolType getClientHeldTool() {
        return ClientSide.instance.getHeldToolType(InteractionHand.MAIN_HAND);
    }

    public ItemStack getPickBlock(
            final LevelReader target, final BlockPos pos, final TileEntityBlockChiseled te) {
        if (! te.getLevel().isClientSide()) {
            if (getClientHeldTool() != null) {
                final VoxelBlob vb = te.getBlob();

                final BitLocation bitLoc = new BitLocation(target, BitOperation.CHISEL);

                final int itemBlock = vb.get(bitLoc.bitX, bitLoc.bitY, bitLoc.bitZ);
                if (itemBlock == 0) {
                    return ModUtil.getEmptyStack();
                }

                return ItemChiseledBit.createStack(itemBlock, 1, false);
            }

            return te.getItemStack(ClientSide.instance.getPlayer());
        }

        return te.getItemStack(null);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntityBlockChiseled();
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos blockPos, BlockState blockState) {
        try {
            final BlockState internalState = getTileEntity(level, blockPos).getBlockState(this);
            return ClientSide.instance.addBlockDestroyEffects(level, blockPos, internalState);
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
        }
    }


//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public boolean addHitEffects(
//            final BlockState state,
//            final World world,
//            final RayTraceResult target,
//            final ParticleManager effectRenderer) {
//        if (!(target instanceof BlockRayTraceResult)) return false;
//
//        try {
//            final BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) target;
//            final BlockPos pos = rayTraceResult.getPos();
//            final BlockState bs = getTileEntity(world, pos).getBlockState(this);
//            return ClientSide.instance.addHitEffects(world, rayTraceResult, bs, effectRenderer);
//        } catch (final ExceptionNoTileEntity e) {
//            Log.noTileError(e);
//            return true;
//        }
//    }


    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        try {
            final VoxelBlob blob = getTileEntity(blockGetter, blockPos).getBlob();
            if (blob == null) return Shapes.empty();

            return VoxelShapeCache.getInstance().get(blob, BoxType.OCCLUSION);
        } catch (ExceptionNoTileEntity exceptionNoTileEntity) {
            return Shapes.empty();
        }
    }

    private static TileEntityBlockChiseled getTileEntity(BlockGetter blockGetter, BlockPos pos) throws ExceptionNoTileEntity{
        BlockEntity b = blockGetter.getBlockEntity(pos);
        return getTileEntity(b);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        try {
            final VoxelBlob blob = getTileEntity(blockGetter, blockPos).getBlob();
            if (blob == null) return Shapes.empty();

            return VoxelShapeCache.getInstance().get(blob, BoxType.COLLISION);
        } catch (ExceptionNoTileEntity exceptionNoTileEntity) {
            return Shapes.empty();
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        try {
            final VoxelBlob blob = getTileEntity(blockGetter, blockPos).getBlob();
            if (blob == null) return Shapes.empty();

            return VoxelShapeCache.getInstance().get(blob, BoxType.OCCLUSION);
        } catch (ExceptionNoTileEntity exceptionNoTileEntity) {
            return Shapes.empty();
        }
    }



    public static boolean replaceWithChiseled(
            final Level world, final BlockPos pos, final BlockState originalState, final boolean triggerUpdate) {
        return replaceWithChiseled(world, pos, originalState, 0, triggerUpdate).success;
    }



//    @Override
//    public BlockState rotate(final BlockState state, final IWorld world, final BlockPos pos, final Rotation direction) {
//        try {
//            getTileEntity(world, pos).rotateBlock();
//            return state;
//        } catch (final ExceptionNoTileEntity e) {
//            Log.noTileError(e);
//            return state;
//        }
//    }

    public static class ReplaceWithChiseledValue {
        public boolean success = false;
        public TileEntityBlockChiseled te = null;
    }
    ;

    public static ReplaceWithChiseledValue replaceWithChiseled(
            final @NotNull Level world,
            final @NotNull BlockPos pos,
            final BlockState originalState,
            final int fragmentBlockStateID,
            final boolean triggerUpdate) {
        BlockState actingState = originalState;
        Block target = originalState.getBlock();
        final boolean isAir = world.getBlockState(pos).isAir()
                || actingState.canBeReplaced(
                        new DirectionalPlaceContext(world, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
        ReplaceWithChiseledValue rv = new ReplaceWithChiseledValue();

        if (BlockBitInfo.canChisel(actingState) || isAir) {
            BlockChiseled blk = ModBlocks.convertGivenStateToChiseledBlock(originalState);

            int BlockID = ModUtil.getStateId(actingState);

            if (isAir) {
                actingState = ModUtil.getStateById(fragmentBlockStateID);
                target = actingState.getBlock();
                BlockID = ModUtil.getStateId(actingState);
                blk = ModBlocks.convertGivenStateToChiseledBlock(actingState);
                // its still air tho..
                actingState = Blocks.AIR.defaultBlockState();
            }

            if (BlockID == 0) {
                return rv;
            }

            if (blk != null && blk != target) {
                TileEntityBlockChiseled.setLightFromBlock(actingState);
                world.setBlock(pos, blk.defaultBlockState(), triggerUpdate ? 3 : 0);
                TileEntityBlockChiseled.setLightFromBlock(null);
                final BlockEntity te = world.getBlockEntity(pos);

                TileEntityBlockChiseled tec;
                if (!(te instanceof TileEntityBlockChiseled)) {
                    tec = (TileEntityBlockChiseled) blk.newBlockEntity(blk.defaultBlockState(), world);
                    world.setBlockEntity(tec);
                } else {
                    tec = (TileEntityBlockChiseled) te;
                }

                if (tec != null) {
                    tec.fillWith(actingState);
                    tec.setPrimaryBlockStateId(BlockID);
                    tec.setState(tec.getState(), tec.getBlobStateReference());
                }

                rv.success = true;
                rv.te = tec;

                return rv;
            }
        }

        return rv;
    }

    public BlockState getCommonState(final TileEntityBlockChiseled te) {
        final VoxelBlobStateReference data = te.getBlobStateReference();

        if (data != null) {
            final VoxelBlob vb = data.getVoxelBlob();
            if (vb != null) {
                return ModUtil.getStateById(vb.getVoxelStats().mostCommonState);
            }
        }

        return null;
    }

    @Override
    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        // is this the right block?
        final BlockState realState = blockGetter.getBlockState(blockPos);
        final Block realBlock = realState.getBlock();
        if (realBlock != this) {
            return realBlock.getLightBlock(realState, blockGetter, blockPos);
        }

        // enabled?
        if (ChiselsAndBits.getConfig().getServer().enableBitLightSource.get()) {
            try {
                return getTileEntity(blockGetter, blockPos).getLightValue();
            } catch (final ExceptionNoTileEntity e) {
                Log.noTileError(e);
            }
        }

        return 0;
    }



    public static void setActingAs(final BlockState state) {
        actingAs.set(state);
    }
//
//    @Override
//    public float getPlayerRelativeBlockHardness(
//            final BlockState state, final PlayerEntity player, final IBlockReader worldIn, final BlockPos pos) {
//        if (ChiselsAndBits.getConfig().getServer().enableToolHarvestLevels.get()) {
//            BlockState actingState = actingAs.get();
//
//            if (actingState == null) {
//                actingState = getPrimaryState(worldIn, pos);
//            }
//
//            final float hardness = state.getBlockHardness(worldIn, pos);
//            if (hardness < 0.0F) {
//                return 0.0F;
//            }
//
//            // since we can't call getDigSpeed on the acting state, we can just
//            // do some math to try and roughly estimate it.
//            float denom = player.inventory.getDestroySpeed(actingState);
//            float numer = player.inventory.getDestroySpeed(state);
//
//            if (!state.canHarvestBlock(new SingleBlockBlockReader(state), ZERO, player)) {
//                return player.getDigSpeed(actingState, pos) / hardness / 100F * (numer / denom);
//            } else {
//                return player.getDigSpeed(actingState, pos) / hardness / 30F * (numer / denom);
//            }
//        }
//
//        return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
//    }


//    @Override
//    public boolean isToolEffective(final BlockState state, final ToolType tool) {
//
//        return Blocks.STONE.isToolEffective(Blocks.STONE.getDefaultState(), tool);
//    }

    public ResourceLocation getModel() {
        return new ResourceLocation(ChiselsAndBits.MODID, name);
    }

    @Override
    public BlockState getPrimaryState(BlockGetter world, BlockPos pos) {
        try {
            return getTileEntity(world, pos).getBlockState(Blocks.STONE);
        } catch (final ExceptionNoTileEntity e) {
            Log.noTileError(e);
            return Blocks.STONE.defaultBlockState();
        }

        return null;
    }

}
