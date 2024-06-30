package mod.chiselsandbits.crafting;

import java.util.List;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.api.StateCount;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.BitIterator;
import mod.chiselsandbits.chiseledblock.data.IntegerBox;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.api.BitAccess;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemBitSaw;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.registry.ModRecipeSerializers;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BitSawCrafting extends SpecialRecipe {

    public BitSawCrafting(ResourceLocation name) {
        super(name);
    }

    private static class SawCraft {
        int sawPosX = -1;
        int blockPosX = -1;
        int sawPosY = -1;
        int blockPosY = -1;
        ItemStack chisledBlock = null;
    }
    ;

    private SawCraft getSawCraft(final CraftingInventory inv) {
        final SawCraft r = new SawCraft();

        for (int x = 0; x < inv.getWidth(); ++x) {
            for (int y = 0; y < inv.getHeight(); ++y) {
                final ItemStack is = inv.getStackInSlot(x + y * inv.getWidth());

                if (!ModUtil.isEmpty(is)) {
                    if (is.getItem() instanceof ItemBitSaw) {
                        if (r.sawPosX != -1) {
                            return null;
                        }

                        r.sawPosX = x;
                        r.sawPosY = y;
                        continue;
                    }

                    if (is.getItem() instanceof ItemBlockChiseled) {
                        if (r.blockPosX != -1) {
                            return null;
                        }

                        r.chisledBlock = is;
                        r.blockPosX = x;
                        r.blockPosY = y;
                        continue;
                    }

                    if (is != null && is.getItem() instanceof BlockItem) {
                        final BlockItem blkItem = (BlockItem) is.getItem();
                        final BlockState state = blkItem.getBlock().getDefaultState();

                        if (!BlockBitInfo.isSupported(state)) {
                            return null;
                        }

                        if (r.blockPosX != -1) {
                            return null;
                        }

                        r.chisledBlock = is;
                        r.blockPosX = x;
                        r.blockPosY = y;
                        continue;
                    }

                    return null;
                }
            }
        }

        if (r.sawPosX == -1 || r.blockPosX == -1) {
            return null;
        }

        return r;
    }

    @Override
    public boolean matches(final CraftingInventory inv, final World worldIn) {
        return getSawCraft(inv) != null;
    }

    @Override
    public ItemStack getCraftingResult(final CraftingInventory inv) {
        final SawCraft sc = getSawCraft(inv);

        if (sc == null) {
            return ModUtil.getEmptyStack();
        }

        final IBitAccess contents = ChiselsAndBits.getApi().createBitItem(sc.chisledBlock);
        if (contents == null) {
            return ModUtil.getEmptyStack();
        }

        final VoxelBlob blob = ((BitAccess) contents).getNativeBlob();

        final VoxelBlob a = new VoxelBlob();
        final VoxelBlob b = new VoxelBlob();

        final int sawOffsetX = sc.sawPosX - sc.blockPosX;
        final int sawOffsetY = sc.sawPosY - sc.blockPosY;

        Axis direction = null;
        if (sawOffsetY == 0) {
            direction = Axis.X;
        } else if (sawOffsetX == 0) {
            direction = Axis.Y;
        } else {
            direction = Axis.Z;
        }

        int split_pos = 7;
        final IntegerBox box = blob.getBounds();
        int scale = 0;

        switch (direction) {
            case X:
                split_pos = MathHelper.clamp((box.maxX + box.minX) / 2, 0, 15);
                scale = (box.maxX - box.minX) / 2;
                break;
            case Y:
                split_pos = MathHelper.clamp((box.maxY + box.minY) / 2, 0, 15);
                scale = (box.maxY - box.minY) / 2;
                break;
            case Z:
                split_pos = MathHelper.clamp((box.maxZ + box.minZ) / 2, 0, 15);
                scale = (box.maxZ - box.minZ) / 2;
                break;
        }

        final int split_pos_plus_one = MathHelper.clamp(split_pos + 1, 0, 15);

        final BitIterator bi = new BitIterator();
        while (bi.hasNext()) {
            final int state = bi.getNext(blob);
            if (state == 0) {
                continue;
            }

            switch (direction) {
                case X:
                    if (bi.x > split_pos) {
                        a.set(scale - (bi.x - split_pos_plus_one), bi.y - box.minY, bi.z - box.minZ, state);
                    } else {
                        b.set(bi.x - box.minX, bi.y - box.minY, bi.z - box.minZ, state);
                    }

                    break;
                case Y:
                    if (bi.y > split_pos) {
                        a.set(bi.x - box.minX, scale - (bi.y - split_pos_plus_one), bi.z - box.minZ, state);
                    } else {
                        b.set(bi.x - box.minX, bi.y - box.minY, bi.z - box.minZ, state);
                    }

                    break;
                case Z:
                    if (bi.z > split_pos) {
                        a.set(bi.x - box.minX, bi.y - box.minY, scale - (bi.z - split_pos_plus_one), state);
                    } else {
                        b.set(bi.x - box.minX, bi.y - box.minY, bi.z - box.minZ, state);
                    }

                    break;
            }
        }

        if (a.equals(b)) {
            final List<StateCount> refs = a.getStateCounts();

            if (refs.size() == 2) {
                boolean good = false;
                int outState = -1;
                for (final StateCount tr : refs) {
                    if (tr.stateId != 0 && tr.quantity == 1) {
                        outState = tr.stateId;
                    } else if (tr.stateId == 0 && tr.quantity == VoxelBlob.full_size - 1) {
                        good = true;
                    }
                }

                if (good && outState != -1) {
                    final ItemStack stack = ItemChiseledBit.createStack(outState, 2, false);

                    if (stack != null) {
                        return stack;
                    }
                }
            }

            blob.fill(a);
            final ItemStack out = contents.getBitsAsItem(null, ItemType.CHISELED_BLOCK, false);

            if (out != null) {
                ModUtil.setStackSize(out, 2);
                return out;
            }
        }

        return ModUtil.getEmptyStack();
    }

    @Override
    public boolean canFit(final int width, final int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ModUtil.getEmptyStack();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final CraftingInventory inv) {
        final NonNullList<ItemStack> aitemstack = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < aitemstack.size(); ++i) {
            final ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return aitemstack;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BIT_SAW_CRAFTING.get();
    }
}
