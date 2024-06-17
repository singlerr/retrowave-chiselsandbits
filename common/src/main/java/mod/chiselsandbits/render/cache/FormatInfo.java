package mod.chiselsandbits.render.cache;

import net.minecraft.client.renderer.vertex.VertexFormat;

public class FormatInfo
{

	final int totalSize;
	final int faceSize;

	final int[] offsets;
	final int[] indexLengths;
	final int[] finalLengths;

	public FormatInfo(
			final VertexFormat format )
	{
		int total = 0;
		indexLengths = new int[format.getElementCount()];
		finalLengths = new int[format.getElementCount()];
		offsets = new int[format.getElementCount()];

		for ( int x = 0; x < indexLengths.length; ++x )
		{
			finalLengths[x] = format.getElement( x ).getElementCount();
			indexLengths[x] = finalLengths[x];

			switch ( format.getElement( x ).getUsage() )
			{
				default:
				case GENERIC:
				case PADDING:
					indexLengths[x] = 0;
					break;

				case COLOR:
					indexLengths[x] = 4;
					break;

				case NORMAL:
					indexLengths[x] = 3;
					break;

				case POSITION:
					indexLengths[x] = 3;
					break;

				case UV:
					indexLengths[x] = 2;
					break;
			}

			offsets[x] = total;
			total += indexLengths[x];
		}

		this.totalSize = total;
		this.faceSize = total * 4;
	}

	public int[] pack(
			float[][][] unpackedData )
	{
		int[] out = new int[this.faceSize];

		int offset = 0;
		for ( int f = 0; f < 4; ++f )
		{
			float[][] run2 = unpackedData[f];
			for ( int x = 0; x < indexLengths.length; ++x )
			{
				float[] run = run2[x];
				for ( int z = 0; z < indexLengths[x]; z++ )
				{
					if ( run.length > z )
						out[offset++] = Float.floatToRawIntBits( run[z] );
					else
						out[offset++] = 0;
				}
			}
		}

		return out;
	}

	public float[] unpack(
			int[] raw,
			int vertex,
			int index )
	{
		int size = indexLengths[index];
		float[] out = new float[size];
		int start = vertex * this.totalSize + offsets[index];

		for ( int x = 0; x < size; x++ )
		{
			out[x] = Float.intBitsToFloat( raw[start + x] );
		}

		return out;
	}

}
