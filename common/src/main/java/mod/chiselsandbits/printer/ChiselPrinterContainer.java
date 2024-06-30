package mod.chiselsandbits.printer;

import mod.chiselsandbits.interfaces.IPatternItem;
import mod.chiselsandbits.items.ItemChisel;
import mod.chiselsandbits.registry.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ChiselPrinterContainer extends Container {

    public static final int PLAYER_INVENTORY_ROWS = 3;
    public static final int PLAYER_INVENTORY_COLUMNS = 9;
    public static final int PLAYER_INVENTORY_X_OFFSET = 8;
    public static final int PLAYER_INVENTORY_Y_OFFSET = 84;
    public static final int SLOT_SIZE = 18;
    public static final int PLAYER_TOOLBAR_Y_OFFSET = 142;

    private final IIntArray stationData;

    private final IItemHandlerModifiable toolHandler;

    public ChiselPrinterContainer(final int id, final PlayerInventory playerInventory) {
        this(
                id,
                playerInventory,
                new ItemStackHandler(1),
                new ItemStackHandler(1),
                new ItemStackHandler(1),
                new IntArray(1));
    }

    public ChiselPrinterContainer(
            final int id,
            final PlayerInventory playerInventory,
            final IItemHandlerModifiable patternHandler,
            final IItemHandlerModifiable toolHandler,
            final IItemHandlerModifiable resultHandler,
            final IIntArray stationData) {
        super(ModContainerTypes.CHISEL_STATION_CONTAINER.get(), id);
        this.stationData = stationData;
        this.toolHandler = toolHandler;

        this.addSlot(new SlotItemHandler(patternHandler, 0, 50, 47) {
            @Override
            public boolean isItemValid(@NotNull final ItemStack stack) {
                return stack.isEmpty() || stack.getItem() instanceof IPatternItem;
            }
        });

        this.addSlot(new SlotItemHandler(toolHandler, 0, 81, 21) {
            @Override
            public boolean isItemValid(@NotNull final ItemStack stack) {
                return stack.getItem() instanceof ItemChisel;
            }
        });

        this.addSlot(new SlotItemHandler(resultHandler, 0, 116, 47) {
            @Override
            public boolean isItemValid(@NotNull final ItemStack stack) {
                return false;
            }
        });

        for (int rowIndex = 0; rowIndex < PLAYER_INVENTORY_ROWS; ++rowIndex) {
            for (int columnIndex = 0; columnIndex < PLAYER_INVENTORY_COLUMNS; ++columnIndex) {
                this.addSlot(new Slot(
                        playerInventory,
                        columnIndex + rowIndex * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS,
                        PLAYER_INVENTORY_X_OFFSET + columnIndex * SLOT_SIZE,
                        PLAYER_INVENTORY_Y_OFFSET + rowIndex * SLOT_SIZE));
            }
        }

        for (int toolbarIndex = 0; toolbarIndex < PLAYER_INVENTORY_COLUMNS; ++toolbarIndex) {
            this.addSlot(new Slot(
                    playerInventory,
                    toolbarIndex,
                    PLAYER_INVENTORY_X_OFFSET + toolbarIndex * SLOT_SIZE,
                    PLAYER_TOOLBAR_Y_OFFSET));
        }

        this.trackIntArray(this.stationData);
    }

    @Override
    public boolean canInteractWith(final PlayerEntity playerIn) {
        return true;
    }

    public int getChiselProgressionScaled() {
        int progress = this.stationData.get(0);
        return progress != 0 ? progress * 16 / 100 : 0;
    }

    public ItemStack getToolStack() {
        return toolHandler.getStackInSlot(0);
    }

    @NotNull
    @Override
    public ItemStack transferStackInSlot(final PlayerEntity playerIn, final int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (itemstack1.getItem() instanceof IPatternItem) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.getItem() instanceof ItemChisel) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
