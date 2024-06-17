package mod.chiselsandbits.client;

import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class ItemColorBitBag implements IItemColor
{

	@Override
	public int getColorFromItemstack(
			ItemStack stack,
			int tintIndex )
	{
		if ( tintIndex == 1 )
		{
			EnumDyeColor color = ChiselsAndBits.getItems().itemBitBag.getDyedColor( stack );
			if ( color != null )
				return color.func_193350_e();
		}

		return -1;
	}

}
