package mod.chiselsandbits.network.packets;

import java.io.IOException;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketClearBagGui extends ModPacket
{
	ItemStack stack = null;

	public PacketClearBagGui()
	{
	}

	public PacketClearBagGui(
			final ItemStack inHandItem )
	{
		stack = inHandItem;
	}

	@Override
	public void server(
			final EntityPlayerMP player )
	{
		if ( player.openContainer instanceof BagContainer )
		{
			( (BagContainer) player.openContainer ).clear( stack );
		}
	}

	@Override
	public void getPayload(
			final PacketBuffer buffer )
	{
		buffer.writeItemStackToBuffer( stack );
		// no data...
	}

	@Override
	public void readPayload(
			final PacketBuffer buffer )
	{
		try
		{
			stack = buffer.readItemStackFromBuffer();
		}
		catch ( final IOException e )
		{
			stack = null;
		}
	}

}
