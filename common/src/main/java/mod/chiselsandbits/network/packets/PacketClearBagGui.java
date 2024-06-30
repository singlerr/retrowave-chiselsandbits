package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketClearBagGui extends ModPacket {
    private ItemStack stack = null;

    public PacketClearBagGui(final PacketBuffer buffer) {
        readPayload(buffer);
    }

    public PacketClearBagGui(final ItemStack inHandItem) {
        stack = inHandItem;
    }

    @Override
    public void server(final ServerPlayerEntity player) {
        if (player.openContainer instanceof BagContainer) {
            ((BagContainer) player.openContainer).clear(stack);
        }
    }

    @Override
    public void getPayload(final PacketBuffer buffer) {
        buffer.writeItemStack(stack);
        // no data...
    }

    @Override
    public void readPayload(final PacketBuffer buffer) {
        stack = buffer.readItemStack();
    }
}
