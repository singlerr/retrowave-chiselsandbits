package mod.chiselsandbits.client.model.baked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.util.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBakedItemModel extends BaseBakedPerspectiveModel implements IBakedModel {
    protected ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();

    @Override
    public final boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public final boolean isGui3d() {
        return true;
    }

    @Override
    public final boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable final BlockState state, @Nullable final Direction side, final Random rand) {
        if (side != null) {
            return Collections.emptyList();
        }

        return list;
    }

    @Override
    public final ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }
}
