package mod.chiselsandbits.registry;

import lombok.experimental.UtilityClass;
import mod.chiselsandbits.ModBootstrap;
import mod.chiselsandbits.item.ChiseledBlockItem;
import mod.chiselsandbits.material.LegacyMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

@UtilityClass
public class ModItemRegistries {

    public void initialize() {
        for (String material : LegacyMaterials.LEGACY_MATERIALS) {
            ModRegistrars.MATERIAL_TO_ITEM_CONVERSIONS.put(
                    material,
                    ModRegistrars.ITEM_REGISTRAR.register(
                            new ResourceLocation(ModBootstrap.MOD_ID, "chiseled_%s".formatted(material)),
                            () -> new ChiseledBlockItem(
                                    ModRegistrars.MATERIAL_TO_BLOCK_CONVERSIONS
                                            .get(material)
                                            .get(),
                                    new Item.Properties())));
        }
    }
}
