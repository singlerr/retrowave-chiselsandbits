package mod.chiselsandbits.render.chiseledblock;

import mod.flatcoloredblocks.block.BlockFlatColored;
import mod.flatcoloredblocks.block.EnumFlatBlockType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;

public final class BlockDiscriminator {

    public static boolean isLightSource(Block block) {
        if (block instanceof BlockFlatColored) {
            return ((BlockFlatColored) block).getType() == EnumFlatBlockType.GLOWING;
        }
        return block.equals(Blocks.GLOWSTONE) || block.equals(Blocks.SEA_LANTERN);
    }

    public static boolean isTransparent(Block block) {
        if (block instanceof BlockFlatColored) {
            return ((BlockFlatColored) block).getType() == EnumFlatBlockType.TRANSPARENT;
        }

        return block instanceof StainedGlassBlock;
    }
}
