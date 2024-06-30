package mod.chiselsandbits.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class CreativeClipboardTab extends ItemGroup implements ICacheClearable {
    static boolean renewMappings = true;
    private static List<ItemStack> myWorldItems = new ArrayList<ItemStack>();
    private static List<CompoundNBT> myCrossItems = new ArrayList<CompoundNBT>();
    private static ClipboardStorage clipStorage = null;

    public static void load(final File file) {
        clipStorage = new ClipboardStorage(file);
        myCrossItems = clipStorage.read();
    }

    public static void addItem(final ItemStack iss) {
        // this is a client side things.
        if (EffectiveSide.get().isClient()) {
            final IBitAccess bitData = ChiselsAndBits.getApi().createBitItem(iss);

            if (bitData == null) {
                return;
            }

            final ItemStack is = bitData.getBitsAsItem(null, ItemType.CHISELED_BLOCK, true);

            if (is == null) {
                return;
            }

            // remove duplicates if they exist...
            for (final CompoundNBT isa : myCrossItems) {
                if (isa.equals(is.getTag())) {
                    myCrossItems.remove(isa);
                    break;
                }
            }

            // add item to front...
            myCrossItems.add(0, is.getTag());

            // remove extra items from back..
            while (myCrossItems.size()
                            > ChiselsAndBits.getConfig()
                                    .getServer()
                                    .creativeClipboardSize
                                    .get()
                    && !myCrossItems.isEmpty()) {
                myCrossItems.remove(myCrossItems.size() - 1);
            }

            clipStorage.write(myCrossItems);
            myWorldItems.clear();
            renewMappings = true;
        }
    }

    public CreativeClipboardTab() {
        super(ChiselsAndBits.MODID + ".Clipboard");
        ChiselsAndBits.getInstance().addClearable(this);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModItems.ITEM_MIRROR_PRINT_WRITTEN.get());
    }

    @Override
    public void fill(final NonNullList<ItemStack> items) {
        if (renewMappings) {
            myWorldItems.clear();
            renewMappings = false;

            for (final CompoundNBT nbt : myCrossItems) {
                final NBTBlobConverter c = new NBTBlobConverter();
                c.readChisleData(nbt.getCompound(ModUtil.NBT_BLOCKENTITYTAG), VoxelBlob.VERSION_ANY);

                // recalculate.
                c.updateFromBlob();

                final ItemStack worldItem = c.getItemStack(false);

                if (worldItem != null) {
                    myWorldItems.add(worldItem);
                }
            }
        }

        items.addAll(myWorldItems);
    }

    @Override
    public void clearCache() {
        renewMappings = true;
    }
}
