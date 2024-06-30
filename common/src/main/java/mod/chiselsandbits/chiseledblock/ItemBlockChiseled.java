package mod.chiselsandbits.chiseledblock;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import dev.architectury.event.EventResult;
import mod.chiselsandbits.api.BlockBitModification;
import mod.chiselsandbits.api.ChiselsAndBitsEvents;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.chiseledblock.BlockChiseled.ReplaceWithChiseledValue;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.chiseledblock.data.IntegerBox;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.BitOperation;
import mod.chiselsandbits.helpers.DeprecationHelper;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IItemScrollWheel;
import mod.chiselsandbits.interfaces.IVoxelBlobItem;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.network.packets.PacketAccurateSneakPlace;
import mod.chiselsandbits.network.packets.PacketAccurateSneakPlace.IItemBlockAccurate;
import mod.chiselsandbits.network.packets.PacketRotateVoxelBlob;
import mod.chiselsandbits.render.helpers.SimpleInstanceCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBlockChiseled extends BlockItem implements IVoxelBlobItem, IItemScrollWheel, IItemBlockAccurate {

    SimpleInstanceCache<ItemStack, List<Component>> tooltipCache =
            new SimpleInstanceCache<ItemStack, List<Component>>(null, new ArrayList<Component>());

    public ItemBlockChiseled(final Block block, Item.Properties builder) {
        super(block, builder);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, tooltip, tooltipFlag);
        ChiselsAndBits.getConfig()
                .getCommon()
                .helpText(
                        LocalStrings.HelpChiseledBlock,
                        tooltip,
                        ClientSide.instance.getKeyName(Minecraft.getInstance().options.keyUse),
                        ClientSide.instance.getKeyName(ClientSide.getOffGridPlacementKey()));

        if (itemStack.hasTag()) {
            if (ClientSide.instance.holdingShift()) {
                if (tooltipCache.needsUpdate(itemStack)) {
                    final VoxelBlob blob = ModUtil.getBlobFromStack(itemStack, null);
                    tooltipCache.updateCachedValue(blob.listContents(new ArrayList<>()));
                }

                tooltip.addAll(tooltipCache.getCached());
            } else {
                tooltip.add(Component.literal(LocalStrings.ShiftDetails.getLocal()));
            }
        }
    }

    @Override
    protected boolean canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState) {
        return canPlaceBlockHere()
    }

    @Override
    protected boolean canPlace(final BlockItemUseContext p_195944_1_, final BlockState p_195944_2_) {
        // TODO: Check for offgrid logic.
        return canPlaceBlockHere(
                p_195944_1_.getWorld(),
                p_195944_1_.getPos(),
                p_195944_1_.getFace(),
                p_195944_1_.getPlayer(),
                p_195944_1_.getHand(),
                p_195944_1_.getItem(),
                p_195944_1_.getHitVec().x,
                p_195944_1_.getHitVec().y,
                p_195944_1_.getHitVec().z,
                false);
    }

    public boolean vanillaStylePlacementTest(
            final @Nonnull World worldIn,
            @Nonnull BlockPos pos,
            @Nonnull Direction side,
            final PlayerEntity player,
            final Hand hand,
            final ItemStack stack) {
        final Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.SNOW) {
            side = Direction.UP;
        } else if (!block.isReplaceable(
                worldIn.getBlockState(pos),
                new BlockItemUseContext(
                        player, hand, stack, new BlockRayTraceResult(new Vector3d(0.5, 0.5, 0.5), side, pos, false)))) {
            pos = pos.offset(side);
        }

        return true;
    }

    public boolean canPlaceBlockHere(
            final @NotNull Level worldIn,
            final @NotNull BlockPos pos,
            final @NotNull Direction side,
            final Player player,
            final InteractionHand hand,
            final ItemStack stack,
            final double hitX,
            final double hitY,
            final double hitZ,
            boolean offgrid) {
        if (vanillaStylePlacementTest(worldIn, pos, side, player, hand, stack)) {
            return true;
        }

        if (offgrid) {
            return true;
        }

        if (tryPlaceBlockAt(
                getBlock(), stack, player, worldIn, pos, side, InteractionHand.MAIN_HAND, hitX, hitY, hitZ, null, false)) {
            return true;
        }

        return tryPlaceBlockAt(
                getBlock(),
                stack,
                player,
                worldIn,
                pos.offset(side),
                side,
                InteractionHand.MAIN_HAND,
                hitX,
                hitY,
                hitZ,
                null,
                false);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final ItemStack stack = context.getPlayer().getItemInHand(context.getHand());

        if (context.getLevel().isClientSide()) {
            // Say it "worked", Don't do anything we'll get a better packet.
            return InteractionResult.SUCCESS;
        }

        // send accurate packet.
        final PacketAccurateSneakPlace pasp = new PacketAccurateSneakPlace(
                context.getItemInHand(),
                context.getClickedPos(),
                context.getHand(),
                context.getClickedFace(),
                context.getClickLocation().x,
                context.getClickLocation().y,
                context.getClickLocation().z,
                ClientSide.offGridPlacement(context.getPlayer()) // TODO: Figure out the placement logic.
        );

        ChiselsAndBits.getNetworkChannel().sendToServer(pasp);
        // TODO: Figure out the placement logic.
        return tryPlace(new BlockPlaceContext(context), ClientSide.offGridPlacement(context.getPlayer()));
    }

    @Override
    public InteractionResult place(BlockPlaceContext blockPlaceContext) {
        return tryPlace(blockPlaceContext, false);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState blockState) {
        return placeBitBlock(
                context.getItemInHand(),
                context.getPlayer(),
                context.getLevel(),
                context.getClickedPos(),
                context.getClickedFace(),
                context.getClickLocation().x,
                context.getClickLocation().y,
                context.getClickLocation().z,
                blockState,
                false);    }


    public boolean placeBitBlock(
            final ItemStack stack,
            final Player player,
            final Level world,
            final BlockPos pos,
            final Direction side,
            final double hitX,
            final double hitY,
            final double hitZ,
            final BlockState newState,
            boolean offgrid) {
        if (offgrid) {
            final BitLocation bl = new BitLocation(
                    new BlockHitResult(new Vec3(hitX, hitY, hitZ), side, pos, false), BitOperation.PLACE);
            return tryPlaceBlockAt(
                    getBlock(),
                    stack,
                    player,
                    world,
                    bl.blockPos,
                    side,
                    InteractionHand.MAIN_HAND,
                    hitX,
                    hitY,
                    hitZ,
                    new BlockPos(bl.bitX, bl.bitY, bl.bitZ),
                    true);
        } else {
            return tryPlaceBlockAt(
                    getBlock(), stack, player, world, pos, side, InteractionHand.MAIN_HAND, hitX, hitY, hitZ, null, true);
        }
    }

    public static boolean tryPlaceBlockAt(
            final @NotNull Block block,
            final @NotNull ItemStack stack,
            final @NotNull Player player,
            final @NotNull Level world,
            @NotNull BlockPos pos,
            final @NotNull Direction side,
            final @NotNull InteractionHand hand,
            final double hitX,
            final double hitY,
            final double hitZ,
            final BlockPos partial,
            final boolean modulateWorld) {
        final VoxelBlob[][][] blobs = new VoxelBlob[2][2][2];

        // you can't place empty blocks...
        if (!stack.hasTag()) {
            return false;
        }


        final VoxelBlob source = ModUtil.getBlobFromStack(stack, player);

        final IntegerBox modelBounds = source.getBounds();
        BlockPos offset = partial == null || modelBounds == null
                ? new BlockPos(0, 0, 0)
                : ModUtil.getPartialOffset(side, partial, modelBounds);

        pos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        if (offset.getX() < 0) {
            pos = pos.offset(-1, 0, 0);
            offset = offset.offset(VoxelBlob.dim, 0, 0);
        }

        if (offset.getY() < 0) {
            pos = pos.offset(0, -1, 0);
            offset = offset.offset(0, VoxelBlob.dim, 0);
        }

        if (offset.getZ() < 0) {
            pos = pos.offset(0, 0, -1);
            offset = offset.offset(0, 0, VoxelBlob.dim);
        }

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    blobs[x][y][z] = source.offset(
                            offset.getX() - source.detail * x,
                            offset.getY() - source.detail * y,
                            offset.getZ() - source.detail * z);
                    final int solids = blobs[x][y][z].filled();
                    if (solids > 0) {
                        final BlockPos bp = pos.offset(x, y, z);

                        EventResult eventResult = ChiselsAndBitsEvents.BLOCK_BIT_MODIFICATION.invoker().handle(world, bp, player, hand, stack, true);


                        // test permissions.
                        if (!world.isBlockModifiable(player, bp) || eventResult.isFalse()) {
                            return false;
                        }

                        if (world.getBlockState(bp).isAir()
                                || world.getBlockState(bp)
                                    .canBeReplaced(new BlockPlaceContext(
                                                player,
                                                hand,
                                                stack,
                                                new BlockHitResult(
                                                        new Vec3(hitX, hitY, hitZ), side, pos, false)))) {
                            continue;
                        }

                        final TileEntityBlockChiseled target = ModUtil.getChiseledTileEntity(world, bp, true);
                        if (target != null) {
                            if (!target.canMerge(blobs[x][y][z])) {
                                return false;
                            }

                            blobs[x][y][z] = blobs[x][y][z].merge(target.getBlob());
                            continue;
                        }

                        return false;
                    }
                }
            }
        }

        if (modulateWorld) {
            UndoTracker.getInstance().beginGroup(player);
            try {
                for (int x = 0; x < 2; x++) {
                    for (int y = 0; y < 2; y++) {
                        for (int z = 0; z < 2; z++) {
                            if (blobs[x][y][z].filled() > 0) {
                                final BlockPos bp = pos.add(x, y, z);
                                final BlockState state = world.getBlockState(bp);

                                if (world.getBlockState(bp)
                                        .isReplaceable(new BlockItemUseContext(
                                                player,
                                                hand,
                                                stack,
                                                new BlockRayTraceResult(
                                                        new Vector3d(hitX, hitY, hitZ),
                                                        side,
                                                        bp,
                                                        false) // TODO: Figure is a recalc of the hit vector is needed
                                                // here.
                                                ))) {
                                    // clear it...
                                    world.setBlockState(bp, Blocks.AIR.getDefaultState());
                                }

                                if (world.isAirBlock(bp)) {
                                    final int commonBlock = blobs[x][y][z].getVoxelStats().mostCommonState;
                                    ReplaceWithChiseledValue rv =
                                            BlockChiseled.replaceWithChiseled(world, bp, state, commonBlock, true);
                                    if (rv.success && rv.te != null) {
                                        rv.te.completeEditOperation(blobs[x][y][z]);
                                    }

                                    continue;
                                }

                                final TileEntityBlockChiseled target = ModUtil.getChiseledTileEntity(world, bp, true);
                                if (target != null) {
                                    target.completeEditOperation(blobs[x][y][z]);
                                    continue;
                                }

                                return false;
                            }
                        }
                    }
                }
            } finally {
                UndoTracker.getInstance().endGroup(player);
            }
        }

        return true;
    }

    @Override
    public ITextComponent getDisplayName(final ItemStack stack) {
        final CompoundNBT comp = stack.getTag();

        if (comp != null) {
            final CompoundNBT BlockEntityTag = comp.getCompound(ModUtil.NBT_BLOCKENTITYTAG);
            if (BlockEntityTag != null) {
                final NBTBlobConverter c = new NBTBlobConverter();
                c.readChisleData(BlockEntityTag, VoxelBlob.VERSION_ANY);

                final BlockState state = c.getPrimaryBlockState();
                ITextComponent name = ItemChiseledBit.getBitStateName(state);

                if (name != null) {
                    final ITextComponent parent = super.getDisplayName(stack);
                    if (!(parent instanceof IFormattableTextComponent)) return parent;

                    final IFormattableTextComponent formattedParent = (IFormattableTextComponent) parent;
                    return formattedParent.appendString(" - ").append(name);
                }
            }
        }

        return super.getDisplayName(stack);
    }

    @Override
    public void scroll(final PlayerEntity player, final ItemStack stack, final int dwheel) {
        final PacketRotateVoxelBlob p = new PacketRotateVoxelBlob(
                Direction.Axis.Y, dwheel > 0 ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
        ChiselsAndBits.getNetworkChannel().sendToServer(p);
    }

    @Override
    public void rotate(final ItemStack stack, final Direction.Axis axis, final Rotation rotation) {
        Direction side = ModUtil.getSide(stack);

        if (axis == Axis.Y) {
            switch (rotation) {
                case CLOCKWISE_180:
                    side = side.rotateY();
                case CLOCKWISE_90:
                    side = side.rotateY();
                    break;
                case COUNTERCLOCKWISE_90:
                    side = side.rotateYCCW();
                    break;
                default:
                case NONE:
                    break;
            }
        } else {
            IBitAccess ba = ChiselsAndBits.getApi().createBitItem(stack);
            ba.rotate(axis, rotation);
            stack.setTag(ba.getBitsAsItem(side, ChiselsAndBits.getApi().getItemType(stack), false)
                    .getTag());
        }

        ModUtil.setSide(stack, side);
    }

    @Override
    public ActionResultType tryPlace(final ItemUseContext context, final boolean offgrid) {
        final BlockState state = context.getWorld().getBlockState(context.getPos());
        final Block block = state.getBlock();

        Direction side = context.getFace();
        BlockPos pos = context.getPos();

        if (block == Blocks.SNOW && state.get(SnowBlock.LAYERS).intValue() < 1) {
            side = Direction.UP;
        } else {
            boolean canMerge = false;
            if (context.getItem().hasTag()) {
                final TileEntityBlockChiseled tebc =
                        ModUtil.getChiseledTileEntity(context.getWorld(), context.getPos(), true);

                if (tebc != null) {
                    final VoxelBlob blob = ModUtil.getBlobFromStack(context.getItem(), context.getPlayer());
                    canMerge = tebc.canMerge(blob);
                }
            }

            BlockItemUseContext replacementCheckContext = context instanceof BlockItemUseContext
                    ? (BlockItemUseContext) context
                    : new BlockItemUseContext(context);
            if (context.getPlayer()
                            .getEntityWorld()
                            .getBlockState(context.getPos())
                            .getBlock()
                    instanceof BlockChiseled) {
                replacementCheckContext = new DirectionalPlaceContext(
                        context.getWorld(), pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP);
            }

            if (!canMerge && !offgrid && !state.isReplaceable(replacementCheckContext)) {
                pos = pos.offset(side);
            }
        }

        if (ModUtil.isEmpty(context.getItem())) {
            return ActionResultType.FAIL;
        } else if (!context.getPlayer().canPlayerEdit(pos, side, context.getItem())) {
            return ActionResultType.FAIL;
        } else if (pos.getY() == 255
                && DeprecationHelper.getStateFromItem(context.getItem())
                        .getMaterial()
                        .isSolid()) {
            return ActionResultType.FAIL;
        } else if (context instanceof BlockItemUseContext
                && canPlaceBlockHere(
                        context.getWorld(),
                        pos,
                        side,
                        context.getPlayer(),
                        context.getHand(),
                        context.getItem(),
                        context.getHitVec().x,
                        context.getHitVec().y,
                        context.getHitVec().z,
                        offgrid)) {
            final int i = context.getItem().getDamage();
            final BlockState BlockState1 = getStateForPlacement((BlockItemUseContext) context);

            if (placeBitBlock(
                    context.getItem(),
                    context.getPlayer(),
                    context.getWorld(),
                    pos,
                    side,
                    context.getHitVec().x,
                    context.getHitVec().y,
                    context.getHitVec().z,
                    BlockState1,
                    offgrid)) {
                context.getWorld()
                        .playSound(
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                DeprecationHelper.getSoundType(this.getBlock()).getPlaceSound(),
                                SoundCategory.BLOCKS,
                                (DeprecationHelper.getSoundType(this.block).getVolume() + 1.0F) / 2.0F,
                                DeprecationHelper.getSoundType(this.block).getPitch() * 0.8F,
                                false);

                if (!context.getPlayer().isCreative() && context.getItem().getItem() instanceof ItemBlockChiseled)
                    ModUtil.adjustStackSize(context.getItem(), -1);

                return ActionResultType.SUCCESS;
            }

            return ActionResultType.FAIL;
        } else {
            return ActionResultType.FAIL;
        }
    }
}
