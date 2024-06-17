package mod.chiselsandbits.helpers;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class IItemHandlerSlot implements IItemInInventory
{

	private IItemHandler internal;
	private int zz;
	private @Nonnull ItemStack stack; // copy of itemstack
	private final @Nonnull ItemStack originalStack;
	private ActingPlayer src;
	private boolean isEditable;

	public IItemHandlerSlot(
			IItemHandler internal,
			int zz,
			ItemStack which,
			ActingPlayer src,
			boolean canEdit )
	{
		this.internal = internal;
		this.zz = zz;
		this.stack = ModUtil.copy( which );
		this.originalStack = ModUtil.copy( which );
		this.src = src;
		this.isEditable = canEdit;
	}

	@Override
	public boolean isValid()
	{
		return isEditable && ( src.isCreative() || !ModUtil.isEmpty( stack ) && ModUtil.getStackSize( stack ) > 0 );
	}

	@Override
	public void damage(
			ActingPlayer who )
	{
		throw new RuntimeException( "Cannot damage an item in an inventory?" );
	}

	@Override
	public boolean consume()
	{
		ItemStack is = internal.extractItem( zz, 1, true );
		if ( is != null && ItemStack.areItemStackTagsEqual( is, stack ) && ItemStack.areItemStackTagsEqual( is, stack ) )
		{
			internal.extractItem( zz, 1, false );
			ModUtil.adjustStackSize( stack, -1 );
			return true;
		}

		return false;
	}

	@Override
	public ItemStack getStack()
	{
		return stack;
	}

	@Override
	public void swapWithWeapon()
	{
		throw new RuntimeException( "Cannot swap an item in an inventory?" );
	}

	@Override
	public ItemStack getStackType()
	{
		return originalStack;
	}

}
