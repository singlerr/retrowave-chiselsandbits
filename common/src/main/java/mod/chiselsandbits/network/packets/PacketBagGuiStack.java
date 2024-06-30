package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketBagGuiStack extends ModPacket {
    private int index = -1;
    private ItemStack is;

    public PacketBagGuiStack(PacketBuffer buffer) {
        this.readPayload(buffer);
    }

    public PacketBagGuiStack(final int index, final ItemStack is) {
        this.index = index;
        this.is = is;
    }

    @Override
    public void client() {
        final Container cc = ClientSide.instance.getPlayer().openContainer;
        if (cc instanceof BagContainer) {
            ((BagContainer) cc).customSlots.get(index).putStack(is);
        }
    }

    @Override
    public void getPayload(final PacketBuffer buffer) {
        buffer.writeInt(index);

        if (is == null) {
            buffer.writeInt(0);
        } else {
            buffer.writeInt(ModUtil.getStackSize(is));
            buffer.writeInt(ItemChiseledBit.getStackState(is));
        }
    }

    @Override
    public void readPayload(final PacketBuffer buffer) {
        index = buffer.readInt();

        final int size = buffer.readInt();

        if (size <= 0) {
            is = null;
        } else {
            is = ItemChiseledBit.createStack(buffer.readInt(), size, false);
        }
    }
}
