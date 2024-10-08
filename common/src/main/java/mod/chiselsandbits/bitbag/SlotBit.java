package mod.chiselsandbits.bitbag;

import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotBit extends Slot {

    public SlotBit(final IInventory inventoryIn, final int index, final int xPosition, final int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(final ItemStack stack) {
        return ModUtil.notEmpty(stack) && stack.getItem() instanceof ItemChiseledBit;
    }
}
