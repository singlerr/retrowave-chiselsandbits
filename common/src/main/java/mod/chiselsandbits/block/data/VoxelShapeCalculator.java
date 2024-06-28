package mod.chiselsandbits.block.data;

import java.util.Collection;
import mod.chiselsandbits.api.BoxType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

/**
 * Calculates the block shape of a VoxelBlob.
 *
 * Thanks to Daniel from C&B2.
 */
public class VoxelShapeCalculator {
    /**
     * Calculates both the selection shape and the collision shape for a voxel blob.
     */
    public static VoxelShape calculate(final VoxelBlob blob, final BoxType type) {
        final VoxelBlobStateReference reference = new VoxelBlobStateReference(blob, 0L);
        return calculateFromBB(reference.getBoxes(type));
    }

    private static VoxelShape calculateFromBB(final Collection<AxisAlignedBB> bbList) {
        return bbList.stream()
                .reduce(
                        VoxelShapes.empty(),
                        (voxelShape, axisAlignedBB) -> {
                            final VoxelShape bbShape = VoxelShapes.create(axisAlignedBB);
                            return VoxelShapes.combine(voxelShape, bbShape, IBooleanFunction.OR);
                        },
                        (voxelShape, voxelShape2) -> VoxelShapes.combine(voxelShape, voxelShape2, IBooleanFunction.OR))
                .simplify();
    }

    private static VoxelShape calculateFromBlob(final VoxelBlob blob) {
        VoxelShape collisionShape = VoxelShapes.empty();
        int x1 = 15, y1 = 15, z1 = 15, x2 = 0, y2 = 0, z2 = 0;

        // For every x/z coordinate we build shapes from the bottom up
        double bitDimension = 1 / 16.0d;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double dx = x / 16.0d;
                double dz = z / 16.0d;

                int start = -1;
                for (int y = 0; y < 16; y++) {
                    // If this bit isn't air we start the shape here
                    if (blob.get(x, y, z) != 0) {
                        if (start == -1) {
                            start = y;
                            if (y < y1) y1 = y;
                        }
                    } else if (start != -1) {
                        // If this is air and we were working on a box we end it
                        collisionShape = VoxelShapes.or(
                                collisionShape,
                                VoxelShapes.create(
                                        dx, start / 16.0d, dz, dx + bitDimension, y / 16.0d, dz + bitDimension));
                        start = -1;
                        if (x < x1) x1 = x;
                        if (z < z1) z1 = z;
                        if (x > x2) x2 = x;
                        if (y > y2) y2 = y - 1;
                        if (z > z2) z2 = z;
                    }
                }
                if (start != -1) {
                    // If we ended with a box we add that too
                    collisionShape = VoxelShapes.or(
                            collisionShape,
                            VoxelShapes.create(dx, start / 16.0d, dz, dx + bitDimension, 1.0d, dz + bitDimension));
                    y2 = 15;
                    if (x < x1) x1 = x;
                    if (z < z1) z1 = z;
                    if (x > x2) x2 = x;
                    if (z > z2) z2 = z;
                }
            }
        }

        // Determine selection shape by taking the bounds
        return collisionShape;
    }
}
