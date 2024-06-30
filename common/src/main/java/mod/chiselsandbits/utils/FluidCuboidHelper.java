package mod.chiselsandbits.utils;

import static net.minecraft.util.Direction.*;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public final class FluidCuboidHelper {

    private FluidCuboidHelper() {
        throw new IllegalStateException("Tried to initialize: FluidCuboidHelper but this is a Utility class.");
    }

    /**
     * Renders a fluid block with offset from the matrices and from x1/y1/z1 to x2/y2/z2 using block model coordinates, so from 0-16
     */
    public static void renderScaledFluidCuboid(
            FluidStack fluid,
            MatrixStack matrices,
            IVertexBuilder renderer,
            int combinedLight,
            final int combinedOverlay,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2) {
        renderFluidCuboid(
                fluid,
                matrices,
                renderer,
                combinedLight,
                combinedOverlay,
                x1 / 16,
                y1 / 16,
                z1 / 16,
                x2 / 16,
                y2 / 16,
                z2 / 16);
    }

    /**
     * Renders a fluid block with offset from the matrices and from x1/y1/z1 to x2/y2/z2 inside the block local coordinates, so from 0-1
     */
    public static void renderFluidCuboid(
            FluidStack fluid,
            MatrixStack matrices,
            IVertexBuilder renderer,
            int combinedLight,
            final int combinedOverlay,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2) {
        int color = fluid.getFluid().getAttributes().getColor(fluid);
        renderFluidCuboid(fluid, matrices, renderer, combinedLight, combinedOverlay, x1, y1, z1, x2, y2, z2, color);
    }

    /**
     * Renders a fluid block with offset from the matrices and from x1/y1/z1 to x2/y2/z2 inside the block local coordinates, so from 0-1
     */
    public static void renderFluidCuboid(
            FluidStack fluid,
            MatrixStack matrices,
            IVertexBuilder renderer,
            int combinedLight,
            final int combinedOverlay,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            int color) {
        TextureAtlasSprite still = Minecraft.getInstance()
                .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                .apply(fluid.getFluid().getAttributes().getStillTexture(fluid));
        TextureAtlasSprite flowing = Minecraft.getInstance()
                .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                .apply(fluid.getFluid().getAttributes().getFlowingTexture(fluid));

        renderFluidCuboid(
                still, flowing, color, matrices, renderer, combinedOverlay, combinedLight, x1, y1, z1, x2, y2, z2);
    }

    public static void renderFluidCuboid(
            TextureAtlasSprite still,
            TextureAtlasSprite flowing,
            int color,
            MatrixStack matrices,
            IVertexBuilder renderer,
            final int combinedOverlay,
            int combinedLight,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2) {
        matrices.push();
        matrices.translate(x1, y1, z1);

        // x/y/z2 - x/y/z1 is because we need the width/height/depth
        putTexturedQuad(
                renderer,
                matrices.getLast(),
                still,
                x2 - x1,
                y2 - y1,
                z2 - z1,
                DOWN,
                color,
                combinedOverlay,
                combinedLight,
                false);
        putTexturedQuad(
                renderer,
                matrices.getLast(),
                flowing,
                x2 - x1,
                y2 - y1,
                z2 - z1,
                Direction.NORTH,
                color,
                combinedOverlay,
                combinedLight,
                true);
        putTexturedQuad(
                renderer,
                matrices.getLast(),
                flowing,
                x2 - x1,
                y2 - y1,
                z2 - z1,
                Direction.EAST,
                color,
                combinedOverlay,
                combinedLight,
                true);
        putTexturedQuad(
                renderer,
                matrices.getLast(),
                flowing,
                x2 - x1,
                y2 - y1,
                z2 - z1,
                Direction.SOUTH,
                color,
                combinedOverlay,
                combinedLight,
                true);
        putTexturedQuad(
                renderer,
                matrices.getLast(),
                flowing,
                x2 - x1,
                y2 - y1,
                z2 - z1,
                Direction.WEST,
                color,
                combinedOverlay,
                combinedLight,
                true);
        putTexturedQuad(
                renderer,
                matrices.getLast(),
                still,
                x2 - x1,
                y2 - y1,
                z2 - z1,
                UP,
                color,
                combinedOverlay,
                combinedLight,
                false);
        matrices.pop();
    }

    public static void putTexturedQuad(
            IVertexBuilder renderer,
            MatrixStack.Entry matrix,
            TextureAtlasSprite sprite,
            float w,
            float h,
            float d,
            Direction face,
            int color,
            final int overlay,
            int brightness,
            boolean flowing) {
        putTexturedQuad(renderer, matrix, sprite, w, h, d, face, color, overlay, brightness, flowing, false, false);
    }

    public static void putTexturedQuad(
            IVertexBuilder renderer,
            MatrixStack.Entry matrix,
            TextureAtlasSprite sprite,
            float w,
            float h,
            float d,
            Direction face,
            int color,
            final int overlay,
            int brightness,
            boolean flowing,
            boolean flipHorizontally,
            boolean flipVertically) {
        int l1 = brightness >> 0x10 & 0xFFFF;
        int l2 = brightness & 0xFFFF;

        int o1 = overlay >> 0x10 & 0xFFFF;
        int o2 = overlay & 0xFFFF;

        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;

        putTexturedQuad(
                renderer,
                matrix,
                sprite,
                w,
                h,
                d,
                face,
                r,
                g,
                b,
                a,
                l1,
                l2,
                o1,
                o2,
                flowing,
                flipHorizontally,
                flipVertically);
    }

    /* Fluid cuboids */
    // x and x+w has to be within [0,1], same for y/h and z/d
    public static void putTexturedQuad(
            IVertexBuilder renderer,
            MatrixStack.Entry matrices,
            TextureAtlasSprite sprite,
            float w,
            float h,
            float d,
            Direction face,
            int r,
            int g,
            int b,
            int a,
            int light1,
            int light2,
            final int overlay1,
            final int overlay2,
            boolean flowing,
            boolean flipHorizontally,
            boolean flipVertically) {
        // safety
        if (sprite == null) {
            return;
        }
        float minU;
        float maxU;
        float minV;
        float maxV;

        double size = 16f;
        if (flowing) {
            size = 8f;
        }

        double xt1 = 0;
        double xt2 = w;
        while (xt2 > 1f) {
            xt2 -= 1f;
        }
        double yt1 = 0;
        double yt2 = h;
        while (yt2 > 1f) {
            yt2 -= 1f;
        }
        double zt1 = 0;
        double zt2 = d;
        while (zt2 > 1f) {
            zt2 -= 1f;
        }

        // flowing stuff should start from the bottom, not from the start
        if (flowing) {
            double tmp = 1d - yt1;
            yt1 = 1d - yt2;
            yt2 = tmp;
        }

        switch (face) {
            case DOWN:
            case UP:
                minU = sprite.getInterpolatedU(xt1 * size);
                maxU = sprite.getInterpolatedU(xt2 * size);
                minV = sprite.getInterpolatedV(zt1 * size);
                maxV = sprite.getInterpolatedV(zt2 * size);
                break;
            case NORTH:
            case SOUTH:
                minU = sprite.getInterpolatedU(xt2 * size);
                maxU = sprite.getInterpolatedU(xt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            case WEST:
            case EAST:
                minU = sprite.getInterpolatedU(zt2 * size);
                maxU = sprite.getInterpolatedU(zt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            default:
                minU = sprite.getMinU();
                maxU = sprite.getMaxU();
                minV = sprite.getMinV();
                maxV = sprite.getMaxV();
        }

        if (flipHorizontally) {
            float tmp = minV;
            minV = maxV;
            maxV = tmp;
        }

        if (flipVertically) {
            float tmp = minU;
            minU = maxU;
            maxU = tmp;
        }

        final Matrix4f worldMatrix = matrices.getMatrix();
        final Matrix3f normalMatrix = matrices.getNormal();

        switch (face) {
            case DOWN:
                renderer.pos(worldMatrix, 0, 0, 0)
                        .color(r, g, b, a)
                        .tex(minU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, DOWN.getXOffset(), DOWN.getYOffset(), DOWN.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, 0, 0)
                        .color(r, g, b, a)
                        .tex(maxU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, DOWN.getXOffset(), DOWN.getYOffset(), DOWN.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, 0, d)
                        .color(r, g, b, a)
                        .tex(maxU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, DOWN.getXOffset(), DOWN.getYOffset(), DOWN.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, 0, 0, d)
                        .color(r, g, b, a)
                        .tex(minU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, DOWN.getXOffset(), DOWN.getYOffset(), DOWN.getZOffset())
                        .endVertex();
                break;
            case UP:
                renderer.pos(worldMatrix, 0, h, 0)
                        .color(r, g, b, a)
                        .tex(minU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, UP.getXOffset(), UP.getYOffset(), UP.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, 0, h, d)
                        .color(r, g, b, a)
                        .tex(minU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, UP.getXOffset(), UP.getYOffset(), UP.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, h, d)
                        .color(r, g, b, a)
                        .tex(maxU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, UP.getXOffset(), UP.getYOffset(), UP.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, h, 0)
                        .color(r, g, b, a)
                        .tex(maxU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, UP.getXOffset(), UP.getYOffset(), UP.getZOffset())
                        .endVertex();
                break;
            case NORTH:
                renderer.pos(worldMatrix, 0, 0, 0)
                        .color(r, g, b, a)
                        .tex(minU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, NORTH.getXOffset(), NORTH.getYOffset(), NORTH.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, 0, h, 0)
                        .color(r, g, b, a)
                        .tex(minU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, NORTH.getXOffset(), NORTH.getYOffset(), NORTH.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, h, 0)
                        .color(r, g, b, a)
                        .tex(maxU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, NORTH.getXOffset(), NORTH.getYOffset(), NORTH.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, 0, 0)
                        .color(r, g, b, a)
                        .tex(maxU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, NORTH.getXOffset(), NORTH.getYOffset(), NORTH.getZOffset())
                        .endVertex();
                break;
            case SOUTH:
                renderer.pos(worldMatrix, 0, 0, d)
                        .color(r, g, b, a)
                        .tex(maxU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, SOUTH.getXOffset(), SOUTH.getYOffset(), SOUTH.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, 0, d)
                        .color(r, g, b, a)
                        .tex(minU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, SOUTH.getXOffset(), SOUTH.getYOffset(), SOUTH.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, h, d)
                        .color(r, g, b, a)
                        .tex(minU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, SOUTH.getXOffset(), SOUTH.getYOffset(), SOUTH.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, 0, h, d)
                        .color(r, g, b, a)
                        .tex(maxU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, SOUTH.getXOffset(), SOUTH.getYOffset(), SOUTH.getZOffset())
                        .endVertex();
                break;
            case WEST:
                renderer.pos(worldMatrix, 0, 0, 0)
                        .color(r, g, b, a)
                        .tex(maxU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, WEST.getXOffset(), WEST.getYOffset(), WEST.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, 0, 0, d)
                        .color(r, g, b, a)
                        .tex(minU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, WEST.getXOffset(), WEST.getYOffset(), WEST.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, 0, h, d)
                        .color(r, g, b, a)
                        .tex(minU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, WEST.getXOffset(), WEST.getYOffset(), WEST.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, 0, h, 0)
                        .color(r, g, b, a)
                        .tex(maxU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, WEST.getXOffset(), WEST.getYOffset(), WEST.getZOffset())
                        .endVertex();
                break;
            case EAST:
                renderer.pos(worldMatrix, w, 0, 0)
                        .color(r, g, b, a)
                        .tex(minU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, EAST.getXOffset(), EAST.getYOffset(), EAST.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, h, 0)
                        .color(r, g, b, a)
                        .tex(minU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, EAST.getXOffset(), EAST.getYOffset(), EAST.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, h, d)
                        .color(r, g, b, a)
                        .tex(maxU, minV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, EAST.getXOffset(), EAST.getYOffset(), EAST.getZOffset())
                        .endVertex();
                renderer.pos(worldMatrix, w, 0, d)
                        .color(r, g, b, a)
                        .tex(maxU, maxV)
                        .overlay(overlay1, overlay2)
                        .lightmap(light1, light2)
                        .normal(normalMatrix, EAST.getXOffset(), EAST.getYOffset(), EAST.getZOffset())
                        .endVertex();
                break;
        }
    }
}
