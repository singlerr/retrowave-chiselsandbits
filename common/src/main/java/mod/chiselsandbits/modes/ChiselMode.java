package mod.chiselsandbits.modes;

import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.LocalStrings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public enum ChiselMode implements IToolMode
{
	SINGLE( LocalStrings.ChiselModeSingle ),
	SNAP2( LocalStrings.ChiselModeSnap2 ),
	SNAP4( LocalStrings.ChiselModeSnap4 ),
	SNAP8( LocalStrings.ChiselModeSnap8 ),
	LINE( LocalStrings.ChiselModeLine ),
	PLANE( LocalStrings.ChiselModePlane ),
	CONNECTED_PLANE( LocalStrings.ChiselModeConnectedPlane ),
	CUBE_SMALL( LocalStrings.ChiselModeCubeSmall ),
	CUBE_MEDIUM( LocalStrings.ChiselModeCubeMedium ),
	CUBE_LARGE( LocalStrings.ChiselModeCubeLarge ),
	SAME_MATERIAL( LocalStrings.ChiselModeSameMaterial ),
	DRAWN_REGION( LocalStrings.ChiselModeDrawnRegion ),
	CONNECTED_MATERIAL( LocalStrings.ChiselModeConnectedMaterial );

	public final LocalStrings string;

	public boolean isDisabled = false;

	public Object binding;

	private ChiselMode(
			final LocalStrings str )
	{
		string = str;
	}

	public static ChiselMode getMode(
			final ItemStack stack )
	{
		if ( stack != null )
		{
			try
			{
				final NBTTagCompound nbt = stack.getTagCompound();
				if ( nbt != null && nbt.hasKey( "mode" ) )
				{
					return valueOf( nbt.getString( "mode" ) );
				}
			}
			catch ( final IllegalArgumentException iae )
			{
				// nope!
			}
			catch ( final Exception e )
			{
				Log.logError( "Unable to determine mode.", e );
			}
		}

		return SINGLE;
	}

	@Override
	public void setMode(
			final ItemStack stack )
	{
		if ( stack != null )
		{
			stack.setTagInfo( "mode", new NBTTagString( name() ) );
		}
	}

	@Override
	public LocalStrings getName()
	{
		return string;
	}

	@Override
	public boolean isDisabled()
	{
		return isDisabled;
	}

	public static ChiselMode castMode(
			final IToolMode chiselMode )
	{
		if ( chiselMode instanceof ChiselMode )
		{
			return (ChiselMode) chiselMode;
		}

		return ChiselMode.SINGLE;
	}

}
