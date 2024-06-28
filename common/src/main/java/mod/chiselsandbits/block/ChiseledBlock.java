package mod.chiselsandbits.block;

import lombok.extern.slf4j.Slf4j;
import mod.chiselsandbits.block.entity.ChiseledBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class ChiseledBlock extends Block implements EntityBlock {

    private static ThreadLocal<BlockState> actingAs = new ThreadLocal<>();

    public static final BooleanProperty FULL_BLOCK = BooleanProperty.create("full_block");

    private final String name;

    public ChiseledBlock(String name, Properties properties) {
        super(properties.isRedstoneConductor((blockState, blockGetter, blockPos) -> isFullCube(blockState)));
        this.name = name;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ChiseledBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        BlockPos target = blockPlaceContext.getClickedPos();
        if (!(blockPlaceContext instanceof DirectionalPlaceContext) && !blockPlaceContext.replacingClickedOnBlock()) {
            target = target.offset(
                    blockPlaceContext.getClickedFace().getOpposite().getNormal());
        }

        if (!(blockPlaceContext.getLevel().getBlockEntity(target) instanceof ChiseledBlockEntity blockEntity)) {
            log.error("Expected ChiseledBlockEntity at {} but not found", target);
            return super.canBeReplaced(blockState, blockPlaceContext);
        }
    }

    @Override
    public void playerDestroy(
            Level level,
            Player player,
            BlockPos blockPos,
            BlockState blockState,
            @Nullable BlockEntity blockEntity,
            ItemStack itemStack) {
        if (!willHarvest
                && ChiselsAndBits.getConfig()
                        .getClient()
                        .addBrokenBlocksToCreativeClipboard
                        .get()) {

            try {
                final TileEntityBlockChiseled tebc = getTileEntity(world, pos);
                CreativeClipboardTab.addItem(tebc.getItemStack(player));

                UndoTracker.getInstance()
                        .add(world, pos, tebc.getBlobStateReference(), new VoxelBlobStateReference(0, 0));
            } catch (final ExceptionNoTileEntity e) {
                Log.noTileError(e);
            }
        }
    }

    private static boolean isFullCube(BlockState state) {
        return state.getValue(FULL_BLOCK);
    }
}
